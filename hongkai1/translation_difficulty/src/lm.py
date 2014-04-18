import os, sys, math, operator
import subprocess
import eval
from filegetter import *
from nltk import FreqDist, ConditionalFreqDist
from nltk.corpus import PlaintextCorpusReader
from nltk.tokenize import sent_tokenize, word_tokenize
from numpy import array,zeros

DATA_ROOT = '/home1/c/cis530/hw2/data/'
TEST_F = '/home1/c/cis530/hw2/data/test/118742636.txt'
CLUTO_DIR_S = '/home1/c/cis530/hw2/cluto-2.1.1/Linux/scluster'
NGRAM_COUNT_LOC = '/home1/c/cis530/hw2/srilm/ngram-count'
NGRAM_LOC = '/home1/c/cis530/hw2/srilm/ngram'

def get_sub_directories(path):
    return [filename for filename in os.listdir(path) if os.path.isdir(os.path.join(path, filename))]

def get_all_files_dir(directory):
    file_names = []
    for (i, rel_dir) in enumerate(get_sub_directories(directory)):
        files = get_all_files(os.path.join(directory, rel_dir))
        abs_files = [os.path.join(directory, rel_dir, f) for f in files]
        file_names.extend(abs_files)
    return file_names

def get_all_files(path):
    files_all = PlaintextCorpusReader(path, '.*')
    return files_all.fileids()

#2.1
#--- functions adopted/changed from previous homework ---#
def get_dir_words(directory):
    if (os.path.isdir(directory)):
        return load_collection_tokens(directory);
    return load_file_tokens(directory)

def load_collection_tokens(path):
    lists = []
    for f in get_all_files(path):
        lists.extend(load_file_tokens(os.path.join(path,f)))
    return lists

def load_file_tokens(filename):
    linestring = open(filename, 'r').read()
    words = word_tokenize(linestring)
    return map(lambda w: word_transform(w), words)

def list_mult(A, B):
    return map(lambda (x,y): x*y, zip(A,B))

def sum_v(A):
    A2 = [a*a for a in A]
    return sum(A2)

def cosine_similarity(A, B): # (5 Points)
    if (sum(A)*sum(B) == 0): return 0
    return float(sum(list_mult(A,B))) / math.sqrt(float(sum_v(A)*sum_v(B)))

#3.1
def print_sentences_from_files(files, outfilename):
    lists = []
    for (i, f) in enumerate(files):
        if (i%100 == 0): print(str(i) + " / " + str(len(files)))
        fullstring = open(f, 'r').read()        
        lists.extend(map(lambda s: sent_transform(s), sent_tokenize(fullstring)))
    fout = open(outfilename, 'w')
    for s in lists:
        if len(s) > 0:
            s = reduce(lambda c,w: c+' '+w, s)
            fout.write(s+'\n')
#3.2(1)
def gen_lm_from_file(input, output):
    os.system(NGRAM_COUNT_LOC+' -text '+input+' -lm '+output)
           
#3.2(2) cluster is better
def get_ppl_from_lm(lm_file, test_file):
    outstr = subprocess.check_output([NGRAM_LOC, '-lm', lm_file, '-ppl', test_file])
    items = outstr.split()
    for i,item in enumerate(items):
        if item == 'ppl=': return float(items[i+1])
    return None

def get_ppls(perplexity_file):
    lines =  readsents(perplexity_file)
    outnums = [];
    for (i, thisline) in enumerate(lines):        
        if (i%4 == 2):             
            items = thisline.split(' ')            
            #print(items)
            for j,item in enumerate(items):    
                if item == 'ppl=': 
                    if (items[j+1]=='undefined'):
                        outnums.append(999.0)
                    else:
                        outnums.append(float(items[j+1]))

    return outnums

def PlayLM(sentfile, outfile):
    #filenames = Hw1_funcs.load_files_recur(inputdirectory)
    #print('Total Names'  ,  len(filenames))       
    #print_sentences_from_files(filenames, sentfile)
    gen_lm_from_file(sentfile, outfile)

