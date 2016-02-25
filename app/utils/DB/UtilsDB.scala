package utils.DB
import controllers.DatabaseController
/**
  * Created by Luke on 2/11/2016.
  */
object UtilsDB {

  var activeDatabase = databaseTypes.liveDatabase

  object databaseTypes extends Enumeration {
    val liveDatabase, testDatabase = Value
  }

  def setActiveDatabaseAsTest(isTestDB : Boolean) =
  {
    if(isTestDB)
      activeDatabase = databaseTypes.testDatabase
    else
      activeDatabase = databaseTypes.liveDatabase
  }
  def getActiveDatabaseName() :String ={
    if(activeDatabase == databaseTypes.testDatabase)
      return "testDB"
    else if(activeDatabase == databaseTypes.liveDatabase)
      return "default"
    else
      return "default"
  }
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
