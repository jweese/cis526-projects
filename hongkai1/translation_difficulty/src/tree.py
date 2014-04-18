
CLAUSE_TAGSET = set(['S','SBAR','SINV']) 
NP_TAGSET = set(['NP','WHNP'])
# PHRASE_TAGSET = set(["ADJP","ADVP","CONJP","FRAG","INTJ","LST","NAC",
#                   "NP","NX","PP","PRN","PRT","QP","RRC","UCP",
#                   "VP","WHADJP","WHAVP","WHNP","WHPP","X"])

class Tree:

    def __init__(self, node_elt):
        self.node_elt = node_elt
        self.parent = None
        self.children = []
    
    def depth(self):
        if self.parent == None: return 0
        return 1+self.parent.depth()

    def getMaxDepth(self):
        return max([leaf.depth() for leaf in self.getLeaves()])
    
    def getRoot(self):
        if self.parent == None: return self
        return self.parent.getRoot()

    def getLeaves(self):
        if len(self.children) == 0: return [self]
        leaves = []
        for child in self.children:
            leaves.extend(child.getLeaves())
        return leaves

    def getAllPhraseLen(self):
        PHRASE_TAGSET = set(["PP","NP","VP","ADJP","ADVP"])
        if len(self.children) == 0: return []
        rs = []
        for child in self.children:
            if child.node_elt.txt in PHRASE_TAGSET:
                rs.append(len(child.getLeaves()))
            rs.extend(child.getAllPhraseLen())
        return rs

    def getPosTags(self):
        return [l.parent.node_elt.txt for l in self.getLeaves()]

    def getPosTagNodes(self):
        return [l.parent for l in self.getLeaves()]
    
    def getLeafStr(self):
        s = reduce(lambda c,x: c+x.node_elt.txt+' ', self.getLeaves(), '').strip()
        return s

    def getNumTokens(self):
        return len(self.getLeaves())

    def getProdRules(self): ## get the production rules
        if len(self.children) == 0 or len(self.children[0].children) == 0: return []
        prules = [self.node_elt.txt+"_"+"_".join(c.node_elt.txt for c in self.children)]
        for child in self.children:
            prules.extend(child.getProdRules())
        return prules
    
    def __repr__(self):
        return '('+self._tree_repr(True)+')'

    def _tree_repr(self, with_dsc):
        if len(self.children) == 0: return self.node_elt.txt
        str = self.node_elt.txt if with_dsc else self.node_elt.txt.split('#')[0]
        for child in self.children:
            if len(child.children) > 0:
                str += ' ('+child._tree_repr(with_dsc)+')'
            else:
                str += ' '+child._tree_repr(with_dsc)
        return str

class ConstituentNode:
    def __init__(self, str, token_id):
        ## token_id != None -> leaf
        self.txt = str.split(' ')[0]
        self.token_id = token_id

class TreeParser:
    
    def __init__(self, txtstr):
        str_list = self._get_parse_str_list(txtstr)
        self.tree = self._parse_tree(str_list)

    def _get_parse_str_list(self, txtstr):
        str_list = []
        for item in txtstr.split():
            if item[-1] != ')': str_list.append(item); continue
            rp = []
            while len(item)>2 and item[-2] == ')':
                rp.append(')')
                item = item[:-1]
            str_list.extend([item]+rp)
        #print str_list
        return str_list

    def _parse_tree(self, str_list):
        tok_counter = 0
        tree_stack = []
        cur_tree = None
        for tok in str_list:
            if tok[0] == '(':
                tree_stack.append(Tree(ConstituentNode(tok[1:],None)))
                #print 'Appended '+tok[1:]+' now: '+repr(tree_stack)
            elif tok[-1] == ')':
                if cur_tree != None and cur_tree.node_elt=='ROOT': break
                # print cur_tree
                cur_tree = tree_stack.pop()
                if len(tok) > 1: ## leaf
                    leafstr = tok[:-1]
                    ntree = Tree(ConstituentNode(leafstr, tok_counter))
                    tok_counter += 1
                    ntree.parent = cur_tree
                    cur_tree.children.append(ntree)
                    #print 'Leaf '+repr(ntree)
                if len(tree_stack) > 0:
                    cur_tree.parent = tree_stack[-1]
                    tree_stack[-1].children.append(cur_tree)
        return cur_tree

def parseString(strtxt):
    tp = TreeParser(strtxt.strip())
    return tp.tree


if __name__ == '__main__':
    # treestr = "(ROOT (S (NP (DT An) (JJ endowed) (NN chair)) (VP (VBZ has) (VP (VBN been) (VP (VBN named) (PP (IN at) (NP (NP (DT a) (NNP Northern) (NNP Ireland) (NN university) (NN program)) (PP (IN for) (NP (NP (NNP Thomas) (NNP P.) (`` ``) (NNP Tip) ('' '') (NNP O'Neill)) (, ,) (NP (NP (DT the) (JJ late) (NNP Speaker)) (PP (IN of) (NP (NP (DT the) (NNP U.S.) (NNP House)) (PP (IN of) (NP (NNPS Representatives))))))))))))) (. .)))"
    treestr = "(ROOT (S (NP (DT The) (NN program)) (, ,) (VP (VB run) (ADVP (RB jointly)) (PP (IN by) (NP (NP (DT the) (NNP United) (NNPS Nations) (NNP University)) (CC and) (NP (NP (DT the) (NNP University)) (PP (IN of) (NP (NNP Ulster))))))) (. .)))"
    tree = parseString(treestr)
    print tree.getProdRules()
    print tree.getAllPhraseLen()
