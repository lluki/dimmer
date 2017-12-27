#include <stdlib.h>
#include <stdio.h>
#include <libgen.h>

#include "sim_avr.h"
#include "avr_ioport.h"
#include "avr_uart.h"
#include "sim_elf.h"
#include "sim_gdb.h"
#include "sim_vcd_file.h"
#include "ac_input.h"


static void uart_in_hook(struct avr_irq_t * irq, uint32_t value, void * param)
{
	//uart_udp_t * p = (uart_udp_t*)param;
	fprintf(stderr, "%c", value);
	//uart_udp_fifo_write(&p->in, value);
}


static void setup_uart(avr_t * avr){
    static char uart = '0';
    avr_irq_t * src = avr_io_getirq(avr, AVR_IOCTL_UART_GETIRQ(uart), UART_IRQ_OUTPUT);
    avr_irq_register_notify(src, uart_in_hook, NULL);
}

static void uart_putc(avr_t * avr, char c){
            static char uart = '0';
            avr_irq_t * uo
                = avr_io_getirq(avr, AVR_IOCTL_UART_GETIRQ(uart), UART_IRQ_INPUT);

            avr_raise_irq(uo, c);
} 

static void uart_puts(avr_t * avr, char * str){
    while(*str != 0) uart_putc(avr, *str++);
}


int main(int argc, char *argv[]) {
    avr_t * avr = NULL;
    avr_vcd_t vcd_file;
    elf_firmware_t f;
	const char * fname =  "dimmer.axf";
	elf_read_firmware(fname, &f);

	printf("firmware %s f=%lu mmcu=%s\n", fname, F_CPU, MCU);

	avr = avr_make_mcu_by_name(MCU);
	if (!avr) {
		fprintf(stderr, "%s: AVR '%s' not known\n", argv[0], f.mmcu);
		exit(1);
	}
    avr->frequency = F_CPU;
	avr_init(avr);
	avr_load_firmware(avr, &f);
    avr->frequency = F_CPU;
    printf("Fq: %u\n", avr->frequency);

    ac_input_t ac_input;
    ac_input_init(avr, &ac_input);
    avr_connect_irq(ac_input.irq + IRQ_AC_OUT,
        avr_io_getirq(avr, AVR_IOCTL_IOPORT_GETIRQ('D'), 2));

    avr->gdb_port = 1234;
    
    setup_uart(avr); 

    // wave output
    avr_vcd_init(avr, "gtkwave_output.vcd", &vcd_file, 100000 /* usec */);
	avr_vcd_add_signal(&vcd_file, 
		avr_io_getirq(avr, AVR_IOCTL_IOPORT_GETIRQ('D'), IOPORT_IRQ_PIN_ALL), 8 /* bits */ ,
		"portd" );

	avr_vcd_add_signal(&vcd_file, 
		avr_io_getirq(avr, AVR_IOCTL_IOPORT_GETIRQ('C'), IOPORT_IRQ_PIN_ALL), 8 /* bits */ ,
		"portc" );


    int a = F_CPU/10;
    printf("Testcase1\n");
    printf("Starting simulation for %d cycles\n", a);
    avr_vcd_start(&vcd_file);
    while(a--){
        avr_run(avr);
        if(a % 10000 == 0) printf("%d\n", a);
        //if(a == F_CPU / 2){
        //    uart_puts(avr, "hallo\n");
        //}
    }
    avr_vcd_stop(&vcd_file);
    printf("Sim done\n");
    return 0;

}
