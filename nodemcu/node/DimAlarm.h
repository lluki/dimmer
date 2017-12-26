#ifndef DimAlarm_h
#define DimAlarm_h

#include <stdint.h>

class DimAlarm {
  private:
    long alarm_time;
    uint32_t val;
    uint32_t del;
    bool active = false;
  public:
    void set_alarm(long alarm_time, uint32_t val, uint32_t del);
    void deactivate_alarm();
    void loop(long current_time);
  
};

#endif
