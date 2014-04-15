package edu.jhu.jacana.align.feature

import edu.jhu.jacana.align.AlignPair
import edu.jhu.jacana.align.AlignFeatureVector
import edu.jhu.jacana.align.resource.Dictionary
import edu.jhu.jacana.align.IndexLabelAlphabet.NONE_STATE
import edu.jhu.jacana.align.Alphabet
import edu.jhu.jacana.align.util.AlignerParams
import java.lang.Math.abs;

object PositionAlignFeature extends AlignFeature {
    
    val FEAT_NAME = "position"

 	def addPhraseBasedFeature(pair: AlignPair, ins:AlignFeatureVector, i:Int, srcSpan:Int, j:Int, tgtSpan:Int, currState:Int, featureAlphabet: Alphabet) {
 		if (j == -1) {
		} else {
		    if (!AlignerParams.phraseBased) {
		    	if (srcSpan == 1 && tgtSpan == 1)
		    		ins.addFeature(FEAT_NAME, NONE_STATE, currState, abs((i.toFloat / pair.srcLen) - (j.toFloat / pair.tgtLen)), srcSpan, featureAlphabet)
		    } 
		}
	
	}
}
