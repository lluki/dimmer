#include <avr/io.h>
#include <avr/sleep.h>
#include <avr/interrupt.h>
#include <util/delay.h>
#define BAUD 9600
#include <util/setbaud.h>
#include <stdint.h>
#include <inttypes.h>

#include "serial_helper.h"

// Berechnungen
#define UBRR_VAL ((F_CPU+BAUD*8)/(BAUD*16)-1)   // clever runden
#define BAUD_REAL (F_CPU/(16*(UBRR_VAL+1)))     // Reale Baudrate
#define BAUD_ERROR ((BAUD_REAL*1000)/BAUD) // Fehler in Promille, 1000 = kein Fehler.
 
#if ((BAUD_ERROR<990) || (BAUD_ERROR>1010))
  #error Systematischer Fehler der Baudrate grösser 1% und damit zu hoch! 
#endif

// zero pass input PD2
// trigger PD6
// PC0, PC1  Debug leds


// At every zero crossing, delta is added to the value until it reaches dest
static volatile float timer_match_value = 16000;
static volatile float timer_match_value_dest = 16000;
static volatile float timer_match_value_delta = 0;

ISR(INT0_vect) {
    TCNT1 = 0;           // Reset timer
    PORTD &= ~(1 << 6);  // Set trigger low
    PORTC &= ~(1 << 1);  // Set debug led low
    //PORTC = 1 | 1<<1;  // Set trigger low
    if(timer_match_value != timer_match_value_dest){
        timer_match_value += timer_match_value_delta;
        if( (timer_match_value_delta > 0 && timer_match_value > timer_match_value_dest) ||
            (timer_match_value_delta < 0 && timer_match_value < timer_match_value_dest) ){
            // gone too far
            timer_match_value = timer_match_value_dest;
        }
    }
    if(timer_match_value < 16000) {
        TIMSK1 |= 1 << OCIE1A;               // Enable timer1 cmpa interrupt
        TIFR1 = 1 << OCF1B | 1 << OCF1A;    // Reset any pending interrupts
        OCR1A = timer_match_value;
    } else {
        TIMSK1 &= ~(1 << OCIE1A);            // Disable timer int.
    }
    TCNT1 = 0;           // Reset timer
}

ISR(TIMER1_COMPA_vect) {
    PORTD |= 1 << 6; // Set trigger high
    PORTC |= 1 << 1; // Set debug led on
    //PORTC = 0 | 1<<1;  
}

ISR(TIMER1_COMPB_vect) {
    PORTD &= ~(1 << 6);  // Set trigger low
}

static void handle_serial_input(){
    char buf[20];
    char c;
    int val; 
    int time;
    if(my_fgets(buf, sizeof(buf))){
        PORTC ^= 1;
        //my_puts(buf);
        //my_puts("\n");
        int scan_res = parse_serial_line(buf, &c, &val, &time);
        //my_puts("r:");
        //my_puti(scan_res);
        //my_puts(":");
        //my_putc(c);
        //my_puts(":");
        //my_puti(val);
        //my_puts(":");
        //my_puti(time);
        //my_puts("\n");
        if(scan_res == 3 && c == 's'){
            //disable interrupts while updating
            cli();
            // transform val into the timer domain.
            // 0=off -> val=15000
            // 1000=full on -> val ~= 0 (actually 15...)
            if(val > 1000) val = 1000;
            if(val < 0) val = 0;
            val = (1000 - val) * 16;
            if(val == 0) val = 1;
            if(time == 0){
                timer_match_value = val;
                timer_match_value_dest = val;
                timer_match_value_delta = 0;
            } else {
                timer_match_value_dest = val;
                // We get our time in miliseconds, our timer fires every 100th second. hence *10
                timer_match_value_delta = ((float)(timer_match_value_dest - timer_match_value))*10/((float)time);
            }
            sei();
        }
        //if(scan_res == 1 && c=='r'){
        //   printf("r:%" PRIu32 "\n", (uint32_t)timer_match_value);
        //}
    }
}

int main() {
    // Setup outputs
    DDRC = 1 | 1<<1;               //Debug LEDs
    DDRD = 1 << 6;                  //Trigger
    PORTC = 0;

    // Setup timer. Timer counts to 15000 every 100th second.
    TCCR1B = 1 << CS11; // prescale = 8
    OCR1A = timer_match_value;
    OCR1B = 15000; // trigger off counter
    TIMSK1 = 1 << OCIE1A | 1 << OCIE1B;               // Enable timer1 cmpa interrupt

    // Setup external interupts
    EIMSK = 1 << INT0;
	EICRA = 0 << ISC01 | 1 << ISC00;	// Trigger INT0 on any change

    // Setup USART
    UBRR0 = UBRR_VALUE;
    UCSR0B |= (1<<TXEN0) | (1<<RXEN0);
    UCSR0C = (1<<UCSZ01)|(1<<UCSZ00);   // Frame Format: Asynchron 8N1

    sei();
    while(1){
        handle_serial_input();
    }

    return 0;
}
