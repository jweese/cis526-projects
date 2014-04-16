#!/usr/bin/env python
from __future__ import division
import glob
import math
import optparse
import os
import string
import sys
from collections import defaultdict

optparser = optparse.OptionParser()
optparser.add_option("-d", "--data", dest="data", default="data/train")
optparser.add_option("-t", "--test", dest="test", default="data/test")
opts = optparser.parse_args()[0]

# Returns a list of tuples. Each tuple represents one ngram.
# e.g. find_ngrams("hello", 2) produces [('h', 'e'), ('e', 'l'), ('l', 'l'), ('l', 'o')]
def find_ngrams(input_list, n):
    return zip(*[input_list[i:] for i in range(n)])

lang_map = {}

for file in glob.glob(opts.data + "/*"):
    lang_name = os.path.basename(file)
    text = open(file).read()
    string.translate(text.lower(), None, string.punctuation + "\n")   # remove punctuation and newlines
    bi_grams = find_ngrams(text, 2)
    tri_grams = find_ngrams(text, 3)

    t = defaultdict(int)
    for ngram in bi_grams + tri_grams:
        t[ngram] += 1
    lang_map[lang_name] = t

for i, line in enumerate(open(opts.test)):
    tri_grams = find_ngrams(line, 3)
    lang_guess = ''
    highest_prob = -sys.maxint - 1

    for lang in lang_map:
        current_prob = 0.0
        for ngram in tri_grams:
            current_prob += math.log((lang_map[lang][ngram] + 1) / (lang_map[lang][ngram[:2]] + 100000))

        if current_prob > highest_prob:
            highest_prob = current_prob
            lang_guess = lang
    print lang_guess
