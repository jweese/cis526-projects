#!/bin/bash

formated='formatted'
input=$1

mkdir $formated

python evaluate/code/format_data.py $formated/ -d $input

PATH_TO_M2=evaluate/m2scorer/scripts

#dir=$1

reffile=$formated/gold.m2
predfile=$formated/system.m2

python $PATH_TO_M2/m2scorer.py -v $predfile $reffile > $formated/prediction.log

tail -9 $formated/prediction.log
