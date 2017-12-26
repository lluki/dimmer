# AVR Code for dimmer

## Serial Settings
9600 Baud, 8N1

## Serial Format

### Read
To request the current state, send the string "r\n". The reply looks like "r:" followed by a decimal integer in the range of 0-1000 and a newline. 

### Set
To set the current dimmer value, use
s:VAL:TIME
Where val is the destination brightness in decimal, range 0-1000 and TIME is the current fading delay in msec

### Other
Any other line will be ignored
