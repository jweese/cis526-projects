#CIS 526 TERM PROJECT

##Challenge Problem – Language Identification

Language identification is one of the most basic steps to be taken in many machine translation system. Regardless of the translation approach, one need to identify the source language of a given text in order to process it. Language identification is a well-studied problem and there are numerous way to approach the problem. The objective is to identify the source language of a sample text with minimal training data (~10-25K words) and sample size (~10 – 50 characters). In this assignment, you will be provided with a similarly restrained set of training data and sample text in multiple languages to classify. Your goal is to maximize the precision of your identification system as much as possible

##Getting Started

Under the default directory, we have provided you with a very simple language identification system written in python. To see this system in action, use the following command:

~~~
  default > output
~~~

To see the precision of this system:

~~~
  grade < output
~~~

Under the hood, this system use the training data provided to build a dictionary of words in each language with their associated frequency of occurrence. These frequencies are then used to classify un-identified text and output the identified language for each line of input into the output file. The grade system simply compare the accuracy of the output against a prepared solution with 100% accuracy line by line.
The training datasets for the different languages are provided in the train directory under data. These are provided in sentences (each sentence on a line). The development set that you should be used to test your system during your implementation process is in the dev directory. For your submission, run your system against the test set provided in the test directory.

##The Challenge

Your task is to improve the precision of your system as much as possible. As stated, there are many known algorithms developed for better language identification given a constrained dataset. To improve the precision, you can try to obtain more training data on the web and incorporating that into your system or change the way the language models are built. Additionally, you can try implementing one of these algorithms:
* Implement the n-gram Text Categorization algorithm (Section 2.3 of this paper http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.48.1958&rep=rep1&type=pdf )
* Classifying sample text based on tri-gram frequency vectors (Section 2.2 of this paper: http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.48.1958&rep=rep1&type=pdf )
* Implementing the Hidden Markov Model (Section 2.1 of this paper http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.48.1958&rep=rep1&type=pdf )
* Implement a Bayesian classifier (http://www.sepln.org/revistaSEPLN/revista/33/33-Pag155.pdf )

But the sky’s the limit. There are numerous methods of implementing language identification. The only thing you are not allowed to do is to go through the provided data and annotate the result yourself (this might be more work than it is worth).
