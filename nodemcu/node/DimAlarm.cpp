#include "DimAlarm.h"
#include "DimLight.h"

void DimAlarm::set_alarm(time_t alarm_time, uint32_t val, uint32_t del){
  this->alarm_time = alarm_time;
  this->val = val;
  this->del = del;
  this->active = true;
}

void DimAlarm::deactivate_alarm(){
  this->active = false;
}

void DimAlarm::loop(time_t current_time){
  if(current_time >= alarm_time && active){
    dim_light_set(val,del);
    active = false;
  }
}

