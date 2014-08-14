package grapevineType

/**
 * Created by smcho on 8/11/14.
 */
class StringType extends GrapevineType {
  var value:String = ""
  override def set(value: Any): Unit = {
    this.value = value.asInstanceOf[String]
  }
  override def get(): String = {
    value
  }
  override def toByteArray(goalSize: Int): Array[Byte] = {
    null
  }
  override def fromByteArray(b: Array[Byte]): Boolean = {
    true
  }
}