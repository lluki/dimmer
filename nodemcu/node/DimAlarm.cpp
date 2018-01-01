#include "DimAlarm.h"
#include "DimLight.h"

#define SNOOZE_DURATION_S (5*60)

void DimAlarm::set_alarm(time_t alarm_time, uint32_t val, uint32_t del){
  bool is_snooze = this->alarm_time < alarm_time && alarm_time - this->alarm_time < SNOOZE_DURATION_S;
  if(!is_snooze){
    Serial.println("DimAlarm: No snooze, setting alarm");
    this->alarm_time = alarm_time;
    this->val = val;
    this->del = del;
    this->active = true;
  } else {
    Serial.println("DimAlarm: snooze detected");
    // Set fake alarm time for very long snooze-along sessions
    this->alarm_time = alarm_time;
  }
  
}

void DimAlarm::deactivate_alarm(){
  Serial.println("DimAlarm: deactivate alarm");
  this->active = false;
  
}

void DimAlarm::loop(time_t current_time){
  if(current_time >= alarm_time && active){
    dim_light_set(val,del);
    active = false;
  }
}

