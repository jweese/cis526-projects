#!/usr/bin/env python
import glob
import operator
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

def create_profile(text):
    string.translate(text.lower(), None, string.punctuation + "\n")   # remove punctuation and newlines
    bi_grams = find_ngrams(text, 2)
    tri_grams = find_ngrams(text, 3)
    quad_grams = find_ngrams(text, 4)
    five_grams = find_ngrams(text, 5)

    freq = defaultdict(int)
    for ngram in bi_grams + tri_grams + quad_grams + five_grams:
        freq[ngram] += 1

    # sorts the dictionary by value, producing a list of tuples ordered
    # starting with the most frequent n-grams
    ordered = sorted(freq.iteritems(), key=operator.itemgetter(1), reverse=True)
    return [(ngram, i) for i, (ngram, count) in enumerate(ordered)]

# frequencies maps from language --> probability of each word in that language
lang_map = {}

for file in glob.glob(opts.data + "/*"):
    lang_name = os.path.basename(file)
    text = open(file).read()
    # change ordered list back into a dictionary for easy lookup during
    # distance calculation
    sys.stderr.write("creating profile for %s\n" % lang_name)
    profile = create_profile(text)
    lang_map[lang_name] = dict(profile)

for i, line in enumerate(open(opts.test)):
    sys.stderr.write(line)
    profile = create_profile(line)
    lang_guess = ''
    max_penalty = 3
    min_distance = max_penalty * len(profile)
    sys.stderr.write("initial value of min_distance: \t%d\n" % min_distance)

    for lang in lang_map:
        distance = 0
        for (ngram, i) in profile:
            if ngram in lang_map[lang]:
                # print "rank in line profile: %d, rank in lang profile: %d" % (i, lang_map[lang][ngram])
                distance += min(abs(lang_map[lang][ngram] - i),max_penalty)
            else:
                distance += max_penalty

        sys.stderr.write("distance to %s profile: \t%d\n" % (lang, distance))
        if distance < min_distance:
            min_distance = distance
            lang_guess = lang
    print lang_guess
