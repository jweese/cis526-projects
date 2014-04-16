##This script trains a translation model and language model on the EMEA training data, and tunes using the EMEA development set. It also trains a language model on the large monolingual dataset. The scripts assumes that it will be run from the main assignment directory.
#Baseline from http://homepages.inf.ed.ac.uk/pkoehn/publications/wmt2007-edinburgh-system.pdf
#BLEU: 18.6

$JOSHUA/scripts/training/pipeline.pl \
    --rundir baseline     \
    --source fr                \
    --target en                \
    --corpus data/emea_train/small_train \
    --tune data/emea_dev/dev \
    --lm-gen berkeleylm \
    --buildlm-mem 6g \
    --tuner pro   \
    --last-step TUNE  \

#Build LM from monolingual data
java -ea -mx6g -server -cp $JOSHUA/lib/berkeleylm.jar edu.berkeley.nlp.lm.io.MakeKneserNeyArpaFromText 5 baseline/monolingual_lm.gz data/monolingual/data.en

$JOSHUA/src/joshua/decoder/ff/lm/kenlm/build_binary baseline/monolingual_lm.gz baseline/monolingual_lm.kenlm

##Modify baseline/tune/1/joshua.config.final to use both LMs, and to output 1-best sentences without annotation ; decode with this configuration file (e.g. "./scripts/decode [config-file] > output").
