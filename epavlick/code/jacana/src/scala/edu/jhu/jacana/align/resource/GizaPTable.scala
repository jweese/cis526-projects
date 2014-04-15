/**
 *
 */
package edu.jhu.jacana.align.resource

import scala.collection.mutable
import edu.jhu.jacana.util.FileManager
import scala.collection.mutable.HashMap
import edu.jhu.jacana.align.util.AlignerParams
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashSet

import java.io.PrintStream

/**
 * @author Xuchen Yao
 *
 */
object GizaPTable {

    var dbPathToken:String = FileManager.getResource("resources/giza/es-en.ptable")
    var nullPathToken:String = FileManager.getResource("resources/giza/es-en.nullalign")
//    var dbPathToken:String = FileManager.getResource("resources/giza/en-es.ptable")
//    var nullPathToken:String = FileManager.getResource("resources/giza/en-es.nullalign")
    val pairs = new mutable.HashMap[String, Float]()
    val nulls = new mutable.HashMap[String, Float]()
    val vocab = new HashSet[String]();
    init()
    
    def init(dbName:String = dbPathToken) {
        if (pairs.size > 0) return // already initialized
        readPTable(dbPathToken)
        readNullTable(nullPathToken)
    }
    
    private def readPTable(dbName:String = dbPathToken) {
        val reader = FileManager.getReader(dbName)
        var line:String = null
        line = reader.readLine()
        while (line != null) {
            val splits = line.split("\t") 
            val s = splits(0); val t = splits(1); val p = splits(2)
            pairs.put(makePhrase(s, t), p.toFloat)
            line = reader.readLine()
        }
        reader.close()       
    }

    private def readNullTable(dbName:String = dbPathToken) {
        val reader = FileManager.getReader(dbName)
        var line:String = null
        line = reader.readLine()
        while (line != null) {
            val splits = line.split("\t") 
            val w = splits(0); val p = splits(1); 
            nulls.put(w, p.toFloat)
            vocab += w
            line = reader.readLine()
        }
        reader.close()       
    }

    def prob(w1:String, w2:String): Float = {
        val p = makePhrase(w1,w2)
        if (pairs.getOrElse(p, 0) != 0){
        }
        return pairs.getOrElse(p, 0)
    }

    def nullprob(w:String): Float = {
        return nulls.getOrElse(w, 0)
    }

    def sumAlignProb(w:String): Float = {
	    val buffer = new ArrayBuffer[Float]()
      vocab.foreach( v => buffer.append(prob(w,v))) 
      return buffer.sum
    }

    def maxAlignProb(w:String): Float = {
	    val buffer = new ArrayBuffer[Float]()
      vocab.foreach( v => buffer.append(prob(w,v))) 
      return buffer.max
    }

    private def makePhrase(s1: String, s2: String): String = s1+"::"+s2 //s1.toLowerCase()+"::"+s2.toLowerCase()
}
