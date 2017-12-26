# General MQTT Topics
## announce
announce is a generic topic for monitoring purposes. Payload should consists of
human readble messages and no automatic processing of events is intended

# Device specific topics

Each device has a prefix. Apart from general topics, it publishes messages only
on topics that start with the prefix. An example prefix is dimmer0.

## Variables
Each device may contain multiple state variables, such as the current dimming
level. On the change of a variable, the device publishes a message on

> prefix/variable

where the payload represents the new value of the variable. When a client wishes
to change the variable, it publishes a message on the topic

> prefix/variable/set

with the desired value as payload. The message is then reflected on the variable
topic, when the device has accepted the change.

# Dimmer device

The dimmer controls a single channel AC mains dimmed lamp.
## Variable: brightness 
Read write. The brightness can be setdirectly using the this variable.

## Variable: alarm
The alarm variable can be set to set an alarm time. The light will dim on
according to the time. The variable contains the time as UNIX timestamp
integer value. If the variable is set to zero, the alarm is disabled.
