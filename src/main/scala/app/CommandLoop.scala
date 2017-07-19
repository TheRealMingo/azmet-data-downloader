package app

import java.time.LocalDate

import scala.annotation.tailrec
import scala.io.StdIn.readLine
import scala.util.matching.Regex

object CommandLoop {
  private val CommandNotRecognized : String = "Command not recognized.\nType ? and enter to see all valid commands."
  private val AllYearsForStations : Regex = "download\\s([A-Za-z\\s]*)(raw|standard)\\s(hourly|daily|weekly)".r
  private val OneYearForStations : Regex = "download\\s([A-Za-z\\s]*)(\\d{4})\\s(raw|standard)\\s(hourly|daily|weekly)".r
  private val MultiYearForStations : Regex = "download\\s([A-Za-z]\\s*)(\\d{4}\\s)*(raw|standard)\\s(hourly|daily|weekly)".r
  private val YearIntervalForStations : Regex = "download\\s([A-Za-z]\\s*)(\\d{4})-(\\d{4})\\s(raw|standard)\\s(hourly|daily|weekly)".r
  private val Exit : Regex = "(EXIT|Exit|exit)".r
  private val Help : Regex = "(HELP|Help|help|\\?)".r
  private val ListStations : Regex = "(list-all-station-names)".r
  private val years : List[Int] = (1987 to LocalDate.now.getYear).toList
  private val helpMessage : String = "Commands:\n" +
    "(1)exit\n\t--exits the program\n\n" +
    "(2)help\n\t--displays help message\n\n" +
    "(3)list-all-station-names>\n\t--displays all the support stations in the program\n\n" +
    "(4)download <station> <station> ... <station> <raw|standard> <hourly|daily|weekly> " +
    "\n\t--downloads all the data for given stations for the given type (ex: download Aguile raw hourly)\n\n" +
    "(5)download <station> <station> ... <station> <year> <year> <raw|standard> <hourly|daily|weekly>" +
    "\n\t--downloads the data of the given years for the given stations for they given type (ex: download Aguile Bonita 2017 standard weekly)\n\n" +
    "(6)download <station> <station>> ... <station> <year> - <year> <raw|standard> <hourly|daily|weekly>" +
    "\n\t--downloads the data of between the years given for the given stations for the given type (ex: download Aguile 2005-2008 raw daily)\n\n" +
    "Note: type refers to a combination of <raw|standard> <hourly|daily|weekly> example raw hourly"


  private[app] def argsAreValid(args: String) : Boolean = {
    val allYearsForStations : Boolean = args.matches(AllYearsForStations.regex)
    val oneYearForStations : Boolean = args.matches(OneYearForStations.regex)
    val multiYearForStations : Boolean = args.matches(MultiYearForStations.regex)
    val yearIntervalForStations : Boolean = args.matches(YearIntervalForStations.regex)
    val exit : Boolean = args.matches(Exit.regex)
    val help : Boolean = args.matches(Help.regex)
    val list : Boolean = args.matches(ListStations.regex)

    allYearsForStations || oneYearForStations || multiYearForStations ||
      yearIntervalForStations || exit || help || list
  }

  private def getFileType(theType: String, timeFrame: String) : Option[FileType.Value] = {
    theType match{
      case "raw" if timeFrame == "hourly" => Some(FileType.RawHourly)
      case "raw" if timeFrame == "daily"  => Some(FileType.RawDaily)
      case "standard" if timeFrame == "daily" => Some(FileType.StandardDaily)
      case "standard" if timeFrame == "weekly" => Some(FileType.StandardWeekly)
      case "standard" if timeFrame == "monthly" => Some(FileType.StandardMonthly)
      case _ => None
    }
  }


  private def parseArgs(stations: String, theType: String, timeFrame: String) : (List[String], Option[FileType.Value]) =  {
    val theStations : List[String] = stations.split("\\s").toList
    val fileType : Option[FileType.Value] = getFileType(theType, timeFrame)
    (theStations, fileType)
  }

  private def runDownloaderForCommand(stations: List[String], fileType: Option[FileType.Value], theYears : List[Int]) : Unit = {
    if(fileType.isDefined){
      val stationNumbers : List[Int] = stations.map(station => Stations.stationNumber(station.capitalize))
      stationNumbers.foreach(station => theYears.foreach(year => Downloader.downloadData(station, year, fileType.get)))
    }
    else{
      println(CommandNotRecognized)
    }
  }

  private def listStations(stations: List[String]): Unit ={
    def listStationsHelper(stations: List[String], counter: Int) : Unit = {
      if(stations.nonEmpty){
        println("(" + counter + ") " + stations.head)
        listStationsHelper(stations.tail, counter + 1)
      }
    }
    listStationsHelper(stations.sorted, 1)
  }

  private def runCommand(command : String) : Boolean = {
    command match {
      case Exit(e) => false
      case Help(h) => println(helpMessage); true
      case ListStations(ls) => listStations(Stations.stationNames); true
      case AllYearsForStations(stations, theType, timeFrame) =>
        val (theStations, fileType) : (List[String], Option[FileType.Value]) = parseArgs(stations, theType, timeFrame)
        runDownloaderForCommand(theStations, fileType, years)
        true
      case OneYearForStations(stations, year, theType, timeFrame) =>
        val (theStations, fileType) : (List[String], Option[FileType.Value]) = parseArgs(stations, theType, timeFrame)
        val theYear : Int = year.toInt
        runDownloaderForCommand(theStations, fileType, List(theYear))
        true
      case MultiYearForStations(stations, theYears, theType, timeFrame) =>
        val (theStations, fileType) : (List[String], Option[FileType.Value]) = parseArgs(stations, theType, timeFrame)
        val parsedYears: List[Int] = theYears.split("\\s").toList.map(_.toInt)
        runDownloaderForCommand(theStations, fileType, parsedYears)
        true
      case YearIntervalForStations(stations, fromYear, toYear, theType, timeFrame) =>
        val (theStations, fileType) : (List[String], Option[FileType.Value]) = parseArgs(stations, theType, timeFrame)
        val theYears : List[Int] = (fromYear.toInt to toYear.toInt).toList
        runDownloaderForCommand(theStations, fileType, theYears)
        true
      case _ => println(CommandNotRecognized); true
    }
  }

  @tailrec
  def runLoop(loop: Boolean) : Unit = {
    if(loop){
      print(">")
      val command : String = readLine
      if(argsAreValid(command.trim)){
        val runAgain : Boolean = runCommand(command)
        runLoop(runAgain)
      }
      else {
        println(CommandNotRecognized)
        runLoop(true)
      }
    }
  }

}
