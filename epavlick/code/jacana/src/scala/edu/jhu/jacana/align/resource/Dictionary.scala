/**
 *
 */
package edu.jhu.jacana.align.resource

import scala.collection.mutable
import edu.jhu.jacana.util.FileManager
import scala.collection.mutable.HashSet
import edu.jhu.jacana.align.util.AlignerParams

import java.io.PrintStream

/**
 * @author Xuchen Yao
 *
 */
object Dictionary{

    var dbPathToken:String = FileManager.getResource("resources/dictionary/dictionaries/dict.es")
    val dict = new HashSet[String]();
    init()

    def init(dbName:String = dbPathToken) {
        if (dict.size > 0) return // already initialized
        readDict(dbPathToken)
    }
    
    private def readDict(dbName:String = dbPathToken) {
        val reader = FileManager.getReader(dbName)
        var line:String = null
        line = reader.readLine()
        while (line != null) {
            val splits = line.split("\t") 
            val s = splits(0);
            splits.foreach( t=> dict += makePhrase(s, t))
              line = reader.readLine()
        }
        reader.close()       
    }

    def inDict(w1:String, w2:String): Float = {
        val p = makePhrase(w1,w2)
        if(dict.contains(p)) return 0
        val p2 = makePhrase(w2,w1)
        if(dict.contains(p2)) return 0
        return 1
    }

    private def makePhrase(s1: String, s2: String): String = s1+"::"+s2 //s1.toLowerCase()+"::"+s2.toLowerCase()
}
