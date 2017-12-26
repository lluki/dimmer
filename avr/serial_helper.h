#ifndef SERIAL_HELPER_H
#define SERIAL_HELPER_H

int parse_serial_line(char * line, char * out_a, int * out_b, int * out_c);
int my_fgets(char * buf, int size);
void my_puti(int i);
void my_putc(char a);
void my_puts(char *buf);
char uart_getchar();
void uart_putchar(char c);

#endif
