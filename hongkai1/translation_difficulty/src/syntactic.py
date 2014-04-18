import eval
import os
import tree
from filegetter import *
from collections import defaultdict
from utils import liblinear_train_perdict_svr

from numpy import mean
from nltk.corpus import wordnet as wn

BLEUROOT = "../avgbleus"
FEATURE_ROOT = "../syntaxfeatures"
PARSEROOT = "../data_parsed"

YEARS = ["11","12","13"]
YEARS_ADV = ["11test", "11dev", "12", "13"];   # Because we split year 11 into development and testing set.   
# Reserve the first 1500 sentences as the test sentences. 
TEST_N = 1500

SYSIDS_SRCEN = {"11":common_sysids(True,"11"), 
                "12":common_sysids(True,"12"),
                "13":common_sysids(True,"13")}
SYSIDS_TGTEN = {"11":common_sysids(False,"11"),
                "12":common_sysids(False,"12"),
                "13":common_sysids(False,"13")}

NTAGS = set(["NN","NNS","NNP","NNPS"])
VBTAGS = set(["VB","VBZ","VBP","VBD","VBN","VBG"])
ADJTAGS = set(["JJ","JJR","JJS"])
ADVTAGS = set(["RB","RBR","RBS","RP"])
WNTAGDICT = {}
for item in NTAGS:
    WNTAGDICT[item] = wn.NOUN
for item in VBTAGS:
    WNTAGDICT[item] = wn.VERB
for item in ADJTAGS:
    WNTAGDICT[item] = wn.ADJ
for item in ADVTAGS:
    WNTAGDICT[item] = wn.ADV


## categorical feature space
class Space:

    def __init__(self, startid):
        self.startid = startid
        self.freq = defaultdict(int)
        self.f_id = {}
        self.id_f = {}

    def __repr__(self):
        s = "Feature Space Instance\n"
        s += "startid: "+str(self.startid)+" endid: "+str(self.getEndId())+"\n"
        s += "# entries: "+str(len(self.f_id))
        return s

    def add(self, lst): ## add a training instance, list of features
        for e in lst:
            if e not in self.f_id:
                i = len(self.f_id)
                self.f_id[e] = i
                self.freq[e] = 1
                self.id_f[i] = e
            else:
                self.freq[e] += 1

    def getEndId(self):
        return self.startid + len(self.f_id)

    ## given: raw feature list; produce: svmlight format: each entry format id:1
    def toFeatStrBinary(self, lst):
        s = defaultdict(int)
        for e in lst:
            if e not in self.f_id: continue
            s[self.f_id[e]+self.startid] += 1
        ret = " ".join([str(e)+":"+str(v) for (e,v) in sorted(s.items())])
        return ret

FeatureMeta = namedtuple("FeatureMeta","name,space")

