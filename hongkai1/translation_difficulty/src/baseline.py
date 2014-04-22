import eval
from nltk.corpus import wordnet as wn
from nltk import word_tokenize
from filegetter import *

BLEUROOT_HK = "/home1/h/hongkai1/mt_project/translation_difficulty/avgbleus"
BASELINE0_ROOT = "/home1/h/hongkai1/mt_project/translation_difficulty/baseline0"
BASELINE1_ROOT = "/home1/h/hongkai1/mt_project/translation_difficulty/baseline1"
ROOT = "/project/cis/nlp/data/corpora/wmt_test/"
LANGUAGES = ["cs","de","es","fr"]
YEARS = ["11","12","13"]
YEARS_ADV = ["11test", "11dev", "12", "13"];   # Because we split year 11 into development and testing set.   # Reserve the first 1500 sentences as the test sentences. 
TEST_N = 1500

SYSIDS_SRCEN = {"11":common_sysids(True,"11"), 
                "12":common_sysids(True,"12"),
                "13":common_sysids(True,"13")}
SYSIDS_TGTEN = {"11":common_sysids(False,"11"),
                "12":common_sysids(False,"12"),
                "13":common_sysids(False,"13")}

#-----------------This part needs to be commented if we ask the students to implement.----------------------------------------------
def get_avg_polysemy(words):
    sumscore = 0;
    for w in words: sumscore += len(wn.synsets(w))
    return sumscore/ (len(words) + 0.0)

def get_polysemy(words):
    sumscore = 0.0;
    for w in words: sumscore += len(wn.synsets(w)) + 0.0;
    return sumscore; 

def get_max_polysemy(words):
    polys = [len(wn.synsets(w)) for w in words]
    return max(polys)+0.0

# Implementation of the baseline system goes here. 
#---------------------------------------------------------------
def baseline_system(en_is_src, year, outfolder):
    filedict = getallfilepairs(en_is_src, year)  # filedict looked like: en-de: files(reffile, sysfilelist)
    for k,v in filedict.items():
        filetag = k+"."+year
        outf = os.path.join(outfolder, filetag)
        refsents = readsents(v.reffile); srcsents = readsents(v.srcfile)
        
        f = open(outf,'w')
        #Baseline Difficulty: Average polysemy
        for src in srcsents:
            words = word_tokenize(src);
            score = get_avg_polysemy(words);
            f.write(str(score)+'\n')
        sys.stderr.write(outf+" produced\n")
        f.close();

def baseline_maxsys(en_is_src, year, outfolder):
    filedict = getallfilepairs(en_is_src, year)
    for k,v in filedict.items():
        filetag = k+"."+year
        outf = os.path.join(outfolder, filetag)
        refsents = readsents(v.reffile); srcsents = readsents(v.srcfile)

        f = open(outf, 'w')
        #Baselin2: Max polysemy
        for src in srcsents:
            words = word_tokenize(src);
            score = get_max_polysemy(words);
            f.write(str(score)+'\n')
        sys.stderr.write(outf+" produced\n")
        f.close();
        
#  Evaluate on the baseline output
#-----------------------------------------------------------------
def baseline_output(en_is_src, year, outfolder):
    filedict = getallfilepairs(en_is_src, year)
    for k,v in filedict.items():
        filetag = k+"."+year
        print(filetag); sys.stderr.write(filetag + "\n")
        avgbleu_file = os.path.join(BLEUROOT_HK,filetag)
        output_file = os.path.join(outfolder, filetag)

        prds = readnums(output_file) 
        labels = readnums(avgbleu_file)

        eval.eval(prds, labels)   
        print(""); print("========"); sys.stderr.write("\n"); sys.stderr.write("===========\n")

        # Evaluate on the Development Set or Test Set.
#---------------------------------------------------------
def test_output(en_is_src, setname, outfolder):
    for lan in LANGUAGES:
        pairname = 'en-' + lan if (en_is_src) else lan + '-en';
        print(pairname, setname); sys.stderr.write(pairname + ".11" + setname + "\n")
        avgbleu_file = os.path.join(BLEUROOT_HK, pairname + '.11' + setname)
        output_file = os.path.join(outfolder, pairname + '.11' + setname)
 
        prds = readnums(output_file)
        labels = readnums(avgbleu_file)

        eval.eval(prds, labels) 
        print(""); print("========"); sys.stderr.write("\n"); sys.stderr.write("===========\n")

#----------------------------------------------------------------
# Produce Baseline Scores ##
def run_baseline_system():
    for year in YEARS:   # Output Folder is BASELINE0_ROOT
        baseline_system(True, year, BASELINE0_ROOT)

def run_baseline_maxsystem():
    for year in YEARS:
        baseline_maxsys(True, year, BASELINE1_ROOT)

# Run Evaluations on 2011, 2012, 2013
def run_baseline_eval():
    for year in YEARS:    #  The Folder to compare with is BASELINE0_ROOT
        baseline_output(True, year, BASELINE1_ROOT)        

def run_test_eval():    
    test_output(True, 'dev', BASELINE1_ROOT)
    test_output(True, 'test', BASELINE1_ROOT)

def aux_split():
    #root = '../avgbleus/' # or 
    en_is_src = True;
    root = '../baseline1/'
    for lan in LANGUAGES:
        pairname = 'en-' + lan if (en_is_src) else lan + '-en';
        print(pairname)        
        filename = root + pairname + '.11' # Only split the year 2011
        testname = root + pairname + '.11test'; devname = root + pairname + '.11dev'

        lines = readnums(filename)
        testlines = lines[:TEST_N]; devlines = lines[TEST_N:];
        writefile(testname, testlines); writefile(devname, devlines)
        sys.stderr.write(filename + "-->" + testname + "\n"); 
        sys.stderr.write(filename + "-->" + devname + "\n");     

if __name__ == "__main__":
#    run_baseline_system()
#    run_baseline_maxsystem()
    run_baseline_eval()
    print("")
    run_test_eval()
    #aux_split()
