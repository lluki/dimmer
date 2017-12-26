#!/usr/bin/python

### 
#
# Emulate the behaviour of a dimmer
#
###

import argparse
import json
import paho.mqtt.client as mqtt
from conf import get_conf_from_file


parser = argparse.ArgumentParser()
parser.add_argument("-p", "--prefix", help="the device prefix", default="dimmer")
parser.add_argument("-c", "--conf", help="config file", default="/etc/mqtt-relay.conf")
args = parser.parse_args()
prefix = args.prefix

config = get_conf_from_file(args.conf)

# device state
brightness = 0
alarm = 0

def on_connect(client, userdata, flags, rc):
    print("Connected with result code "+str(rc))

    # Subscribing in on_connect() means that if we lose the connection and
    # reconnect then subscriptions will be renewed.
    client.subscribe(prefix + "/#")

# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
    print(msg.topic+": "+str(msg.payload))
    if msg.topic.endswith("brightness/set"):
        brightness = int(msg.payload)
        print("Setting brightness to: " + str(brightness))
        client.publish(prefix + "/brightness", brightness)
    
    if msg.topic.endswith("alarm/set"):
        alarm = int(msg.payload)
        print("Setting alarm to: " + str(alarm))
        client.publish(prefix + "/alarm", alarm)

def on_disconnect():
    print "On disconnect.."

client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message
client.on_disconnect = on_disconnect

client.username_pw_set(config["username"],config["password"])
client.connect(config["server"], config["port"], 60)

# Blocking call that processes network traffic, dispatches callbacks and
# handles reconnecting.
# Other loop*() functions are available that give a threaded interface and a
# manual interface.

client.publish(prefix + "/status", "online")
client.will_set(prefix + "/status", "offline", 0, False)
client.loop_forever()
