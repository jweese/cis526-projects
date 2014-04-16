#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
A feature extractor for chunking.
Copyright 2010,2011 Naoaki Okazaki.

Modified by Sean Welleck for use in Chinese Word Segmentation application.
"""
import crfutils
import unicodedata
import sys

# Separator of field values in the tagged file.
separator = ' '

# Field names of the input data.
fields = 'c y'

# Attribute templates; a tuple of (name, offset) pairs,
# where name and offset specify a field name and offset from which
# the template extracts a feature value.
#   E.g. (('c', -3), ('c', 0)) is a feature containing the current character
#   and the character three positions back.
# See http://www.newdesign.aclweb.org/anthology/Y/Y06/Y06-1012.pdf for ideas
# on good features to define here.
templates = (
    (('c', -2), ),
    (('c', -1), ),
    (('c',  0), ),
    (('c',  1), ),
    (('c',  2), ),
    (('c', -2), ('c', -1)),
    (('c', -2), ('c',  0)),
    (('c', -1), ('c',  0)),
    (('c',  0), ('c',  1)),
    (('c', -1), ('c',  1)),
    (('c', -1), ('c',  0), ('c',  1)),
    )

# Return whether the given character is punctuation.
# Specifically, whether it is a member of a Unicode 
# punctuation character class.
def is_punct(c):
	punct_categories = ['Pc', 'Pd', 'Pe', 'Pf', 'Pi', 'Po', 'Ps']
	c = c.decode('utf-8')
	if unicodedata.category(c) in punct_categories:
		return "1"
	else:
		return "0"

# Return whether the given character's "class", where
# class = 1 if numeric, 2 if english letter, 3 otherwise.
def char_class(c):
	num_categories = ['Nl', 'No', 'Nd']
	letter_categories = ['LC', 'Ll', 'Lm', 'Lm', 'Lo', 'Lt', 'Lu' ]
	cat = unicodedata.category(c.decode('utf-8'))
	if cat in num_categories:
		return "1"
	elif cat in letter_categories:
		return "2"
	else:
		return "3"

def feature_extractor(X):
    # Apply attribute templates to obtain features.
    crfutils.apply_templates(X, templates)
    # Add the is_punct() feature.
    map(lambda x: x['F'].append("punct=" + is_punct(x['c'])), X)
    # Add the char_class() feature.
    map(lambda x: x['F'].append("class=" + char_class(x['c'])), X)

# Generate features for a tagged file.
# Outputs a file in CRFSuite format.
# Details of the format are found at:
#	http://www.chokkan.org/software/crfsuite/manual.html#id532502
def generate(infile, outfile):
	sys.stderr.write("Generating features.\n")
	crfutils.main(feature_extractor, fields, separator, open(infile, 'r'), open(outfile, 'w'))

if __name__ == '__main__':
    crfutils.main(feature_extractor, fields=fields, sep=separator)
