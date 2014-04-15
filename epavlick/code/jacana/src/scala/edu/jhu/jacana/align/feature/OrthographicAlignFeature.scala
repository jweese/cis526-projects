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
import scala.math.abs

/**
 * A bunch of string similarity measures from [[https://github.com/rockymadden/stringmetric/  stringmetric]]
 * @author Xuchen Yao
 *
 */
object OrthographicAlignFeature extends AlignFeature {

	object Types {
        val LEVENSHTEIN = "Levenshtein"
        val COMMON_PREFIX = "numCommonPrefix"
        val COMMON_SUFFIX = "numCommonSuffix"
        val IDENTICAL_MATCH_IGNORE_CASE = "identicalMatchIgnoreCase"
        val IDENTICAL_MATCH_IGNORE_VOWELS = "identicalMatchIgnoreVowels"
        val WORD_LENGTH_DIFF = "wordLengthDiff"
        val BOTH_LT_4 = "bothLessThan4"
    }
    import Types._
	
	def lengthDiff(token1:String, token2:String): Double = { return abs(token1.length() - token2.length()) }
	def lt4(token1:String, token2:String): Double = { 
    if (token1.length() <= 4 &&  token2.length() <= 4 ) return 1.0
    return 0.0
  }
		
	def commonPrefix(token1:String, token2:String): Double = {
	    val t1 = token1.toLowerCase()
	    val t2 = token2.toLowerCase()
	    val lmin = Math.min(token1.length(), token2.length())
	    val min =  Math.min(lmin, 3)
	    var i = 0
	    while (i < min) {
	        if (t1.charAt(i) != t2.charAt(i))
	            return 1.0
	        i += 1
	    }
	    return 0.0
	}
		
	def commonSuffix(token1:String, token2:String): Double = {
	    val l1 = token1.length()
	    val l2 = token2.length()
	    val t1 = token1.toLowerCase()
	    val t2 = token2.toLowerCase()
	    val lmin = Math.min(token1.length(), token2.length())
	    val min =  Math.min(lmin, 3)
	    var i = 0
	    while (i < min) {
	        if (t1.charAt(l1-1-i) != t2.charAt(l2-1-i))
	            return 1.0
	        i += 1
	    }
	    return 0.0
	}
	
	def getStringSimilarities(srcToken: String, tgtToken: String): List[Tuple2[String, Double]] = {
	    val buffer = new ArrayBuffer[(String,Double)]()
	    buffer.append((LEVENSHTEIN, LevenshteinMetric.compare(srcToken, tgtToken).get))
	    buffer.append((COMMON_PREFIX, commonPrefix(srcToken, tgtToken)))
	    buffer.append((COMMON_SUFFIX, commonSuffix(srcToken, tgtToken)))
	    buffer.append((WORD_LENGTH_DIFF, lengthDiff(srcToken, tgtToken)))
	    buffer.append((BOTH_LT_4, lt4(srcToken, tgtToken)))
		  if (srcToken.toLowerCase() == tgtToken.toLowerCase()) {
			  buffer.append((IDENTICAL_MATCH_IGNORE_CASE, 1.0))
      }
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
        		ins.addFeature(COMMON_PREFIX, NONE_STATE, currState, 500.0, srcSpan, featureAlphabet) 
            else {
				for ((featureName, value) <- getStringSimilarities(srcToken, tgtToken))
					ins.addFeature(featureName, NONE_STATE, currState, value*averageLength, srcSpan, featureAlphabet) 
			}
		}
	}

}
