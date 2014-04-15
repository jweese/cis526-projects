/**
 *
 */
package edu.jhu.jacana.align.resource

import scala.collection.mutable
import edu.jhu.jacana.util.FileManager
import scala.collection.mutable.HashMap
import edu.jhu.jacana.align.util.AlignerParams

/**
 * @author Xuchen Yao
 *
 */
object GizaReverseAlign{

    var dbPathToken:String = FileManager.getResource("resources/giza/en-es.aligns")
    val pairs = new mutable.HashMap[String, HashMap[Int, Int]]()
    init()
    
    def init(dbName:String = dbPathToken) {
        if (pairs.size > 0) return // already initialized
        readAlign(dbPathToken)
    }
    
    private def readAlign(dbName:String = dbPathToken) {
        val reader = FileManager.getReader(dbName)
        var line:String = null
        line = reader.readLine()
        //train0  2-3 1-2 0-0 7-7
        while (line != null) {
            val splits = line.split("\t") 
            val name = splits(0); val a = splits(1); 
            val amap = new mutable.HashMap[Int, Int]()
            val aligns = a.split(" ")
            aligns.foreach( ij => amap.put(ij.split("-")(0).toInt, ij.split("-")(1).toInt))
            pairs.put(name, amap)
            line = reader.readLine()
        }
        reader.close()       
    }

    def isAligned(name:String, i:Int, j:Int): Float = {
        val a = pairs.getOrElse(name, HashMap[Int, Int]())
        if (j == a.getOrElse(i, -1)) {
         return 1 
        }
        return 0
    }

}
