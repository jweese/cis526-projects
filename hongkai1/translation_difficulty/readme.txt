All of the codes are in src folder.
 
Writeup is in: report.pdf
 
Default system is in: default.py
Baseline system is in: baseline.py
Objective function: eval.py
   - eval for comparing one langauge pairs
   - multi_eval for computing the average pairwise accuracy and p-values
 
  
Other code:
syntactic.py   Our main function.  
                        Extract syntax, surface, POS and LM features. Doing regression
bleu.py   Compute the BLEU score
tree.py   Helper script for extracting syntax features
 
lm.py    Run SRILM and generate LM features                        
filegetter.py   Auxiliary function