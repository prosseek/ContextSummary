package summary

import util.json.Json
import scala.collection.mutable.{Map => MMap}
import scala.collection.immutable.Set

object JsonSummary {
  def apply(map: Map[String, Any]) = {
    val summary = new JsonSummary
    summary.create(map)
    summary
  }
  def apply(filePath: String) = {
    val summary = new JsonSummary
    summary.loadJson(filePath)
    summary
  }
  def name = "json"
}

/**
  * JsonSummary builds its map from other map, or parsing
  * Json file.
  */
class JsonSummary extends Summary {
  // JSon summary is an internal map
  var map : MMap[String, Any] = _

  // when the map is created from a file, the link and contents are reserved.
  private var filePath:String = ""
  private var contents:String = ""

  /**
    * create a summary from the given map
    *
    * @param map
    */
  override def create(map: Map[String, Any]): Unit = {
    if (!Json.isSimpleJson(map))
      throw new RuntimeException(s"Error the input map is not in supported format: ${map.mkString(":")}")
    this.map = MMap(map.toSeq:_*)
    this.contents = Json.build(this.map).toString
  }

  // get
  override def get(label: String): Option[Any] = {
    if (map.keySet.contains(label)) Some(map(label))
    else None
  }

  // get information
  override def schema: Option[Set[String]] = {
    Some(map.keySet.toSet)
  }

  override def size: Int = {
      contents.length
  }

  // transform
  override def serialize: Array[Byte] = {
    if (contents == "")
      throw new RuntimeException(s"No contents to serialize")
    else
      _serialize(JsonSummary.name, serializedContent)
  }

  override def serializedContent: Array[Byte] = {
    contents.getBytes
  }

  override def deserialize(ba: Array[Byte]): Map[String, Any] = {
    val (name, content) = _deserialize(ba)
    if (name == JsonSummary.name) {
      val str = new String(content)
      Json.parse(str)
    }
    else
      throw new RuntimeException(s"The header is not ${JsonSummary.name}")
  }

  // I/O
  override def loadJson(filePath: String): Any = {
    val loadedContents = _loadJsonContent(filePath)
    this.map = _toMMap(Json.parse(loadedContents))
    this.contents = Json.build(this.map).toString
    this.filePath = filePath
    this.map
  }

  override def saveJson(filePath: String): Unit  = {
    _saveJsonMap(filePath, this.map.toMap)
  }

  override def load(filePath: String): Any = {
    val byteArray = _load(filePath)
    this.map = _toMMap(deserialize(byteArray))
    this.contents = Json.build(this.map).toString
    this.filePath = filePath
    this.map
  }

  override def save(filePath: String): Unit  = {
    _save(filePath, serialize)
  }
  // I/O
  override def name = JsonSummary.name

  // modify
  override def update(label: String, value:Any): Boolean = {
    if (schema.get.contains(label)) {
      map(label) = value
      true
    } else
      false
  }

  override def delete(label: String): Boolean =     {
    if (schema.get.contains(label)) {
      map -= label
      true
    } else
      false
  }
  override def add(label: String, value:Any): Boolean = {
    if (!schema.get.contains(label)) {
      map += (label -> value)
      true
    } else
      false
  }
}