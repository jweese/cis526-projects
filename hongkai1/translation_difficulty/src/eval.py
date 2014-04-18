#!/usr/bin/env python
import optparse
import sys, os
from filegetter import *
from numpy import zeros
from scipy import stats
import random

LANGUAGES = ["cs","de","es","fr"]
BLEUROOT = "../avgbleus"
FEATURE_ROOT = "../syntaxfeatures"
PARSEROOT = "../data_parsed"
YEARS = ["11","12","13"]
YEARS_ADV = ["11test", "11dev", "12", "13"];   # Because we split year 11 into development and testing set.   # Reserve the first 1500 sentences as the test sentences. 
TEST_N = 1500

def mean(values): return (sum(values)+0.0)/(float(len(values))+0.0)

def pairwise_eval(preds, labels):    
    assert len(preds) == len(labels)
    def _make_sqmat(lst):
        sqmat = zeros((len(lst),len(lst)))
        for r in xrange(len(lst)): # Ignore the ones which are equal?
            for c in xrange(r+1,len(lst)):
                sqmat[r][c] = 1 if lst[r] > lst[c] else -1
        return sqmat
    mat_pred = _make_sqmat(preds)
    # print mat_pred
    mat_label = _make_sqmat(labels)
    # print mat_label
    mat_diff = mat_pred-mat_label
    # print mat_diff
    correct = 0.0; counter = 0.0
    for i in xrange(len(mat_diff)):
        for j in xrange(i+1,len(mat_diff)):
            if mat_diff[i][j] == 0: correct += 1
            counter += 1
    return correct/counter

def ret_eval(preds, labels):
    return pairwise_eval(preds, labels), stats.spearmanr(preds, labels)[0]    

def eval(preds, labels):
    print "pairwise accuracy: %.4f" % pairwise_eval(preds, labels)
    tt, pval = stats.spearmanr(preds, labels)
    print "Spearman corr. 2tail: %.4f; pval: %.4f" % (tt,pval)

# test_dev
def multi_eval(preds, test_dev):
    pvs = []; tts = [];
    for tgtlan in LANGUAGES:
        labelfile = os.path.join(BLEUROOT, "en-"+tgtlan+".11" + test_dev)
        labels = readnums(labelfile)
        pv, tt = ret_eval(preds, labels)
        pvs.append(pv); tts.append(tt);
    return mean(pvs), mean(tts)

def test(numinst):
    preds = []; labels = []
    random.seed(0)
    for i in range(numinst):
        preds.append(random.uniform(5,25))
        labels.append(random.uniform(5,25))
    # print preds
    # print labels
    eval(preds, labels)

def read_file(fname):
    lst = []
    with open(fname) as f:
        for line in f:
            lst.append(float(line.strip()))
    f.close()
    return lst

if __name__ == "__main__":
    optparser = optparse.OptionParser()
    optparser.add_option("-p", "--fpred", dest="fpred", help="file containing predicted BLEU scores")
    optparser.add_option("-l", "--flabel", dest="flabel", help="file containing true avg BLEU scores")
    (opts,_) = optparser.parse_args()
    preds = read_file(opts.fpred)
    labels = read_file(opts.flabel)
    eval(preds, labels)
    # test(3000)
