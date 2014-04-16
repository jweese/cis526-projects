import sys
import os
from itertools import izip

from Sentence import *
from Tree import *
from Correction import *
from DependencyGraph import *

class DataSetIterator:
    """Iterates over a data set, returning Sentence objects until there are no more."""

    def __init__(self, dataset):
        self.index = 0
        self.dataset = dataset
        
    def __iter__(self):
        return self

    def next(self):
        if self.index == self.dataset.len():
            raise StopIteration

        self.index += 1
        return self.dataset.get(self.index-1)


class DataSet:
    """A class representing a dataset. It provides routines for iterating over the sentences
    in the dataset, splitting it up into training, dev, and test, and so on."""

    def __init__(self, dir, maxitems = 0):
        self.dir = dir
        self.sentences = []
        self.sentenceIDs = {}

        if not os.path.exists(dir + "/sentences.tok"):
            sys.stderr.write("* FATAL: can't find '%s/sentences.tok'" % (dir))
            sys.exit(1)

        if not os.path.exists(dir + "/ids"):
            sys.stderr.write("* FATAL: can't find '%s/ids'" % (dir))
            sys.exit(1)

        if not os.path.exists(dir + "/corrections"):
            sys.stderr.write("* WARNING: can't find %s/corrections'" % (dir))
#            sys.exit(1)

        for i,(sid,line) in enumerate(izip(open(dir + "/ids"), open(dir + "/sentences.tok"))):
            sid = sid.rstrip()
            line = line.rstrip()

            self.sentences.append(Sentence(sid,line, basedir = self.dir))
            self.sentenceIDs[sid] = self.sentences[-1]
            if i == maxitems - 1:
                break

        if os.path.exists(dir + "/corrections") :
            for i,line in enumerate(open(dir + "/corrections")):
                
                # 829.3.1 52-53   Wcip    inhabitable     otherwise uninhabitable 
                tokens = line.strip().split('\t')
                if len(tokens) == 6:
                    id, spanstr, code, bad, good, comments = tokens
                    i,j = spanstr.split('-')
                elif len(tokens) == 5:
                    id, spanstr, code, bad, good = tokens
                    i,j = spanstr.split('-')
                elif len(tokens) == 4:
                    id, spanstr, code, good = tokens
                    i,j = spanstr.split('-')
                    if not id in self.sentenceIDs:
                        continue
                    bad = " ".join(self.sentenceIDs[id].tokens[int(i):int(j)])
		else:
			continue # how to handle this?
                c = Correction(id, code, (i,j), bad, good.rstrip())

                if not id in self.sentenceIDs:
                    continue
                
                self.sentenceIDs[id].addCorrection(c)

        if os.path.exists(dir + "/sentences.parses"):
            for i,line in enumerate(open(dir + "/sentences.parses")):
                self.sentences[i].setParse(Tree.fromString(line.rstrip()))
                if i == maxitems - 1:
                    break;

        if os.path.exists(dir + "/sentences.pos"):
            for i,line in enumerate(open(dir + "/sentences.pos")):
                self.sentences[i].setPOS(line.rstrip())
                if i == maxitems - 1:
                    break;

        if os.path.exists(dir + "/sentences.dep") :
            for i,line in enumerate(open(dir + "/sentences.dep")) :
                dg = DependencyGraph(line.replace('\\n','\n'))
                if dg is not None: self.sentences[i].setDepgraph(dg)
                if i == maxitems - 1 :
                    break

    def __iter__(self):
        return DataSetIterator(self)

    def len(self):
        return len(self.sentences)

    def get(self, index):
        return self.sentences[index]

    def getID(self, index):
        return self.sentenceIDs[index]

    def ids(self):
        return self.sentenceIDs.keys()

    @staticmethod
    def test():
        d = DataSet('/export/projects/grammar13/nucle')
        for i,s in enumerate(d):
            print i+1, len(s), s
            if i >= 9:
                break

if __name__ == '__main__':
    DataSet.test()
    
