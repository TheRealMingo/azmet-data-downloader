package app

import scala.io._
import java.io._

import scala.util.Try
import scala.util.matching.Regex.Match

object Downloader {
  /** Variables for url and filename construction. */
  private val baseUrl : String = "https://cals.arizona.edu/azmet/data/"
  private val rawDailyExtension : String = "rd.txt"
  private val rawHourlyExtension : String = "rh.txt"
  private val standardMonthlyExtension : String = "em.txt"
  private val standardWeeklyExtension : String = "ew.txt"
  private val standardDailyExtension : String = "eh.txt"
  private val rawDailyFileExtension : String = "_Daily.txt"
  private val rawHourlyFileExtension : String = "_Hourly.txt"
  private val zero = "0"
  private val underscore = "_"

  private def isOneDigit(number: Int) : Boolean = number/10 == 0

  private def shortenYear(year: Int) : Int = year % 100

  private def acquireFileName(fileType: FileType.Value, station: Int, year: Int) : String = {
    val stationName: String = Stations.stationName(station)

    fileType match {
      case FileType.RawHourly => Configuration.saveHourlyLocationRaw + stationName + underscore + year + rawHourlyFileExtension
      case FileType.RawDaily => Configuration.saveDailyLocationRaw + stationName + underscore + year + rawDailyFileExtension
      case FileType.StandardDaily => Configuration.saveDailyLocationStandard + stationName + underscore + standardDailyExtension
      case FileType.StandardMonthly => Configuration.saveMonthlyLocationStandard + stationName + underscore + standardMonthlyExtension
      case FileType.StandardWeekly => Configuration.saveWeeklyLocationStandard + stationName + underscore + standardWeeklyExtension
    }
  }

  private def acquireFullUrl(sYearIsOneDigit: Boolean, stationIsOneDigit: Boolean, station: Int, sYear: Int, extension: String) : String = sYearIsOneDigit match{
    case true if stationIsOneDigit => baseUrl + zero + station + zero + sYear + extension
    case true if !stationIsOneDigit => baseUrl + station + zero + sYear + extension
    case false if stationIsOneDigit => baseUrl + zero + station + sYear + extension
    case false if !stationIsOneDigit => baseUrl + station + sYear + extension
  }

  def downloadData(station: Int, year: Int, fileType: FileType.Value) : Unit = {
    val sYear : Int = shortenYear(year)
    val sYearIsOneDigit : Boolean = isOneDigit(sYear)
    val stationIsOneDigit : Boolean = isOneDigit(station)
    val stationName: String = Stations.stationName(station)

    val extension : String = fileType match{
      case FileType.RawDaily => rawDailyExtension
      case FileType.RawHourly => rawHourlyExtension
      case FileType.StandardDaily => standardDailyExtension
      case FileType.StandardWeekly => standardWeeklyExtension
      case FileType.StandardMonthly => standardMonthlyExtension
    }

    val fileName : String = acquireFileName(fileType, station, year)
    val url : String = acquireFullUrl(sYearIsOneDigit, stationIsOneDigit, station, sYear, extension)

    val pattern = "([A-Z])".r
    val fileTypePrettyPrint = pattern.replaceAllIn(fileType.toString, (m :Match) => " " + m.group(1).toLowerCase)
    val stationNamePrettyPrint = pattern.replaceAllIn(stationName, (m:Match) => " " + m.group(1))
    println("Retrieving" + fileTypePrettyPrint + " data for station" + stationNamePrettyPrint +  "for year " + year + " ...")

    val content: String = {
      if (Try(Source.fromURL(url)).isSuccess) Source.fromURL(url).mkString
      else "Error Retrieving Data from" + stationName + "for year" + year + ".\r\nEither data does not exist or server refused permission to view it."
    }

    val printerWriter = new PrintWriter(new File(fileName))
    printerWriter.write(content)
    printerWriter.close()
    println("Data Retrieved for station" + stationNamePrettyPrint + ".")
  }

  def downloadDataFromAllStations(year: Int, fileType : FileType.Value) : Unit = {
    val stationIDs : Iterable[Int] = Stations.stationName.keys
    stationIDs.foreach((id: Int) => downloadData(id, year, fileType))
    println("Retreived data for all stations.")
  }


}
