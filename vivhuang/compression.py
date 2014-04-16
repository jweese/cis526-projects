#!/usr/bin/env python
import glob
import optparse
import os
import sys
import math
from collections import defaultdict
from collections import Counter

optparser = optparse.OptionParser()
optparser.add_option("-d", "--data", dest="data", default="data/train")
optparser.add_option("-t", "--test", dest="test", default="data/test")
opts = optparser.parse_args()[0]

# k is maximum context, adjustable
k = 2

# map from language name --> predictions map
lang_map = {}
lm = defaultdict(lambda: defaultdict(float))

def calculate_space(context, x, index):
    if len(context) > k:
        calculate_space(context[-k:], x, index)
    if lm[context][x]:
        return lm[context][x]
    elif context == '':
        return 1.0 / lm['alpha']
    else:
        if lm[context]['esc'] == 0.0:
            return calculate_space(context[1:], x, index)
        else:
            return lm[context]['esc'] * calculate_space(context[1:], x, index)

# create language models for each language
for file in glob.glob(opts.data + "/*"):
    lang_name = os.path.basename(file)
    alpha = len(Counter(open(file).read().lower()).keys())

    for line in open(file):
        prediction = defaultdict(lambda: defaultdict(float))

        line = line.strip().replace(' ', '')
        for j in xrange(k+1):
	    for idx in xrange(len(line)-j):
	        context = line[idx:idx+j]
                prediction[context][line[idx+j]] += 1

        for context in prediction:
            esc_num = len(prediction[context].keys())
            count = sum(prediction[context].values()) + esc_num
            for letter in prediction[context]:
                prediction[context][letter] = prediction[context][letter] / count
            prediction[context]['esc'] = esc_num / count
        lang_map[lang_name] = prediction
        lang_map[lang_name]['alpha'] = alpha


for idx, line in enumerate(open(opts.test)):
    if idx % 50 == 0:
        sys.stderr.write("%s\n" % idx)
    line = line.lower().strip().replace(' ', '')
    lowest_ent = 100.0
    lang_guess = ''

    for lang in lang_map:
        current_ent = 1.0
        lm = lang_map[lang]
        n = len(line)

        for i in xrange(n-1):
            x_i = line[i]
            context_i = line[:i]
            prob = calculate_space(context_i, x_i, idx)
            current_ent += -1 * math.log(prob, 2)
        current_ent = current_ent * 1.0 / n

        if current_ent < lowest_ent:
            lowest_ent = current_ent
            lang_guess = lang

    print lang_guess
