from numpy import mean
from collections import namedtuple
import os.path
import sys

import bleu

ROOT = "/project/cis/nlp/data/corpora/wmt_test/"
BLEUROOT = "../avgbleus"
LANGUAGES = ["cs","de","es","fr"]
#LANGUAGES = ["de","es","fr"]
YEARS = ["11","12","13"]

def sysroot(src, tgt, year):
    nsrc = src; ntgt = tgt
    if year == "11":
        if nsrc == "cs": nsrc = "cz"
        if ntgt == "cs": ntgt = "cz"
    sysroot = ROOT+"wmt"+year+"-data/plain/system-outputs/newstest20"+year+"/"+nsrc+"-"+ntgt
    assert os.path.isdir(sysroot), sysroot
    return sysroot

## return system ids that implemented all language pairs (ok)
def common_sysids(en_is_src, year):
    allids = []
    srclans = None; tgtlans = None
    if en_is_src: srclans = ["en"]*4; tgtlans = LANGUAGES
    else: tgtlans = ["en"]*4; srclans = LANGUAGES
    for src,tgt in zip(srclans, tgtlans):
        l = os.listdir(sysroot(src,tgt,year))
        l = [x.split('.')[2] for x in l if x.startswith("newstest")]
        allids.append(set(l))
    ids = allids[0]
    for idset in allids[1:]:
        ids = ids.intersection(idset)
    return ids

SYSIDS_SRCEN = {"11":common_sysids(True,"11"),
                "12":common_sysids(True,"12"),
                "13":common_sysids(True,"13")}
SYSIDS_TGTEN = {"11":common_sysids(False,"11"),
                "12":common_sysids(False,"12"),
                "13":common_sysids(False,"13")}

def getsrcfile(src, year):
    nsrc = "cz" if year == "11" and src == "cs" else src
    srcfile = ROOT+"wmt"+year+"-data/plain/references/newstest20"+year+"-ref."+nsrc
    assert os.path.isfile(srcfile), srcfile
    return srcfile

def getreffile(tgt, year):
    ntgt = "cz" if year == "11" and tgt == "cs" else tgt
    reffile = ROOT+"wmt"+year+"-data/plain/references/newstest20"+year+"-ref."+ntgt
    assert os.path.isfile(reffile), reffile
    return reffile

def getfilepairs(src, tgt, year):
#    files = namedtuple("files","reffile,sysfilelist")
    files = namedtuple("files","srcfile,reffile,sysfilelist")

    sfl = []
    srcfile = getsrcfile(src, year)
    reffile = getreffile(tgt, year)

    sysids = SYSIDS_SRCEN[year] if src == "en" else SYSIDS_TGTEN[year]
    sysfileroot = sysroot(src,tgt,year)
    for sysfilerel in os.listdir(sysfileroot):
        if not sysfilerel.startswith("newstest"): continue
        sysid = sysfilerel.split('.')[2]
        if sysid not in sysids: continue
        sysfile = os.path.join(sysfileroot,sysfilerel)
        assert os.path.isfile(sysfile), sysfile
        sfl.append(sysfile)

    return files(srcfile, reffile,sfl)

## returns a dict of "src-tgt":(reffile,sysfilelist) of specified year
def getallfilepairs(en_is_src, year):
    filepairs = {}
    srclans = None; tgtlans = None
    if en_is_src: srclans = ["en"]*4; tgtlans = LANGUAGES
    else: tgtlans = ["en"]*4; srclans = LANGUAGES    
    for src,tgt in zip(srclans, tgtlans):
        key = src+"-"+tgt
        filepairs[key] = getfilepairs(src, tgt, year)
    return filepairs

def readsents(filename):
    s = []
    with open(filename) as f:
        for line in f:
            s.append(line.strip())
    return s

def readnums(filename):
    s = []
    with open(filename) as f:
        for line in f:
            s.append(float(line.strip()))
    return s

def writefile(filename, listcontent):
    f = open(filename,'w')
    for content in listcontent:
        f.write(str(content) + '\n');
    f.close()

def writeavgbleu(en_is_src, year):
    filedict = getallfilepairs(en_is_src, year)
    for k,v in filedict.items():
        filetag = k+"."+year
        outf = os.path.join(BLEUROOT,filetag)
        refsents = readsents(v.reffile)
        syssents = []
        for i in xrange(len(refsents)):
            syssents.append([])
        for sysfile in v.sysfilelist:
            s = readsents(sysfile)
            assert len(s) == len(refsents)
            for i in xrange(len(refsents)):
                syssents[i].append(s[i])
        f = open(outf,'w')
        for ref, slist in zip(refsents, syssents):
            avgbleu = bleu.calc_avg_bleu(ref, slist)
            f.write(str(avgbleu)+'\n')
        sys.stderr.write(outf+" produced\n")
        f.close()
    
if __name__ == "__main__":
    # print get_sysids(False, "13")
    # print getfilepairs("cs","en","11")
    # print getallfilepairs(True, "11")
    for year in YEARS:
        writeavgbleu(True, year)
        writeavgbleu(False, year)
