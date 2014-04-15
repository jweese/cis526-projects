/**
 *
 */
package edu.jhu.jacana.align.resource

import edu.jhu.jacana.util.FileManager
import scala.collection.mutable.HashSet
import edu.jhu.jacana.align.util.AlignerParams

/**
 * @author Xuchen Yao
 *
 */
object PPDBsimple {

    var dbPathToken:String = FileManager.getResource("resources/ppdb/ppdb-1.0-eng-xxxl.pruned-20.lexical.pair.gz")
    var dbPathPhrase:String = FileManager.getResource("resources/ppdb/ppdb-1.0-xl-phrasal.pair.gz")
    val pairs = new HashSet[String]()
    init()
    
    def init(dbName:String = dbPathToken) {
        if (pairs.size > 0) return // already initialized
        readPPDB(dbPathToken)
        if (AlignerParams.phraseBased)
        	readPPDB(dbPathPhrase)
    }
    
    private def readPPDB(dbName:String = dbPathToken) {
        val reader = FileManager.getReader(dbName)
        var line:String = null
        line = reader.readLine()
        while (line != null) {
            val splits = line.split("\t") 
            val p1 = splits(0); val p2 = splits(1)
            pairs += makePhrase(p1, p2)
            line = reader.readLine()
        }
        reader.close()       
    }

    def isInPPDB(w1:String, w2:String): Int = {
        if (pairs.contains(makePhrase(w1,w2)) || pairs.contains(makePhrase(w2,w1)))
            return 1
        else
            return 0
    }

    private def makePhrase(s1: String, s2: String): String = s1.toLowerCase()+"::"+s2.toLowerCase()
    
    
	def main(args: Array[String]) {
        println(isInPPDB("10", "10th"))
        println(isInPPDB("agent", "officer"))
        println(isInPPDB("agent", "freedom"))
    }
}