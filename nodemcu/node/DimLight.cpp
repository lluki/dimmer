#include "DimLight.h"


void dim_light_set(uint32_t val, uint32_t del){
  digitalWrite(BUILTIN_LED, !val);
  
  Serial.print("\ns:");
  Serial.print(val);
  Serial.print(":");
  Serial.print(del);
  Serial.println();
};
