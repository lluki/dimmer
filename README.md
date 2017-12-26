Android integrated wake up light.

Sub-directories contain:

## Alarm2Mqtt
The android part. Captures android broadcast and forwards it to mqtt server

## Relay
Implements the business logic. Forwards the messages received from android
clients to dimmer clients

## NodeMCU
The software running on nodemcu (written using arduino) conects wifi,
listens to set or set-alarm messages and writes it using serial to a
dimmer running on an AVR mega48 microcontroller.

## AVR
Receives commands from the nodemcu module and dims the 220V incandescent light.
Hardware is heaviliy inspired from the Semitone Crystal dimmer.
[[http://www.engbedded.com/semitone/crystal]]
