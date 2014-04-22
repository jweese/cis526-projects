import eval
from nltk import word_tokenize
from filegetter import *

BLEUROOT_HK = "/home1/h/hongkai1/mt_project/translation_difficulty/avgbleus"
DEFAULTROOT = "/home1/h/hongkai1/mt_project/translation_difficulty/default"
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

# Implementation of the default system goes here. 
#---------------------------------------------------------------
def default_system(en_is_src, year):
    filedict = getallfilepairs(en_is_src, year)  # filedict looked like: en-de: files(reffile, sysfilelist)
    for k,v in filedict.items():
        filetag = k+"."+year
        outf = os.path.join(DEFAULTROOT, filetag)
        refsents = readsents(v.reffile)
        srcsents = readsents(v.srcfile)
        
        f = open(outf,'w')
        #Default Difficulty: Length. Write the length for coherent comparison later.
        for src in srcsents:
            words = word_tokenize(src);
            f.write(str(len(words))+'\n')
        sys.stderr.write(outf+" produced\n")
        f.close();

#  Evaluation by Year 
#-----------------------------------------------------------------
def default_output(en_is_src, year):
    filedict = getallfilepairs(en_is_src, year)
    for k,v in filedict.items():
        filetag = k+"."+year
        print(filetag + '\n')
        sys.stderr.write(filetag + '\n')

        avgbleu_file = os.path.join(BLEUROOT_HK,filetag)
        default_file = os.path.join(DEFAULTROOT, filetag)

        prds = readnums(default_file) 
        labels = readnums(avgbleu_file)

        eval.eval(prds, labels)    
        print(""); print("========"); sys.stderr.write("\n"); sys.stderr.write("===========\n")

# Evaluation on Development or Test
#----------------------------
def test_output(en_is_src, setname):
    for lan in LANGUAGES:
        pairname = 'en-' + lan if (en_is_src) else lan + '-en';
        print(pairname + '.11' + setname + '\n')
        sys.stderr.write(pairname + '.11' + setname + '\n')

        avgbleu_file = os.path.join(BLEUROOT_HK, pairname + '.11' + setname)
        default_file = os.path.join(DEFAULTROOT, pairname + '.11' + setname)
 
        prds = readnums(default_file)
        labels = readnums(avgbleu_file)

        eval.eval(prds, labels) 
        print(""); print("========"); sys.stderr.write("\n"); sys.stderr.write("===========\n")

#----------------------------------------------------------------
# Produce Default Scores ##
def run_default_system():
    for year in YEARS:
        default_system(True, year)
        default_system(False, year)

# Run Evaluations on 2011, 2012, 2013
def run_default_eval():
    for year in YEARS:
        default_output(True, year)
        default_output(False, year)

# Run Evaluations on 2011_development and 2011_test set 
def run_test_eval():
    test_output(True, 'dev')
    test_output(True, 'test')
    test_output(False, 'dev')
    test_output(False, 'test')

# Auxiliary Function. Splitting the test and development data. 
def aux_split(en_is_src):
    #root = '../avgbleus/' # or 
    root = '../default/'
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
#    run_default_system()
    run_default_eval();
#    run_test_eval()
#    aux_split(False); aux_split(True)



