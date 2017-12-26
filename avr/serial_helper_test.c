#include <stdio.h>
#include <assert.h>
#include "serial_helper.h"

int main(){
    char * a = "s:1:2";
    char out_a;
    int out_b, out_c, res;
    res = parse_serial_line(a, &out_a, &out_b, &out_c);
    assert(res == 3);
    assert(out_a == 's');
    assert(out_b == 1);
    assert(out_c == 2);

    printf("Success\n");
}
