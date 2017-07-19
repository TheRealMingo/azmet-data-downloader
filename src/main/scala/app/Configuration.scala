package app

import java.io.{File, FileInputStream, FileNotFoundException}
import java.util.Properties

object Configuration {
  private val SaveDailyLocationRaw : String = "save.daily.location.raw"
  private val SaveHourlyLocationRaw : String = "save.hourly.location.raw"
  private val SaveDailyLocationStandard : String = "save.daily.location.standard"
  private val SaveWeeklyLocationStandard : String = "save.weekly.location.standard"
  private val SaveMonthlyLocationStandard : String  = "save.monthly.location.standard"

  private val conf : Properties = {
    val conf : Properties = new Properties()
    conf.load(getClass.getClassLoader.getResourceAsStream("conf.properties"))
    conf
  }

  val saveDailyLocationRaw : String = {
    val location : String = conf.getProperty(SaveDailyLocationRaw)
    val file : File = new File(location)
    if(!file.exists){
      file.mkdirs()
    }
    location
  }

  val saveHourlyLocationRaw : String = {
    val location : String = conf.getProperty(SaveHourlyLocationRaw)
    val file : File = new File(location)
    if(!file.exists){
      file.mkdirs()
    }
  location
}

  val saveDailyLocationStandard : String = {
    val location : String = conf.getProperty(SaveDailyLocationStandard)
    val file : File = new File(location)
    if(!file.exists){
      file.mkdirs()
    }
    location
  }

  val saveWeeklyLocationStandard : String = {
    val location : String = conf.getProperty(SaveWeeklyLocationStandard)
    val file : File = new File(location)
    if(!file.exists){
      file.mkdirs()
    }
    location
  }

  val saveMonthlyLocationStandard : String = {
    val location : String = conf.getProperty(SaveMonthlyLocationStandard)
    val file : File = new File(location)
    if(!file.exists){
      file.mkdirs()
    }
    location
  }
}
