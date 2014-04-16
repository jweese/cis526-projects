#!/bin/bash

PATH_TO_M2=/Users/kunpeng/Dropbox/CIS526/Project/release2.3.1/m2scorer/scripts

dir=$1

reffile=$dir/gold.m2
predfile=$dir/system.m2

python $PATH_TO_M2/m2scorer.py -v $predfile $reffile > $dir/prediction.log

tail -9 $dir/prediction.log
