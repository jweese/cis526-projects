Mary Xia (maryx)

CIS526

# Language Identification

## Problem Definition
Given a list of sentences or phrases in various languages, how can we determine what language each sentence is in? If given a large amount of training data and dictionaries for each language, it is possible to just search for the words in the sentence. However, what about words that do not appear in the training data? What if we have only a small amount of training data? What about words that appear in multiple languages?
Here, you are given sentences and phrases in the official European Union languages (Bulgarian, Croatian, Czech, Danish, Dutch, English, Estonian, Finnish, French, German, Greek, Hungarian, Irish, Italian, Latvian, Lithuanian, Maltese, Polish, Portuguese, Romanian, Slovak, Slovene, Spanish, and Swedish), and 16 other languages (Afrikaans, Catalan, Esperanto, Icelandic, Indonesian, Kurdish, Malay, Norwegian (Bokmal), Russian, Swahili, Turkish, Vietnamese, Welsh, Zulu). Your goal is to determine the language of each sentence/phrase out of these 38 languages. Overall, these languages incorporate the Latin, Cyrillic, or Greek, alphabet. (Note that Google Translate can detect from among 80 languages.)

## Getting Started
In the data directory are training data for each language that will be tested. Each language has one Wikipedia article of training data (wiki.language), and most languages have one piece of literature (text.language).  You will run your data on the data/test/test file, which contains one sentence/phrase per line. We have provided the simple implementation of Language ID in the home directory, which you can run by:

`default  > output`

and 

`grade < output`

or

`default | grade`

By default, this system simply creates a set of words for each language from the data. For each word in each phrase, it scans all the sets for that word and adds a point. The language with the most points is deemed the language. The grade output will tell you the number of languages you correctly identified out of the total number of test sentences. it will also tell you which languages you got wrong, in the form of an ugly confusion matrix, in the format Correct Language -> [Language(s) you mis-identified the correct language as]. 
For example, if your code misidentified a French sentence as English, and also misidentified a French sentence as Spanish, your output matrix will include:

`French -> ['English', 'Spanish']`

Multiple occurrences of the same language means that you identified the language wrong multiple times.

## The Challenge
Your job is to improve upon this system and identify more languages correctly, especially for shorter phrases and words that do not appear in the training data. The test data you are currently testing is on only a percentage of the total sentences to be tested for the leaderboard. Here are some ideas:
- Check the sentence for occurrences of trigrams for Language X (http://xrce.fr/content/download/18502/133266/file/Gref---Comparing-two-language-identification-schemes.pdf)
- Use a Markov model for trigram/word generation to figure out whether a sentence in Language X is likely (http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.48.1958&rep=rep1&type=pdf)
- Check for presence of the top n number of commonly used words for each language (http://bit.ly/1hxJVuX)
Search for characteristics of each language or use the data to generate an alphabet for each language (http://en.wikipedia.org/wiki/Wikipedia:Language_recognition_chart)
- Whatever else you want, except for adding more training data/dictionaries

To get to the baseline, you can try to find the most common trigrams (from the training data) for each language, and use those to determine whether a sentence is in that language.

## Notes
- The text data was retrieved from The Gutenberg Project for novels (not plays or poems) written in the language (except for Welsh, which only had poems). See `sources` for a full list
- The wiki data was retrieved from the Wikipedia page for a country that uses the language. E.g. The "Norway" page was used for the Norwegian language data.
- These pages are all different lengths... which is interesting. I wonder how short each language's training data needs to be in order for good language identification.

## For Jonny!
- The students can test their implementation using the `default` file on `data/test/test.small` and `data/test/answers.small`
- However, the leaderboard score should be based on running the file on `data/test/test.big` and comparing the results to `data/test/answers.big`
- Running `default | grade` or `baseline | grade` runs on the small data by default since it would be easier for students. However, you need to test on the big data so the command would be `default -t test.big | grade -k answers.big`
- The files that should not be given to the students include `baseline`, `data/test/test.big` and `data/test/answers.big`
- The default score for the _small data_ is 117 correct out of 152 sentences (76.9736842105%). The baseline score is 126 correct out of 152 sentences (82.8947368421%).
- The default score for the _big data_ is 234 correct out of 304 sentences (76.9736842105%) (pretty sure the same % is coincidence). The baseline score is 255 correct out of 304 sentences (83.8815789474%).
- `answers.person.good`, `answers.person.bad` and `answers.demo` were solely used to test the `grade` function/milestone and do not actually have anything to do with the project.