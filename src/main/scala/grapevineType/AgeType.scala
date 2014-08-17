package grapevineType

/**
 * Age uses 1 byte (8 bits: 0 - 255)
 */
class AgeType extends SingleBitsType(8, 0, 120) {
  this.signed = false
  override def getId(): Int = 8
  override def toByteArray(goalSize:Int) = {
    val size = if (goalSize == -1) 1 else goalSize
    super.toByteArray(size)
  }
}
