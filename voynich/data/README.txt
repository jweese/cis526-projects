Wikipedia articles, annotated for parallelism, version 0.2.

Release date 2010 June 2nd.

Please contact chrisq@microsoft.com with questions or comments.

Data was originally released at 
http://research.microsoft.com/en-us/people/chrisq/WikiDownload.aspx

There are two datasets: annotated document pairs, and parallel test
sentences.


ANNOTATED DOCUMENT PAIRS
========================

Data in three langauges is being released: German, Bulgarian, and
Spanish.  There's a subdirectory for each language with its two letter
code (de, bg, es, respectively).

In each directory, there are five files, all encoded as UTF-8 (no
BOM, just straight UTF-8).

First, there is the original data, unaligned, stored in two files:

 - orig.enu.snt: The original sentences extracted from the English
   Wikipedia articles.  Each article begins with a title line and
   ends with an empty line.

 - orig.XXX.snt: the original sentences extracted from the other
   language Wikipedia.

These files have the same number of articles, but do not have the same
number of sentences per article.

Next we have the parallel (or nearly parallel) sentence pairs from
all the articles.  There are three files here:

 - pairs.enu.snt: the English parallel sentences / segments

 - pairs.XXX.snt: the parallel sentences / segments in the other
   language

 - pairs.label: a file indicating which articles and which line numbers
   the parallel sentences came from.  The format is:

<ForeignArticleTitle><TAB><EnglishArticleTtile><TAB><ForeignSentNum><TAB><EnglishSentNum>

Note that the sentence numbers are 0 based, and do not include the
article title in the list of sentences.

There should be the same number of lines in each of these files.


PARALLEL TEST SENTENCES
=======================

Inside the testdata directory, there are also files with just
parallel sentences extracted from Wikipedia to be used as held out
test data.  Again there is one subdirectory per language pair.  Each
subdirectory has two files: the parallel sentences in English and in
the foreign language.

CHANGE LOG
==========

v0.1 June 2nd: initial release.
v0.2 June 2nd: also include the test data.
