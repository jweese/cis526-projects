class Tree:
    """Represents a PTB-style parse tree."""

    def __init__(self):
        self.children = []
        self.label = ''
        self.i = -1
        self.j = -1
        
    def __len__(self):
        return self.j

    def setSpan(self, i, j):
        self.i = i
        self.j = j

    def setLabel(self, label):
        self.label = label

    def addChild(self, child):
        self.children.append(child)
        if len(self.children) == 1:
            self.i = child.i
        self.j = child.j

    def hasSpan(self, i, j, label = None):
        """Returns the node existing at span (i,j), or None if present. If the label is specified,
        the node's label must match it."""
        if (j - i) > (self.j - self.i):
            return None

        if i == self.i and j == self.j:
            if label is None or self.label == label:
                return self

        for child in self.children:
            if child.i <= i and child.j >= j:
                return child.hasSpan(i, j, label)

        return None

    def spansStartingAt(self, i, label=None):
        """Returns all constituents starting at position i."""

        nodes = []
        for j in xrange(i, len(self) + 1):
            node = self.hasSpan(i, j, label)
            if node is not None:
                nodes.append(node)

        return nodes

    @staticmethod
    def fromString(line):
        line = line.replace('(', ' (').replace(')', ' ) ')

        tokens = line.split()
        nodes = []
        top = None
        wordno = 0
        while len(tokens) > 0:
            token = tokens.pop(0)

            if token.startswith('('):
                # Here, we have a new node. Set its label
                current_node = Tree()

                if len(token) == 1:
                    current_node.setLabel('TOP')
                else:
                    current_node.setLabel(token[1:])

                nodes.append(current_node)

            elif token.startswith(')'):
                top = nodes.pop()
                if len(nodes) > 0:
                    nodes[-1].addChild(top)

            else:
                # Leaf node.
                node = Tree()
                node.setLabel(token)
                node.setSpan(wordno, wordno + 1)
                nodes[-1].addChild(node)
                wordno += 1

        return top

    def __str__(self):
        if len(self.children) == 0:
            return self.label

        else:
            return "(%s[%d-%d] %s)" % (self.label, self.i, self.j, " ".join([x.__str__() for x in self.children]))
