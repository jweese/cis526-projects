#!/usr/bin/env python

import nltk.tree

file = open("../data/europarl.es-en.100", "r")

for line in file:
    tree = nltk.tree.Tree.parse(line.strip().split("|||")[1])
    for child in tree.flatten():
        print child,
    print
