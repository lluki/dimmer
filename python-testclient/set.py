#!/usr/bin/python

import json, sys, time, argparse
import paho.mqtt.client as mqtt
from conf import get_conf

parser = argparse.ArgumentParser()
parser.add_argument("-w", "--when", help="when?", choices=["now","alarm"],default="now")
parser.add_argument("-v", "--val", help="value to set on dimmer (0-1000)", default=1000)
parser.add_argument("-d", "--delay", help="delay until value is reached (ms)", default=20)
parser.add_argument("-c", "--conf", help="config file", default="/etc/mqtt-relay.conf")
args = parser.parse_args()

config = get_conf()


def on_connect(client, userdata, flags, rc):
    print("Connected with result code "+str(rc))

client = mqtt.Client()
client.on_connect = on_connect

client.username_pw_set(config["username"],config["password"])
client.connect(config["server"], config["port"], 60)

# Blocking call that processes network traffic, dispatches callbacks and
# handles reconnecting.
# Other loop*() functions are available that give a threaded interface and a
# manual interface.

if args.when == "now":
    client.publish("light/set", payload=json.dumps({
        'val': args.val,
        'delay': args.delay
    }))
elif args.when == "alarm":
    alarm_time = int(time.time() - time.timezone) + 60 * 2
    print "Setting alarm ts: " + str(alarm_time)
    client.publish("light/alarm/set", payload=json.dumps({
        'alarm_time': alarm_time,
        'val': args.val,
        'delay':args.delay 
    }))
    
#client.loop_forever()
