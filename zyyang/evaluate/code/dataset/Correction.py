class Correction:
    """Represents a correction applied to a span of a sentence."""

    def __init__(self, id, code = None, span = None, error = None, fix = None):
        self.id = id
        self.code = code
        self.span = span
        self.error_text = error
        self.corrected_text = fix

    def i(self):
        return int(self.span[0])

    def j(self):
        return int(self.span[1])

    def __str__(self):
        return "%s %s [%s]: %s => %s" % (self.id, self.code, self.span, self.error_text, self.corrected_text)
