import os

class Sentence:
    """Represents an input sentence."""

    def __init__(self, id, line, basedir = None):
        self.id = id.rstrip()
        self.line = line.rstrip()
        self.tokens = self.line.split()
        self.postags = None
        self.parse = None
        self.corrections = []
        self.basedir = basedir
        self.depgraph = None

    def __len__(self):
        return self.len()

    def len(self):
        return len(self.tokens)

    def docid(self):
        """Returns the document ID, which is assumed to be the first field of the ID, with
        periods as field delimiters."""
        return self.id.split('.')[0]

    def tokens(self):
        return self.tokens

    def pos(self):
        return self.postags

    def setPOS(self, POSstr):
        self.postags = POSstr.rstrip().split()

    def setParse(self, tree):
        self.parse = tree

    def getParse(self):
        return self.parse

    def setDepgraph(self,dg) :
        self.depgraph = dg

    def getDepgraph(self) :
        return self.depgraph

    def addCorrection(self, correction):
        """Add a correction object to the list of corrections for the sentence."""
        self.corrections.append(correction)

    def getCorrections(self, type):
        """Get all corrections of a particular type."""
        list = []
        for c in self.corrections:
            if c.code == type:
                list.append(c)

        return list

    def applyCorrection(self, correction):
        """Applies a single Correction instance to the text of a sentence, returning the corrected
        string. Does not modify the underlying Sentence object."""
        words = [x for x in self.tokens]
        i = int(correction.span[0])
        j = int(correction.span[1])
        words[i:j] = correction.corrected_text.split()
        return " ".join(words)

    def applyCorrections(self):
        """Applies all the collected corrections for a sentence, returning the corrected string."""
        words = [x for x in self.tokens]
        for c in self.corrections:
            i = int(c.span[0])
            j = int(c.span[1])
            words[i:j] = c.corrected_text.split()

        return " ".join(words)

    def __str__(self):
        """Just prints the words of the sentence."""
        return ' '.join(self.tokens)

    def getFeature(self, feature, span):
        """Returns all feature values found in the specified file."""

        i,j = span
        filename = '%s/features/%s/%s/%s-%s/%s' % (self.basedir, self.docid(), self.id, i, j, feature)
        if not os.path.exists(filename):
            return "%s:N/A"%feature

        features = []
        for line in open(filename):
            features += line.rstrip().split()

        return " ".join(features)
        

    def getFeatures(self, span):
        """Returns a list of feature names available for the span. The feature names are feature
        templates; that is, the names correspond to the files found within feature directory for the
        span. Each of these feature templates may actually contain many different feature
        instantiations of the template. For example, a "context" feature may define different left
        and right context features, while a "TSG" feature may fire a number of particular
        fragments."""

        i,j = span
        return os.listdir('%s/features/%s/%s/%s-%s' % (self.basedir, self.docid(), self.id, i, j))

    def spans(self, min=0, max=-1):
        """Generator function that iterates over all the spans of the sentence, subject to a minimum
        and maximum span length, returning tuples representing the span. min and max control the
        minimum and maximum span widths. So, for example, to get all insertion points, you'd used
        (0,0), and to get all single words, you'd use (1,1)."""

        if max == -1:
            max = self.len()

        for width in range(min,max+1):
            for i in range(self.len() - width + 1):
                yield (i, i+width)

    ### Example generators iterating over spans ###

    def pos_spans(self, label):
        """This generator returns every single-word span with a given POS tag."""
        if self.pos() is not None:
            for span in self.spans(1,1):
                if label == self.pos()[span[0]]:
                    yield span, self.pos()[span[0]]


    def constituent_spans(self, label=None):
        """This generator demonstrates how to wrap the generic spans() function above inside some
        more restrictive criteria. In this case, we iterate over all spans, but only yield (return)
        the span if it happens to be a constituent according to the parse tree. If an optional label
        is supplied, we only yield for labeled spans."""

        if self.getParse() is not None:
            for span in self.spans():
                constituent = self.getParse().hasSpan(span[0], span[1], label)
                if constituent is not None:
                    yield (span,constituent)

    def insertion_spans(self):
        """Convenience function that returns all spans representing positions between words."""
        return self.spans(0,0)

    def word_spans(self):
        """Convenience function that returns all single-word spans."""
        return self.spans(0,1)

class SentenceIterator:
    """Iterates over all the spans of a sentence subject to some filters."""
