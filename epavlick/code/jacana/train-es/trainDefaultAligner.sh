#!/bin/sh

data_dir=/Users/ellie/Documents/Class/526/CIS526/term-project/data/es

#Step 1 : build model (run if you have added new features)
ant -f ../build.align.xml

#Step 2 : run with es source and en target
java -mx4g -DJACANA_HOME=../ -jar ../build/lib/jacana-align.jar -r $data_dir/train/train.json -d $data_dir/dev/dev.json -t $data_dir/test/test.json -m ./es-en-align.model --reverse --default

#Step 3 : run with en source and es target

data_dir=/Users/ellie/Documents/Class/526/CIS526/term-project/data/esR

java -mx4g -DJACANA_HOME=../ -jar ../build/lib/jacana-align.jar -r $data_dir/train/train.json -d $data_dir/dev/dev.json -t $data_dir/test/test.json -m ./en-es-align.model --default
