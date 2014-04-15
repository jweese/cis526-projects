package edu.jhu.jacana.align.feature

import edu.jhu.jacana.align.AlignPair
import edu.jhu.jacana.align.AlignFeatureVector
import edu.jhu.jacana.align.resource.GizaReversePTable
import edu.jhu.jacana.align.IndexLabelAlphabet.NONE_STATE
import edu.jhu.jacana.align.Alphabet
import edu.jhu.jacana.align.util.AlignerParams

object NULLReverseAlignFeature extends AlignFeature {
    
    val FEAT_NAME = "nullAlign"

    override def init() { 
        // fireup PPDB the first time
        GizaReversePTable.prob("a", "a")
    }
    
 	def addPhraseBasedFeature(pair: AlignPair, ins:AlignFeatureVector, i:Int, srcSpan:Int, j:Int, tgtSpan:Int, currState:Int, featureAlphabet: Alphabet) {
 		if (j == -1) {
		} else {
		    if (!AlignerParams.phraseBased) {
		    	if (srcSpan == 1 && tgtSpan == 1)
		    		ins.addFeature("nullAlignProb", NONE_STATE, currState, GizaReversePTable.nullprob(pair.srcTokens(i)), srcSpan, featureAlphabet)
		    		ins.addFeature("maxAlignProb", NONE_STATE, currState, GizaReversePTable.maxAlignProb(pair.srcTokens(i)), srcSpan, featureAlphabet)
		    		ins.addFeature("sumAlignProb", NONE_STATE, currState, GizaReversePTable.sumAlignProb(pair.srcTokens(i)), srcSpan, featureAlphabet)
		      }
		}
	
	}
}
