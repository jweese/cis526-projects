#!/bin/sh

testfile=test

data_dir=/Users/ellie/Documents/Class/526/CIS526/term-project/data/es

java -mx4g -DJACANA_HOME=../ -jar ../build/lib/jacana-align.jar -m $1 -a $data_dir/$testfile/$testfile.json -o $testfile.es-en.json --reverse
python get_aligns.py $testfile.es-en.json > default.es-en

data_dir=/Users/ellie/Documents/Class/526/CIS526/term-project/data/esR

java -mx4g -DJACANA_HOME=../ -jar ../build/lib/jacana-align.jar -m $2 -a $data_dir/$testfile/$testfile.json -o $testfile.en-es.json 
python get_aligns.py $testfile.en-es.json > default.en-es

python ../../../scripts/combine-align.py default.es-en default.en-es intersect > default.intersection.a
python ../../../scripts/combine-align.py default.es-en default.en-es union > default.union.a
python ../../../scripts/grow-diag-final.py default.es-en default.en-es > default.gdf.a
cat default.en-es | python ../../../scripts/flip-align.py > default.en-es.R

echo "es-en only"
python ../../grade -g default.es-en -r ../../../data/es/$testfile/$testfile.a 
echo "en-es only"
python ../../grade -g default.en-es.R -r ../../../data/es/$testfile/$testfile.a 
echo "intersection"
python ../../grade -g default.intersection.a -r ../../../data/es/$testfile/$testfile.a 
echo "union"
python ../../grade -g default.union.a -r ../../../data/es/$testfile/$testfile.a 
echo "grow diag final"
python ../../grade -g default.gdf.a -r ../../../data/es/$testfile/$testfile.a 

