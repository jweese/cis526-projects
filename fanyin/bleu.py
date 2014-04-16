from collections import Counter # multiset represented by dictionary
import math

# calculates the bleu score based on a reference translation
# uses ngrams up to length 4 and includes a brevity penalty

def ngram(l, n):
    if n == 1:
        return l
    elif n == 2:
        return zip(l[:-1], l[1:])
    elif n == 3:
        return zip(l[:-2], l[1:-1], l[2:])
    else:
        return zip(l[:-3], l[1:-2], l[2:-1], l[3:])

def corpuslen(system, n):
    return float (sum(len(sentence) - n + 1 for sentence in system) + 1)

def get_matches(system, ref, n):  
    total = 0;
    for (s,r) in zip(system, ref):
        if len(r) < n or len(s) < n:
            continue
        a = Counter(ngram(r, n))
        b = Counter(ngram(s, n))
        total += sum ((a & b).values())
    return float(total + 1)

def bleu_score(system, ref):
  score = 0.0
  for i in xrange(1, 5):
      score += math.log (get_matches(system, ref, i) / corpuslen(system, i))
  score /= 4
  penalty = min (1 - corpuslen(ref, 1) / corpuslen(system, 1), 0)
  return math.exp(score + penalty)