NYT_ROOT = '/nlp/scratch/hongkai1/nytimes/LM_MT'
  
def TrainLM(sentfile, outroot):
    smoothing_args = ['', '-addsmooth 1','-cdiscount 0.75', '-cdiscount 0.75 -interpolate']
    for i,sarg in enumerate(smoothing_args):
        outfile = os.path.join(outroot, 'sol_sm_' + str(i));
        print(outfile); 
        os.system(NGRAM_COUNT_LOC+' -text '+ sentfile+' -lm ' + outfile +' '+sarg)

def TestLM(modelfile, testfile, outfile, order):
    os.system(NGRAM_LOC + ' -lm ' + modelfile + ' -ppl ' + testfile + ' -debug 1 >' + outfile )

def OutTestLM(modelfile, testfile):
    outstr = os.system(NGRAM_LOC + ' -lm ' + modelfile + ' -ppl ' + testfile + ' -debug 1')
    return outstr

RootFolder = '~/SumCombine/tmp/sumstore/data2004/best0_combine'
def main(argv):
    if (len(argv) == 0):
        print("Please Input Mode\n");
        print("naivetrain sentfile outfile")
        print("train sentfile outroot") 
        print("test modelfile testfile")
        print("batchtest modelfile outfile")
    else:
        if (argv[0] == 'naivetrain'):   # sentence-file, out-file
            PlayLM(argv[1], argv[2])
        elif (argv[0] == 'train'):
            TrainLM(argv[1], argv[2])
        elif (argv[0] == 'test'):
            TestLM(argv[1], argv[2])
        elif (argv[0] == 'batchtest'):
            BatchLM(argv[1], argv[2])

BLEUROOT_HK = "/home1/h/hongkai1/mt_project/translation_difficulty/avgbleus"
LMROOT = "/home1/h/hongkai1/mt_project/translation_difficulty/lm_features";

YEARS = ["11","12","13"]

def eval_output(year, cproot):
    filedict = getallfilepairs(True, year)
    for k,v in filedict.items():
        filetag = k+'.'+year
        print(filetag+'\n')
        sys.stderr.write(filetag+'\n')
        
        avgbleu_file = os.path.join(BLEUROOT_HK, filetag)
        predict_file = os.path.join(cproot, filetag)
 
        prds = readnums(predict_file)
        labels = readnums(avgbleu_file)
 
        eval.eval(prds, labels)
        print(""); print("=========="); sys.stderr.write("\n"); sys.stderr.write("============\n"); 


LANGS = ['cs', 'de', 'es', 'fr'];
def make_result_file():
    inputfd = os.path.join('../lm_files', 'lm_final'); outroot = '../lm_features';
    for year in YEARS:
        filename = os.path.join(inputfd, 'N3_' + year + '.txt')
        for lan in LANGS:
            outname = os.path.join(outroot, 'en-' + lan + '.' + year)
            os.system('cp ' + filename + ' ' + outname)
            #print(filename, outname) 

def lm_run():
    '''for i in range(1, 4):
        for year in YEARS:
            filename = getsrcfile('en', year);
            outfile = os.path.join('../lm_files', 'LM_output_year' + year + '_N' + str(i) + '.txt')
            TestLM(NYT_ROOT + '/LM_' + str(i) + 'gram', filename, outfile, i)'''

    for i in range(1, 4):
        for year in YEARS:
            outfile = os.path.join('../lm_files', 'LM_output_year' + year + '_N' + str(i) + '.txt')
            outnums= get_ppls(outfile)
            f = open(os.path.join('../lm_files/lm_final', 'ppl_year' + year + '_N' + str(i)+ '.txt'),'w')
            for num in outnums: f.write(str(num)+'\n')
            f.close();
        
if __name__ == '__main__':
    lm_run()
    #ComputeLogProb()   
    #main(sys.argv[1:])    
    #TestLM(NYTMODEL, os.path.join(RootFolder, '00/sum1000.txt'));  
    #outputfile();
#    make_result_file()
#    for year in YEARS:
#        eval_output(year, LMROOT)
    #TrainLM()       

