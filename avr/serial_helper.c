#ifdef UNDER_TEST
    //Stubs
    static void loop_until_bit_is_set(int a, int b){ };
    static int dummy;
    #define UDR0 dummy
    #define UCSR0A 0
    #define UDRE0 0
    #define RXC0 0
#else
#include <avr/io.h>
#endif

#include <stdint.h>
#include <stdlib.h>
#include "serial_helper.h"

void uart_putchar(char c) {
    loop_until_bit_is_set(UCSR0A, UDRE0); // Wait until data register empty. 
    UDR0 = c;
} 

char uart_getchar() {
    loop_until_bit_is_set(UCSR0A, RXC0); /* Wait until data exists. */
    return UDR0;
}

/* This function is inpsired by fgets. It reads up to size bytes into the buffer. Returns 
 * true when a newline is found. The newline is replaced with a zero terminator.
 * If the buffer is exhausted and no newline is found, replaces false. The buffer
 * will contain partial data */
int my_fgets(char * buf, int size){

#ifdef FAKE_SER_INPUT
    static int first = 1;
    if(!first) return 0;
    char * in = "s:1000:100000";
    while(*in){
        *buf = *in;
        buf++;
        in++;
    }
    first = 0;
    return 1;
#endif


    int i = 0;
    while(i < size){
        buf[i] = uart_getchar();
        if(buf[i] == '\n') {
            buf[i] = 0;
            return 1;
        }
        i++;
    }
    return 0;
}

void my_puts(char *buf){
    while(*buf) {
        uart_putchar(*buf);
        buf++;
    }
}

void my_puti(int i){
    char buf[32];
    itoa(i, buf, 10);
    my_puts(buf);
}

void my_putl(long i){
    char buf[32];
    ltoa(i, buf, 10);
    my_puts(buf);
}

void my_putc(char a){
    uart_putchar(a);
}

/* Parses a string in the form of s:0:0. Returns number of matched entries. False (0) on error */
int parse_serial_line(char * line, char * out_a, int * out_b, long * out_c){
    *out_a = line[0];
    if(line[1] != ':'){
        return 1;
    }
    char * current = &line[2];
    *out_b = strtol(current, &current, 10);
    if(current[0] != ':'){
        return 2;
    }
    current++;
    *out_c = strtol(current, &current, 10);
    return 3;
}
