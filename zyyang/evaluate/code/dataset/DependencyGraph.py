from nltk.parse import dependencygraph

class DependencyGraph : 
    """wrapper around NLTK dependencies"""
    
    def __init__(self,dg_string) :
        if len(dg_string.strip()) == 0 :
            self.dg = dependencygraph.DependencyGraph()
        else :
            self.dg = dependencygraph.DependencyGraph(dg_string)
        self.nodelist = self.dg.nodelist
       
    def get_node_list(self) :
        return self.nodelist

    def nodes_of_type(self,pos_type) :
        """Returns nodes in the dependency graph of a certain POS (V for verb, 
        N for noun, JJ for adj, etc)"""
        nodes = list()
        for node in self.nodelist :
            if node['tag'].startswith(pos_type) :
                nodes.append(node)
        return nodes

    def nodes_gov_by_with_rel(self,node,rel) :
        """Return a list of nodes governed by node with relation rel"""
        nodes = list()
        for i in node['deps'] :
            if self.nodelist[i]['rel'] == rel :
                nodes.append(self.nodelist[i])
        return nodes

    def goving_nodes_with_rel(self,node,rel) :
        """Return a list of nodes governing node with relation rel"""
        nodes = list()
        if node['rel'] != rel :
            return nodes
        for n in self.nodelist :
            if node['address'] in n['deps'] :
                nodes.append(n)
        return nodes

    def addresses_governed_by(self,node) :
        """Return list of all node addresses governed by node, including
        the node itself"""
        nodes = set()
        nodes.add(node['address'])
        for i in node['deps'] :
            nodes = nodes.union(self.addresses_governed_by(self.nodelist[i]))
        return sorted(nodes)
 
    def tokens_governed_by(self,node) :
        """List of tokens governed by node, including node"""
        tokens = list()
        for i in self.addresses_governed_by(node) :
            tokens.append(self.nodelist[i]['word'])
        return tokens
    
    def tags_governed_by(self,node) :
        """List of POS tags governed by node, including node"""
        tags = list()
        for n in self.addresses_governed_by(node) :
            tags.append(self.nodelist[i]['tag'])
        return tags
