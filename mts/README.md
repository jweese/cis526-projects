# CIS 526 Final Project: Chinese Word Segmentation

### Project Write-Up

The full project write-up can be viewed by opening FinalProjectWriteUp.pdf.

### Running the Provided Code

Running the default system with the default settings produces all one- and two-character segmentations:

```
$ default | grade
Producing all segmentations of length 1, 2
Precision: 0.267500
   Recall: 0.912897
  F-Score: 0.413759
```

To specify which word lengths to use, the `-l` (or `--lengths`) flag can be used:

```
$ default -l 1,2,3 | grade
Producing all segmentations of length 1, 2, 3
Precision: 0.188900
   Recall: 0.956425
  F-Score: 0.315489
```

Running the baseline system can be accomplished in a similar manner:

```
$ baseline | grade
Constructing lexicon... 88119 unique words
Segmenting sentences using maximum matching algorithm...
Precision: 0.830889
   Recall: 0.919138
  F-Score: 0.872788
```
