#!/usr/bin/env python
import optparse
import glob
import os
import sys
from collections import defaultdict

optparser = optparse.OptionParser()
optparser.add_option("-d", "--data", dest="data", default="data/train")
optparser.add_option("-t", "--test", dest="test", default="data/test")
opts = optparser.parse_args()[0]

# frequencies maps from language --> probability of each word in that language
lang_map = {}

for file in glob.glob(opts.data + "/*"):
    # gets actual language name
    lang_name = os.path.basename(file)
    text_arr = open(file).read().lower().split()

    # total words in each language (training data) for frequency
    total_num_words = len(text_arr)

    # create a probability map for each word in the language (training data)
    frequencies = defaultdict(float)
    for word in text_arr:
        frequencies[word] += 1.0
    for word in frequencies:
        frequencies[word] = frequencies[word] / total_num_words
    lang_map[lang_name] = frequencies


for i, line in enumerate(open(opts.test)):
    word_arr = line.lower().split()

    # initialize variables
    lang_guess = ''
    highest_prob = 0.0

    # accumulate probabilities for each word in the sentence
    for lang in lang_map:
        current_prob = 0.0
        for word in word_arr:
            if word in lang_map[lang]:
                if (current_prob == 0.0):
                    current_prob = 1.0
                current_prob = current_prob * lang_map[lang][word]

        # update to the highest probability and language guess
        if current_prob > highest_prob:
            highest_prob = current_prob
            lang_guess = lang

    print lang_guess
