

/*
 MQTT -> SERIAL
 * Install arduino libs:
 *  pubsubclient
 *  arduinojson
*/

#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#define ARDUINOJSON_USE_LONG_LONG 1 
#include <ArduinoJson.h>
#include <WiFiManager.h>
#include <time.h>

#include "DimLight.h"
#include "DimAlarm.h"
#include "MqttConstants.h"

#define DEBUG 1
#define debug_print(arg) \
            do { if (DEBUG) Serial.print(arg); } while (0)
#define debug_println(arg) \
            do { if (DEBUG) Serial.println(arg); } while (0)

// Update these with values suitable for your network.

const char* mqtt_set_topic = "light/set";
const char* mqtt_alarm_topic = "light/alarm/set";

WiFiClient espClient;
PubSubClient client(espClient);
DimAlarm dimAlarm;


void callback_light_set(byte* payload, unsigned int length){
  DynamicJsonBuffer jsonBuffer;
  JsonObject &root = jsonBuffer.parseObject((char*)payload);
  if(!root.success()){
    debug_println("Parse error: ");
    debug_println((char*)payload);
    return;
  }

  dim_light_set(root["val"], root["delay"]);
}

void callback_alarm_set(byte* payload, unsigned int length){
  DynamicJsonBuffer jsonBuffer;
  JsonObject &root = jsonBuffer.parseObject((char*)payload);
  if(!root.success()){
    debug_println("Alarm Parse error: ");
    debug_println((char*)payload);
    return;
  }
  
  unsigned long long alarm_time = root["alarm_time"];
  debug_print("Alarm set, alarm_time: ");
  debug_println((unsigned long)alarm_time);
  dimAlarm.set_alarm(root["alarm_time"], root["val"], root["delay"]);
}

void callback(char* topic, byte* payload, unsigned int length) {
  if(strcmp(topic, mqtt_set_topic) == 0){
    callback_light_set(payload, length);
  } else if(strcmp(topic, mqtt_alarm_topic) == 0){
    callback_alarm_set(payload, length);
  }
  
}

void setup() {
  pinMode(BUILTIN_LED, OUTPUT);     // Initialize the BUILTIN_LED pin as an output
  Serial.begin(9600);
  debug_println("Hello");

  WiFiManager wifiManager;
  wifiManager.setDebugOutput(false);
  wifiManager.setTimeout(600);
  debug_println("connecting using wifimanager");
  if(!wifiManager.autoConnect("DimmerSetup")){
    //No success
    debug_println("autoConnect unsuccesful");
    while(1) ESP.deepSleep(30 * 1000000);
  }
  
  configTime(1 * 3600, 0, "pool.ntp.org", "time.nist.gov");
  debug_println("\nWaiting for time");
  while (!time(nullptr)) {
    debug_print(".");
    delay(1000);
  }
  debug_println("");
  time_t rawtime;
  time(&rawtime);
  debug_print("The current local time is:");
  debug_println(ctime(&rawtime));
  debug_println(rawtime);

  client.setServer(MQTT_SERVER, MQTT_PORT);
  client.setCallback(callback);
}

void reconnect() {
  // Loop until we're reconnected
  while (!client.connected()) {
    debug_println("Attempting MQTT connection...");
    // Attempt to connect
    if (client.connect("ESP8266Client", MQTT_USERNAME, MQTT_PASSWORD)) {
      debug_println("connected");
      // Once connected, publish an announcement...
      client.publish("announce", "esp8266 dimmer ready");
      // ... and resubscribe
      client.subscribe(mqtt_set_topic);
      client.subscribe(mqtt_alarm_topic);
    } else {
      debug_println("failed, rc=");
      debug_println(client.state());
      debug_println(" try again in 5 seconds");
      // Wait 5 seconds before retrying
      delay(5000);
    }
  }
}

static long next_pb = 0;
void loop() {
  if (!client.connected()) {
    reconnect();
  }
  client.loop();
  
  time_t rawtime;
  time(&rawtime);
  dimAlarm.loop(rawtime);

  long now = millis();
  if(!digitalRead(0) && next_pb < now){
    client.publish("announce", "esp8266 dimmer button push");
    debug_print("Time: ");
    debug_println(rawtime);
    next_pb = now + 200;
  }
}
