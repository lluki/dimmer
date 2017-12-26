#!/usr/bin/python
import sys
sys.path.append("../../python-testclient")
from conf import get_conf

print "#ifndef MQTT_CONSTANTS_H"
print "#define MQTT_CONSTANTS_H"
for (k,v) in get_conf().iteritems():
    if not isinstance(v, int):
        v = '"%s"' % v
    print """#define MQTT_%s %s """ % (k.upper(),v)
print "#endif"
