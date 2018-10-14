#!/usr/bin/python

### 
#
# This service implements the business logic. Right now, it listens to events
# from android devices and forwards alarm set events to light/alarm/set
#
###

import logging, logging.handlers
import json, time 
import paho.mqtt.client as mqtt
from conf import get_conf

SNOOZE_DURATION_S = 5*60   # (Max) time between two snoozed alarms
LIGHT_ADVANCE_S = 10*60    # How much in advance will the light start
ALARM_CLEAR_TIMEOUT_S = 60 # How long does it take to press the snooze button
LIGHT_OFF_TIMEOUT_S = 30*60   # Time to wait until light is turned of after alarm is over

class On(object):
    def __init__(self, ts, fun):
        self.active = True
        self.ts = ts
        self.fun = fun
        ons.append(self)

    def cancel(self):
        self.active = False

    # Returns true if done
    def on_time(self, ts):
        if self.ts < ts:
            if self.active:
                self.fun()
            return True
        return False

ons = []

def check_ons(ts):
    global ons
    ons = [x for x in ons if not x.on_time(ts)]


class Handler(object):
    def __init__(self, deviceid):
        self.deviceid = deviceid
        self.prefix = "/" + deviceid

    def on_message(self, msg):
        pass

class DimmerHandler(Handler):
    def __init__(self, deviceid):
        super(DimmerHandler, self).__init__(deviceid)
        self.current_val = 0 

    def on_message(self, msg):
        if msg.topic.endswith(self.deviceid + "/set"):
            self.current_val = json.loads(msg.payload)["val"]
        if msg.topic.endswith("/button1"):
            if self.current_val <= 333:
                next_val = 500
            elif self.current_val <= 666:
                next_val = 1000
            else:
                next_val = 0
            payload = json.dumps({
                'val': next_val,
                'delay': 1000})
            client.publish(self.deviceid + "/set", payload=payload, retain=True)


class PhoneHandler(Handler):
    def __init__(self, deviceid):
        super(PhoneHandler, self).__init__(deviceid)
        self.clear_alarm_on = On(0, lambda: 1)
        self.light_off_on = On(0, lambda: 1)
        self.last_alarm_time = 0

    def light_off(self):
        logger.debug("Turning light off...")
        payload = json.dumps({
            'val': 0,
            'delay': 1000})
        client.publish("light/set", payload=payload, retain=True)

    def clear_alarm(self):
        logger.debug("Clearing alarm...")
        self.last_alarm_time = 0
        payload = json.dumps({
            'alarm_time': 0,
            'val': 1000,
            'delay': 100000})
        client.publish("light/alarm/set", payload=payload, retain=True)

        logger.debug("Will turn off light in %d secs...", LIGHT_OFF_TIMEOUT_S)
        now = time.time()
        self.light_off_on.cancel()
        self.light_off_on = On(now + LIGHT_OFF_TIMEOUT_S, self.light_off) 

    def on_message_alarm_set(self, msg):
        alarm_time = int(msg.payload)
        now = time.time()

        is_snooze = alarm_time - self.last_alarm_time < SNOOZE_DURATION_S

        if alarm_time == 0:
            logger.debug("Delaying alarm clearing...")
            self.clear_alarm_on = On(now + ALARM_CLEAR_TIMEOUT_S, self.clear_alarm) 
        elif is_snooze:
            logger.debug("Snooze detected, not rearming alarm")
            self.clear_alarm_on.cancel()
            self.last_alarm_time = alarm_time
        else:
            logger.debug("Arming alarm")
            self.clear_alarm_on.cancel()
            self.last_alarm_time = alarm_time
            payload = json.dumps({
                'alarm_time': alarm_time - LIGHT_ADVANCE_S,
                'val': 1000,
                'delay': 100000})
            client.publish("light/alarm/set", payload=payload, retain=True)

    def on_message(self, msg):
        if msg.topic.endswith("alarm"):
            self.on_message_alarm_set(msg)

# Setup logging
def setup_logger():
    logger = logging.getLogger()
    logger.setLevel(logging.DEBUG)
    logger.addHandler(logging.StreamHandler())
    if config.has_key("relay-logfile"):
        handler = logging.handlers.RotatingFileHandler(
                    config["relay-logfile"], maxBytes=1048576, backupCount=7)
        formatter = logging.Formatter("%(asctime)s [%(levelname)s] %(message)s")
        handler.setFormatter(formatter)
        logger.addHandler(handler)
    return logger


config = get_conf()
client = mqtt.Client()
handlers = [DimmerHandler("light"), PhoneHandler("lg")]
logger = setup_logger()


def on_connect(client, userdata, flags, rc):
    logger.debug("Connected with result code "+str(rc))
    client.subscribe("#")

def on_message(client, userdata, msg):
    if msg.topic[0] != '/':
        msg.topic = '/' + msg.topic
    logger.debug(msg.topic+": "+str(msg.payload))
    for handler in handlers:
        if msg.topic.startswith(handler.prefix):
            handler.on_message(msg)


client.on_connect = on_connect
client.on_message = on_message

client.username_pw_set(config["username"], config["password"])
client.connect(config["server"], config["port"], 60)

logger.debug("Relay starting!")

while True:
    check_ons(time.time())
    client.loop(1)

