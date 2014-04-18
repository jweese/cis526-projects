#!/usr/bin/python
import os
import argparse
from liblinearutil import *
import numpy as np

def stanford_lex_parse_en(in_dir, out_dir):
    ## parse all files from in_dir
    ## and put the .stp outputs to out_dir
    ## if a file's id is present in out_dir then the file will be ignored
    parsed_files = set(map(lambda x: x[:-4], os.listdir(out_dir)))
    files = filter(lambda x: x not in parsed_files, os.listdir(in_dir))
    files = map(lambda f: os.path.join(in_dir,f), files)
    stanford_parser_dir = '/project/cis/nlp/tools/stanford-parser-2012-11-12/'
    for i in range(int(ceil(len(files)/100.0))):
        if 100*(i+1)<len(files):
            files_str = reduce(lambda c,x: c+' '+x, files[100*i:100*(i+1)], '')
        else:
            files_str = reduce(lambda c,x: c+' '+x, files[100*i:], '')
        print "start parsing batch "+repr(i)
        os.system('java -mx4g -cp '+stanford_parser_dir+
                  '/*: edu.stanford.nlp.parser.lexparser.LexicalizedParser '+
                  '-sentences newline '+
                  '-outputFormat oneline -writeOutputFiles -outputFilesDirectory '+
                  out_dir+' edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz '+
                  files_str)


def liblinear_train_perdict_svr(trainfile, testfile, normalize, weights=[]):
    y, x = svm_read_problem(trainfile)
    # print y
    # print x
    if normalize: x, maxes = normalize_features(x)
    # m_cv = train(weights,y,x,'-s 12 -v 10')
    m = train(weights,y,x,'-s 12')
    save_model(trainfile.rsplit('.',1)[0]+'.model',m)
    y, x = svm_read_problem(testfile)
    if normalize: x, testmaxes = normalize_features(x, maxes)
    # print predict(y,x,m)
    p_label, p_acc, p_val = predict(y,x,m)
    # filename = trainfile.rsplit('.',1)[0]+'.probs'
    # with open(filename,'w') as f:
    #     for x in pval:
    #         f.write(str(x)+"\n")
    # f.close()
    return p_label


def normalize_features(x, trainmaxes=None):
    maxes = trainmaxes if trainmaxes!=None else {}
    if trainmaxes == None:
        for itemd in x:
            for k,v in itemd.items():
                if k not in maxes or maxes[k] < abs(v): maxes[k] = abs(v)
    newx = []
    for itemd in x:
        newd = dict.fromkeys(itemd)
        for k,v in itemd.items():
            if maxes[k] != 0: newd[k] = (v+0.0)/maxes[k]
            else: newd[k] = 0.0
        newx.append(newd)
    return newx,maxes


if __name__ == "__main__":
    # argparser = argparse.ArgumentParser()
    # argparser.add_argument('-l', dest='lan', help='language')
    # argparser.add_argument('--indir', dest='indir', default='', help='input dir')
    # argparser.add_argument('--outdir', dest='outdir', default='', help='output dir')
    # args = argparse.parse_args()
    # if args.lan[:2].lower() == 'en':
    #     stanford_lex_parse_en(args.indir, args.outdir)
    ## svr
    liblinear_train_perdict_svr("temp.train","temp.test",True)
