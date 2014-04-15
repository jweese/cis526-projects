The project has a default and baseline system for producing a set of parallel sentences in english and spanish given english and spanish articles on the same topics.

To run the default, do:
python default_pair.py > output

To run the basline, do:
python baseline_pair.py > output

The parallel sentences will be written into a file named output. To grade this file, do:
python grade.py < output

In order to run the baseline, you will need two translation model files, pruned_1_grams and pruned_2_grams, which are not included in the repository. You will need to download these files and save them in the /data directory.