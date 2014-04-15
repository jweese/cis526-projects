package edu.jhu.jacana.align.feature

import edu.jhu.jacana.align.AlignPair
import edu.jhu.jacana.align.AlignFeatureVector
import edu.jhu.jacana.align.resource.Dictionary
import edu.jhu.jacana.align.IndexLabelAlphabet.NONE_STATE
import edu.jhu.jacana.align.Alphabet
import edu.jhu.jacana.align.util.AlignerParams

object DictionaryAlignFeature extends AlignFeature {
    
    val FEAT_NAME = "dictionary"

    override def init() { 
        // fireup PPDB the first time
        Dictionary.inDict("a", "a")
    }
    
 	def addPhraseBasedFeature(pair: AlignPair, ins:AlignFeatureVector, i:Int, srcSpan:Int, j:Int, tgtSpan:Int, currState:Int, featureAlphabet: Alphabet) {
 		if (j == -1) {
		} else {
		    if (!AlignerParams.phraseBased) {
		    	if (srcSpan == 1 && tgtSpan == 1)
		    		ins.addFeature(FEAT_NAME, NONE_STATE, currState, Dictionary.inDict(pair.srcTokens(i), pair.tgtTokens(j)), srcSpan, featureAlphabet)
		    } 
		}
	
	}
}
