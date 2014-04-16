#!/usr/bin/env python
import glob
import optparse
import os
import random

optparser = optparse.OptionParser()
optparser.add_option("-t", "--test", dest="test", default="test_data")
optparser.add_option("-l", "--lang", dest="lang")
opts = optparser.parse_args()[0]

files = []
combined = []

if opts.lang:
    files = [(opts.test + "/" + lang_name) for lang_name in opts.lang.split(",")]
else:
    files = glob.glob(opts.test + "/*")

for file in files:
    lang_name = os.path.basename(file)
    combined = combined + [(line.split("\t")[1], lang_name) for line in open(file)]

random.shuffle(combined)

dev_input = open('dev_input', 'w')
dev_soln = open('dev_soln', 'w')
for (line, lang_name) in combined[:500]:
    dev_input.write(line)
    dev_soln.write(lang_name + "\n")
dev_input.close()
dev_soln.close()

test_input = open('test_input', 'w')
test_soln = open('test_soln', 'w')
for (line, lang_name) in combined[500:1000]:
    test_input.write(line)
    test_soln.write(lang_name + "\n")
test_input.close()
test_soln.close()


