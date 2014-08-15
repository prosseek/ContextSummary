package grapevineType


import util.conversion.BitSetTool

import scala.collection.BitSet

/**
 * Created by smcho on 8/14/14.
 */
abstract class BitsType extends GrapevineType with RangeChecker {
  def getTotalBytes(bits:List[Int]) = {
    val size = bits.size
    if (size % 8 == 0) size/8
    else size/8 + 1
  }

  // http://stackoverflow.com/questions/25319546/adding-a-list-from-different-starting-point-in-scala
  def psum (index:Int, bits:List[Int]) = bits.drop(index).sum

  // http://stackoverflow.com/questions/25315590/checking-values-in-lists-with-scala
  // check(aValue, a._2, a._3) && check (bValue, b._2, b._3) && check(cValue, c._2, c._3)
  def check(values:List[Int], ranges:List[(Int, Int)]) : Boolean = {
    (values zip ranges).forall{case (a,(b,c)) => check(a,b,c)}
  }

  //  BitSetTool.intToBitSet(values(0), bits(1) + bits(2) + 0) ++
  //  BitSetTool.intToBitSet(values(1),           bits(2) + 0) ++
  //  BitSetTool.intToBitSet(values(2),                   + 0)
  def shiftAndJoin(values:List[Int], bits:List[Int]) = {
    // http://stackoverflow.com/questions/25319725/aggregation-of-bitset-collection-data-in-scala
    (values zipWithIndex) map { case (v, i) => BitSetTool.intToBitSet(v, psum(i+1, bits)) } reduce {_ ++ _}
  }

  //  val c = bs.filter(v => v >= psum(3, bits) && v < psum(2, bits)).map(_ - psum(3, bits)) <- lower bits
  //  val b = bs.filter(v => v >= psum(2, bits) && v < psum(1, bits)).map(_ - psum(2, bits))
  //  val a = bs.filter(v => v >= psum(1, bits) && v < psum(0, bits)).map(_ - psum(1, bits)) <- higher bits
  def splitBitSets(bs:BitSet, bits:List[Int]) = {
    (List[BitSet]() /: Range(0, bits.size)) { (acc, index) =>
      acc ++ List(bs.filter(v => v >= psum(index+1, bits) && v < psum(index, bits)).map(_ - psum(index+1, bits)))
    }
  }
}
