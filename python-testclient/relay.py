#!/usr/bin/python

### 
#
# This service implements the business logic. Right now, it liistens to events
# from android devices and forwards alarm set events to light/alarm/set
#
###

import json
import paho.mqtt.client as mqtt
from conf import get_conf

config = get_conf()

def on_connect(client, userdata, flags, rc):
    print("Connected with result code "+str(rc))

    # Subscribing in on_connect() means that if we lose the connection and
    # reconnect then subscriptions will be renewed.
    client.subscribe("#")

# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
    print(msg.topic+": "+str(msg.payload))
    if msg.topic == "/lg/alarm":
        print("  relaying to light/alarm/set")
        payload = json.dumps({
            'alarm_time': msg.payload,
            'val': 100,
            'delay': 1000})
        print("  payload=%s" % payload)
        client.publish("light/alarm/set", payload=payload)


client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

client.username_pw_set(config["username"],config["password"])
client.connect(config["server"], config["port"], 60)

# Blocking call that processes network traffic, dispatches callbacks and
# handles reconnecting.
# Other loop*() functions are available that give a threaded interface and a
# manual interface.

client.loop_forever()
