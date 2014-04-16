##This script trains a translation model and language model on the EMEA training data, and tunes using the EMEA development set. The scripts assumes that it will be run from the main assignment directory.
#The important parameters are:
## rundir -- The name of the directory in which all of the output will be saved
## source -- The suffix that identifies the source language files
## target -- The suffix that identifies the target language files
## corpus -- The location of the training data. This defines a file prefix that, when concatenated with the source/target suffixes above, gives the actual path to the data.
## tune -- The prefix for the location of the development/tuning data.


$JOSHUA/scripts/training/pipeline.pl \
    --rundir default                \
    --source fr                \
    --target en                \
    --corpus data/emea_train/train       \
    --tune data/emea_dev/dev  \
    --lm-gen berkeleylm \
    --last-step tune  \
    --buildlm-mem 6g

cp default/tune/1/joshua.config.final default/joshua.config

#To decode, change two lines in the config file:
#1. Change "top-n = 300" to "top-n = 1". This tells Joshua to output the best translation, instead of the 300 best translations.
#2. Change "output-format = ..." to "output-format = %s". The first tells Joshua to print out the translations with annotations like the sentence number and model cost, while the second will output only the translations so that they can be evaluated by the grading script.
