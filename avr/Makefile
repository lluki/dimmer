CC=avr-gcc
OBJCOPY=avr-objcopy
MCU=atmega48
F_CPU=12000000UL

CDEFS=-DF_CPU=${F_CPU} -DMCU=\"${MCU}\"
CFLAGS=-Os -Wall -gdwarf-2 ${CDEFS} -mmcu=${MCU}
PORT=/dev/ttyACM0

dimmer.hex: dimmer.elf
	${OBJCOPY} -O ihex -R .eeprom dimmer.elf dimmer.hex

dimmer.elf: dimmer.axf
	${CC} -o dimmer.elf dimmer.axf
	avr-size dimmer.elf

dimmer.axf: dimmer.c serial_helper.c serial_helper.h
	${CC} ${CFLAGS} dimmer.c serial_helper.c -o dimmer.axf

clean:
	rm -f *.hex *.o *.elf *.axf sim gtkwave_output.vcd

serial_helper_test: serial_helper.c serial_helper.h serial_helper_test.c
	gcc -Wall -DUNDER_TEST serial_helper.c serial_helper_test.c -o serial_helper_test


install: dimmer.hex
	# on my usbasp, the connector must point towards the MC.. 
	# Use mains to power the chip
	avrdude -F -V -c usbasp -p ATMEGA48 -P ${PORT} -b 115200 -U flash:w:dimmer.hex


#Simulator stuff 

sim: sim.c dimmer.axf
	gcc -Wall ${CDEFS} sim.c ../simavr/examples/parts/ac_input.c -I../simavr/simavr/sim/ -I../simavr/examples/parts -L../simavr/simavr/obj-x86_64-linux-gnu -lsimavr -lelf -o sim

run_sim: sim
	LD_LIBRARY_PATH=../simavr/simavr/obj-x86_64-linux-gnu ./sim