class Model:
    def __init__(self):
        self.featureids = {} ## <id, name>
        self.featurestrain = {} ## <name, flist>
        self.featurestest = {} ## <name, flist>
        self.featuresdev = {} ## <name, flist>
        self.nextfeatid = 1
        self.train = []
        self.train_labels = []
        self.dev = []
        self.dev_labels = []
        self.test = []
        self.test_labels = []

        self.train_LM = []; self.test_LM = []; self.dev_LM = [];

    def loadTrain(self, tgtlan):
        self.train_labels = []
        if len(self.train) == 0:
            for year in ["12","13"]:
                parsefile = os.path.join(PARSEROOT, "newstest20"+year+"-src.en.stp")
                self.train.extend(readtrees(parsefile))
        for year in ["12","13"]:
            labelfile = os.path.join(BLEUROOT, "en-"+tgtlan+"."+year)
            self.train_labels.extend(readnums(labelfile))

    def loadTest(self, tgtlan):
        if len(self.test) == 0:
            parsefile = os.path.join(PARSEROOT, "newstest2011-src.en.stp")
            self.test = readtrees(parsefile)[:TEST_N]
            self.dev = readtrees(parsefile)[TEST_N:]
        labelfile = os.path.join(BLEUROOT, "en-"+tgtlan+".11test")
        self.test_labels = readnums(labelfile)
        labelfile = os.path.join(BLEUROOT, "en-"+tgtlan+".11dev")
        self.dev_labels = readnums(labelfile)
    
    def loadLM(self, N):
        LMROOT = '../lm_files/lm_final'
        self.train_LM = [];
        for year in ["12", "13"]:
            self.train_LM.extend(readnums(os.path.join(LMROOT, 'ppl_year' + year + '_N' + str(N) + '.txt')))

        self.test_LM = readnums(os.path.join(LMROOT, 'ppl_year11_N' + str(N) + '.txt'))[:1500];
        self.dev_LM = readnums(os.path.join(LMROOT, 'ppl_year11_N' + str(N) + '.txt'))[1500:];

    def writeFeatures(self):
        lines_train = [[] for i in xrange(len(self.train))]
        lines_test = [[] for i in xrange(len(self.test))]
        lines_dev = [[] for i in xrange(len(self.dev))]
        for (fid, fmeta) in self.featureids.items():
            if fmeta.space == None:
                for i,item in enumerate(self.featurestrain[fmeta.name]):
                    lines_train[i].append(str(fid)+":"+str(item))
                for i,item in enumerate(self.featurestest[fmeta.name]):
                    lines_test[i].append(str(fid)+":"+str(item))
                for i,item in enumerate(self.featuresdev[fmeta.name]):
                    lines_dev[i].append(str(fid)+":"+str(item))
            else:
                for i,item in enumerate(self.featurestrain[fmeta.name]):
                    lines_train[i].append(item)
                for i,item in enumerate(self.featurestest[fmeta.name]):
                    lines_test[i].append(item)
                for i,item in enumerate(self.featuresdev[fmeta.name]):
                    lines_dev[i].append(item)
        with open("temp.train",'w') as f:
            for i,line in enumerate(lines_train):
                f.write(str(self.train_labels[i])+" "+" ".join(line)+"\n")
            f.close()
        with open("temp.test",'w') as f:
            for i,line in enumerate(lines_test):
                f.write(str(self.test_labels[i])+" "+" ".join(line)+"\n")
            f.close()
        with open("temp.dev",'w') as f:
            for i,line in enumerate(lines_dev):
                f.write(str(self.dev_labels[i])+" "+" ".join(line)+"\n")
            f.close()

    def singleFeatureEval(self, key):        
        print "=eval, test, "+key        
        print eval.eval(self.featurestest[key], self.test_labels)
        print "=eval, dev, "+key
        print eval.eval(self.featuresdev[key], self.dev_labels)

    def fPosNgram(self, n):
        def getposngram(t):
            return make_ngram_tuples(t.getPosTags(),n)
        space = Space(self.nextfeatid)
        key="postrigram"
        for t in self.train:
            space.add(getposngram(t))
        self.featureids[self.nextfeatid] = FeatureMeta(key,space)
        self.featurestrain[key] = []
        for t in self.train:
            self.featurestrain[key].append(space.toFeatStrBinary(getposngram(t)))
        self.featurestest[key] = []
        for t in self.test:
            self.featurestest[key].append(space.toFeatStrBinary(getposngram(t)))
        self.featuresdev[key] = []
        for t in self.dev:
            self.featuresdev[key].append(space.toFeatStrBinary(getposngram(t)))
        self.nextfeatid = space.getEndId()

    def fProdRules(self):
        space = Space(self.nextfeatid)
        key="prodrules"
        for t in self.train:
            space.add(t.getProdRules())
        self.featureids[self.nextfeatid] = FeatureMeta(key,space)
        self.featurestrain[key] = []
        for t in self.train:
            self.featurestrain[key].append(space.toFeatStrBinary(t.getProdRules()))
        self.featurestest[key] = []
        for t in self.test:
            self.featurestest[key].append(space.toFeatStrBinary(t.getProdRules()))
        self.featuresdev[key] = []
        for t in self.dev:
            self.featuresdev[key].append(space.toFeatStrBinary(t.getProdRules()))
        self.nextfeatid = space.getEndId()        

    def fNumVerbs(self):
        VB_TAGSET = set(["MD","VB","VBZ","VBD","VBP","VBN","VBG"])
        key="numverbs"
        self.featureids[self.nextfeatid] = FeatureMeta(key,None)
        self.nextfeatid += 1        
        self.featurestrain[key] = []
        for t in self.train:
            olap = len([x for x in t.getPosTags() if x in VB_TAGSET])+0.0
            self.featurestrain[key].append(olap/t.getNumTokens())
        self.featurestest[key] = []
        for t in self.test:
            olap = len([x for x in t.getPosTags() if x in VB_TAGSET])+0.0
            self.featurestest[key].append(olap/t.getNumTokens())
        self.featuresdev[key] = []
        for t in self.dev:
            olap = len([x for x in t.getPosTags() if x in VB_TAGSET])+0.0
            self.featuresdev[key].append(olap/t.getNumTokens())
        self.singleFeatureEval(key)

    def fTreeDepth(self):
        key="treedepth"
        self.featureids[self.nextfeatid] = FeatureMeta(key,None)
        self.nextfeatid += 1        
        self.featurestrain[key] = []
        for t in self.train:
            self.featurestrain[key].append(t.getMaxDepth())
        self.featurestest[key] = []
        for t in self.test:
            self.featurestest[key].append(t.getMaxDepth())
        self.featuresdev[key] = []
        for t in self.dev:
            self.featuresdev[key].append(t.getMaxDepth())
        self.singleFeatureEval(key)

    def fSentLen(self):
        key="sentlen"
        self.featureids[self.nextfeatid] = FeatureMeta(key,None)
        self.nextfeatid += 1        
        self.featurestrain[key] = []
        for t in self.train:
            self.featurestrain[key].append(t.getNumTokens())
        self.featurestest[key] = []
        for t in self.test:
            self.featurestest[key].append(t.getNumTokens())
        self.featuresdev[key] = []
        for t in self.dev:
            self.featuresdev[key].append(t.getNumTokens())
        self.singleFeatureEval(key)

    def fLenSbar(self):
        def lensbar(t):
            if len(t.children) == 0: return 0
            rs = len(t.getLeaves()) if t.node_elt.txt == "SBAR" else 0
            for child in t.children:
                rs += lensbar(child)
            return rs
        key="lensbar"
        self.featureids[self.nextfeatid] = FeatureMeta(key,None)
        self.nextfeatid += 1        
        self.featurestrain[key] = []
        for t in self.train:
            self.featurestrain[key].append((lensbar(t)+0.0)/t.getNumTokens())
        self.featurestest[key] = []
        for t in self.test:
            self.featurestest[key].append((lensbar(t)+0.0)/t.getNumTokens())
        self.featuresdev[key] = []
        for t in self.dev:
            self.featuresdev[key].append((lensbar(t)+0.0)/t.getNumTokens())
        self.singleFeatureEval(key)

    def fNumWh(self):
        WH_TAGSET = set(["WDT","WP","WP$","WRB"])
        key="numwh"
        self.featureids[self.nextfeatid] = FeatureMeta(key,None)
        self.nextfeatid += 1        
        self.featurestrain[key] = []
        for t in self.train:
            olap = len([x for x in t.getPosTags() if x in WH_TAGSET])+0.0
            self.featurestrain[key].append(olap)
        self.featurestest[key] = []
        for t in self.test:
            olap = len([x for x in t.getPosTags() if x in WH_TAGSET])+0.0
            self.featurestest[key].append(olap)        
        self.featuresdev[key] = []
        for t in self.dev:
            olap = len([x for x in t.getPosTags() if x in WH_TAGSET])+0.0
            self.featuresdev[key].append(olap)        
        self.singleFeatureEval(key)

    def fNumPunct(self):
        TAGSET = set([".",",",":"])
        key="numpunct"
        self.featureids[self.nextfeatid] = FeatureMeta(key,None)
        self.nextfeatid += 1        
        self.featurestrain[key] = []
        for t in self.train:
            olap = len([x for x in t.getPosTags() if x in TAGSET])
            self.featurestrain[key].append(olap)
        self.featurestest[key] = []
        for t in self.test:
            olap = len([x for x in t.getPosTags() if x in TAGSET])
            self.featurestest[key].append(olap)        
        self.featuresdev[key] = []
        for t in self.dev:
            olap = len([x for x in t.getPosTags() if x in TAGSET])
            self.featuresdev[key].append(olap)
        self.singleFeatureEval(key)

    def fMaxPhraseLen(self, phrasecat):
        def countmax(t):
            if len(t.children) == 0: return 0
            rs = len(t.getLeaves()) if t.node_elt.txt == phrasecat else 0
            for child in t.children:
                rs = max(rs, countmax(child))
            return rs
        key="maxlen"+phrasecat
        self.featureids[self.nextfeatid] = FeatureMeta(key,None)
        self.nextfeatid += 1        
        self.featurestrain[key] = []
        for t in self.train:
            self.featurestrain[key].append(countmax(t))
        self.featurestest[key] = []
        for t in self.test:
            self.featurestest[key].append(countmax(t))
        self.featuresdev[key] = []
        for t in self.dev:
            self.featuresdev[key].append(countmax(t))
        self.`eatureEval(key)

    def fAvgPhraseLen(self):
        def getAllPhraseLen(t):
            PHRASE_TAGSET = set(["PP","NP","VP","ADJP","ADVP"])
            if len(t.children) == 0: return []
            rs = []
            for child in t.children:
                if child.node_elt.txt in PHRASE_TAGSET:
                    rs.append(len(child.children))
                rs.extend(child.getAllPhraseLen())
            return rs
        key="avgphraselen"
        self.featureids[self.nextfeatid] = FeatureMeta(key,None)
        self.nextfeatid += 1        
        self.featurestrain[key] = []
        for t in self.train:
            l = getAllPhraseLen(t)
            v = mean(l) if len(l) > 0 else 0
            self.featurestrain[key].append(v)
        self.featurestest[key] = []
        for t in self.test:
            l = getAllPhraseLen(t)
            v = mean(l) if len(l) > 0 else 0
            self.featurestest[key].append(v)
        self.featuresdev[key] = []
        for t in self.dev:
            l = getAllPhraseLen(t)
            v = mean(l) if len(l) > 0 else 0
            self.featuresdev[key].append(v)
        self.singleFeatureEval(key)

    def fFracContentWords(self):
        TAGSET = set(["CC","DT","EX","IN","MD","PDT","PRP$","TO","WH","WP","WP$","WRB"])
        key="fracfunc"
        self.featureids[self.nextfeatid] = FeatureMeta(key,None)
        self.nextfeatid += 1        
        self.featurestrain[key] = []
        for t in self.train:
            olap = len([x for x in t.getPosTags() if x in TAGSET])+0.0
            self.featurestrain[key].append(olap/t.getNumTokens())
        self.featurestest[key] = []
        for t in self.test:
            olap = len([x for x in t.getPosTags() if x in TAGSET])+0.0
            self.featurestest[key].append(olap/t.getNumTokens())
        self.featuresdev[key] = []
        for t in self.dev:
            olap = len([x for x in t.getPosTags() if x in TAGSET])+0.0
            self.featuresdev[key].append(olap/t.getNumTokens())
        self.singleFeatureEval(key)

    def fAvgWordLen(self):
        key="avgwordlen"
        self.featureids[self.nextfeatid] = FeatureMeta(key,None)
        self.nextfeatid += 1        
        self.featurestrain[key] = []
        for t in self.train:
            olap = [len(x.node_elt.txt) for x in t.getLeaves()]
            self.featurestrain[key].append(mean(olap))
        self.featurestest[key] = []
        for t in self.test:
            olap = [len(x.node_elt.txt) for x in t.getLeaves()]
            self.featurestest[key].append(mean(olap))
        self.featuresdev[key] = []
        for t in self.dev:
            olap = [len(x.node_elt.txt) for x in t.getLeaves()]
            self.featuresdev[key].append(mean(olap))
        self.singleFeatureEval(key)

    def fWordSpecificity(self):
        key="avgwordspec"
        self.featureids[self.nextfeatid] = FeatureMeta(key,None)
        self.nextfeatid += 1
        self.featurestrain[key] = []
        for t in self.train:
            l = [wordspecificity(x) for x in t.getPosTagNodes()]
            l = [x for x in l if x != None]
            olap = mean(l) if len(l) > 0 else 0
            self.featurestrain[key].append(olap)
        self.featurestest[key] = []
        for t in self.test:
            l = [wordspecificity(x) for x in t.getPosTagNodes()]
            l = [x for x in l if x != None]
            olap = mean(l) if len(l) > 0 else 0
            self.featurestest[key].append(olap)        
        self.featuresdev[key] = []
        for t in self.dev:
            l = [wordspecificity(x) for x in t.getPosTagNodes()]
            l = [x for x in l if x != None]
            olap = mean(l) if len(l) > 0 else 0
            self.featuresdev[key].append(olap)
        self.singleFeatureEval(key)

    def doc_perplexity(self, N):
        self.loadLM(N)
        key = "perplexity" + str(N)
        self.featureids[self.nextfeatid] = FeatureMeta(key,None)
        self.nextfeatid += 1

        self.featurestrain[key] = [];
        for (i, t) in enumerate(self.train):
            self.featurestrain[key].append(self.train_LM[i])

        self.featurestest[key] = []
        for (i, t) in enumerate(self.test):            
            self.featurestest[key].append(self.test_LM[i])

        self.featuresdev[key] = []
        for (i, t) in enumerate(self.dev):
            self.featuresdev[key].append(self.dev_LM[i])

        #print(len(self.train_LM), len(self.test_LM), len(self.dev_LM))            
        #self.singleFeatureEval(key)
        self.multi_run(key)

    
    def multi_run(self, key):
        print "\n\nEvaluate on 4 en-cs\en-de\en-es\en-fr. Taking the average"
        print "=eval, dev, "+key
        vpair, vspearman = eval.multi_eval(self.featuresdev[key], 'dev')
        print "Pairwise acc: %.4f; spearman: %.4f" % (vpair, vspearman)
        print "=eval, test, "+key
        vpair, vspearman = eval.multi_eval(self.featurestest[key], 'test')
        print "Pairwise acc: %.4f; spearman: %.4f" % (vpair, vspearman)
        
        print "====\n\n"    

    def singlefeature_run(self):
        self.fSentLen(); self.multi_run("sentlen")   
        self.fTreeDepth(); self.multi_run("treedepth");
        self.fNumPunct(); self.multi_run("numpunct")
        self.fNumWh(); self.multi_run("numwh")
        self.fLenSbar(); self.multi_run("lensbar")
        self.fAvgWordLen(); self.multi_run("avgwordlen")
        self.fMaxPhraseLen("VP"); self.multi_run("maxlenVP")
        self.fMaxPhraseLen("NP"); self.multi_run("maxlenNP")
        self.fMaxPhraseLen("PP"); self.multi_run("maxlenPP")
        self.fFracContentWords(); self.multi_run("fracfunc")
        self.fWordSpecificity(); self.multi_run("avgwordspec")

    def loadFeatures(self):
        self.fNumVerbs()
        self.fNumWh()
        self.fTreeDepth()
        self.fSentLen(); 
        self.fNumPunct()
        self.fLenSbar()
        self.fMaxPhraseLen("VP")
        self.fMaxPhraseLen("NP")
        self.fMaxPhraseLen("PP")
        self.fAvgPhraseLen()
        self.fFracContentWords()
        self.fAvgWordLen()
        self.fWordSpecificity()
        #self.fPosNgram(1)
        for n in [1,2,3,4]:        
            self.fPosNgram(n)
        self.fProdRules()
        for n in [1,2,3]:
            self.doc_perplexity(n);
        print self.featureids


## utilities
def _sliding_window(l, n):
    return [tuple(l[i:i+n]) for i in range(len(l)-n+1)]

def make_ngram_tuples(l, n):
     t = _sliding_window(l, n)
     if n == 1:
          return [((None,), s) for (s,) in t]
     return [(tuple(s[:-1]), s[-1]) for s in t]

def readtrees(filename):
    trees = []
    with open(filename) as f:
        for line in f:
            if len(line.strip()) == 0: continue
            trees.append(tree.parseString(line.strip()))
        f.close()
    return trees

def wordspecificity(posnode):
    pos = posnode.node_elt.txt
    if pos not in WNTAGDICT: return
    wnpos = WNTAGDICT[pos]
    synsets = wn.synsets(posnode.children[0].node_elt.txt,pos=wnpos)
    if len(synsets) > 0:
        return max([x.min_depth() for x in synsets])
    return

def LMperplexity():
    model = Model()    
    model.loadTrain("de"); model.loadTest("de")      

    for GRAM in [1,2,3]:        
        model.doc_perplexity(GRAM)

def run():
    model = Model()
    for tgtlan in LANGUAGES:
        print "==="+tgtlan+"==="
        # model = Model()
        model.loadTrain(tgtlan)
        model.loadTest(tgtlan)            
        if len(model.featureids) == 0: model.loadFeatures()
        # eval.eval(linear_regression(model.featurestrain.values(), model.train_labels, model.featurestest.values()))
        model.writeFeatures()
        #print('aha'); sys.exit()
        print "= eval test, combined"
        prds = liblinear_train_perdict_svr("temp.train","temp.test",True)
        eval.eval(prds, model.test_labels)
        print "= eval dev, combined"
        prds = liblinear_train_perdict_svr("temp.train","temp.dev",True)
        eval.eval(prds, model.dev_labels)

def singlefeaturerun():
    model = Model()
    model.loadTrain('de')
    model.loadTest('de')            
    model.singlefeature_run()

if __name__ == "__main__":
    run()
    #LMperplexity()
    #singlefeaturerun()
#    run_default_system()
    # run_default_eval()
    # run_test_eval()
#    aux_split(False); aux_split(True)



