package utils.DB
import controllers.DatabaseController
/**
  * Created by Luke on 2/11/2016.
  */
object UtilsDB {

  def buildWhereClause(whereClause : String, colName : String, value: Int): String = {
    var valueString = ""
    if(value != -1)
      valueString = value.toString()
    return buildWhereClause(whereClause,colName,valueString)
  }
  def buildWhereClause(whereClause : String, colName : String, value: String): String = {
    var newWhereClause = whereClause
    if(value != ""){
      if(newWhereClause == ""){
        newWhereClause += s" WHERE $colName = $value"
      }
      else
      {
        newWhereClause += s" AND $colName = $value"
      }
    }
    return newWhereClause
  }
}
