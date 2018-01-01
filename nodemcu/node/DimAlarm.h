#ifndef DimAlarm_h
#define DimAlarm_h

#include <stdint.h>
#include <time.h>

class DimAlarm {
  private:
    time_t alarm_time = 0;
    uint32_t val = 0;
    uint32_t del = 0;
    bool active = false;
  public:
    void set_alarm(long alarm_time, uint32_t val, uint32_t del);
    void deactivate_alarm();
    void loop(long current_time);
  
};

#endif
