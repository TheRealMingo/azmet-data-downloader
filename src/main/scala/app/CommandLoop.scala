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


  /** Determines whether a commend is valid
    *
    * @param command The command to check for validity
    * @return True if the command is valid, false otherwise
    * */
  private def commmandIsValid(command: String) : Boolean = {
    val allYearsForStations : Boolean = command.matches(AllYearsForStations.regex)
    val oneYearForStations : Boolean = command.matches(OneYearForStations.regex)
    val multiYearForStations : Boolean = command.matches(MultiYearForStations.regex)
    val yearIntervalForStations : Boolean = command.matches(YearIntervalForStations.regex)
    val exit : Boolean = command.matches(Exit.regex)
    val help : Boolean = command.matches(Help.regex)
    val list : Boolean = command.matches(ListStations.regex)

    allYearsForStations || oneYearForStations || multiYearForStations ||
      yearIntervalForStations || exit || help || list
  }

  /** Given a type and a timeframe return the corresponding FileType
    *
    * @param theType The type either raw or standard
    * @param timeFrame The timeframe either hourly, daily, or monthly
    * @return An Option representing the FileType, returns None if the file type doesn't exist
    * */
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


  /** Takes the arguments and convert them to types that can be better used to download data from the archive
    *
    * @param stations A string representing the data to download
    * @param theType A string representing the type (ex. raw hourly)
    * @param timeFrame A string representing the time frame
    * @return A tuple with the first argument being a list of the stations to download and the second being a type (ex. standard weekly)
    * */
  private def parseArgs(stations: String, theType: String, timeFrame: String) : (List[String], Option[FileType.Value]) =  {
    val theStations : List[String] = stations.split("\\s").toList
    val fileType : Option[FileType.Value] = getFileType(theType, timeFrame)
    (theStations, fileType)
  }

  /** Given a list of stations, file type, and years download the corresponding data from the AZMET Archive
    *
    * @param stations The stations to download
    * @param fileType The type to download (ie raw hourly)
    * @param theYears The list of years to download
    * */
  private def runDownloaderForCommand(stations: List[String], fileType: Option[FileType.Value], theYears : List[Int]) : Unit = {
    if(fileType.isDefined){
      val stationNumbers : List[Int] = stations.map(station => Stations.stationNumber(station.capitalize))
      stationNumbers.foreach(station => theYears.foreach(year => Downloader.downloadData(station, year, fileType.get)))
    }
    else{
      println(CommandNotRecognized)
    }
  }


  /** Lists all of the stations
    *
    * @param stations The stations of the program
    * */
  private def listStations(stations: List[String]): Unit ={
    @tailrec
    def listStationsHelper(stations: List[String], counter: Int) : Unit = {
      if(stations.nonEmpty){
        println("(" + counter + ") " + stations.head)
        listStationsHelper(stations.tail, counter + 1)
      }
    }
    listStationsHelper(stations.sorted, 1)
  }

  /** Runs a given command
    *
    * @param command The command to be ran
    * */
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


  /** The main loop of the program
    *
    * @param loop Determines whether to keep the program running
    * */
  @tailrec
  def runLoop(loop: Boolean) : Unit = {
    if(loop){
      print(">")
      val command : String = readLine
      if(commmandIsValid(command.trim)){
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
