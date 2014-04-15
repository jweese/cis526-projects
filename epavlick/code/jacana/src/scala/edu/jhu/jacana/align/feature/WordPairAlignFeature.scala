/**
 *
 */
package edu.jhu.jacana.align.feature

import edu.jhu.jacana.align.AlignPair
import edu.jhu.jacana.align.AlignFeatureVector
import com.rockymadden.stringmetric.similarity._
import edu.jhu.jacana.align.AlignTrainRecord
import edu.jhu.jacana.align.util.AlignerParams
import edu.jhu.jacana.align.IndexLabelAlphabet.NONE_STATE
import edu.jhu.jacana.align.IndexLabelAlphabet.NULL_STATE
import edu.jhu.jacana.align.IndexLabelAlphabet
import edu.jhu.jacana.align.Alphabet
import scala.collection.immutable.HashSet
import scala.collection.mutable.ArrayBuffer

/**
 * A bunch of string similarity measures from [[https://github.com/rockymadden/stringmetric/  stringmetric]]
 * @author Xuchen Yao
 *
 */
object WordPairAlignFeature extends AlignFeature {

	def getStringSimilarities(srcToken: String, tgtToken: String): List[Tuple2[String, Double]] = {
	    val buffer = new ArrayBuffer[(String,Double)]()
	    buffer.append((srcToken+"::"+tgtToken, 1.0))
	    return buffer.toList
	}
    
 	override def addTokenBasedFeature(pair: AlignPair, ins:AlignFeatureVector, i:Int, j:Int, currState:Int, featureAlphabet: Alphabet) {
		if (j == -1) {
		} else {
            val srcToken = pair.srcTokens(i)
			val tgtToken = pair.tgtTokens(j)
			for ((featureName, value) <- getStringSimilarities(srcToken, tgtToken))
				ins.addFeature(featureName, NONE_STATE, currState, value, 1, featureAlphabet) 
		}
	}

	def addPhraseBasedFeature(pair: AlignPair, ins:AlignFeatureVector, i:Int, srcSpan:Int, j:Int, tgtSpan:Int, currState:Int, featureAlphabet: Alphabet){
        //var averageLength = 0.5 * (srcSpan + tgtSpan)
	    var averageLength = (if (srcSpan < tgtSpan) 0.5 * (srcSpan + tgtSpan) else srcSpan)*AlignerParams.phraseWeight
	    //var averageLength = if (srcSpan < tgtSpan) srcSpan else 0.5 * (srcSpan + tgtSpan)
		if (j == -1) {
		} else {
            val srcToken = pair.srcTokens.slice(i, i+srcSpan).mkString("")
           	val tgtToken = pair.tgtTokens.slice(j, j+tgtSpan).mkString("")
           	
        	//if (srcToken == "veryfew" && tgtToken == "poorlyrepresented")
        	//if (srcToken == "results" && tgtToken == "isaresult")
        	if (false && (srcToken == "saidinaninterviewwith" && tgtToken == "told" ||
        	      srcToken == "results" && tgtToken == "isaresult" ||
        	      srcToken == "asyousay" && tgtToken == "right" ||
        	      srcToken == "UnitedAirlines" && tgtToken == "UnitedAirways" ||
        	      srcToken == "appeared" && tgtToken == "informationindicated"))
        	    // for debug, ASSUME we have a good lexical resource to align
        	    // "very few" and "poorly represented" (score = 5.0)
        	    // then check whether they are really aligned together 
        		ins.addFeature("feature", NONE_STATE, currState, 500.0, srcSpan, featureAlphabet) 
            else {
				for ((featureName, value) <- getStringSimilarities(srcToken, tgtToken))
					ins.addFeature(featureName, NONE_STATE, currState, value*averageLength, srcSpan, featureAlphabet) 
			}
		}
	}
}
