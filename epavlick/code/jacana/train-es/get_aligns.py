#!/bin/python

import sys
import json

data = json.load(open(sys.argv[1]))

for l in data : print l['sureAlign']
