package app

object Test extends App {
  Downloader.downloadData(Stations.Aguile, 2013, FileType.RawDaily)
  Downloader.downloadData(Stations.Aguile, 2013, FileType.RawHourly)
  Downloader.downloadData(Stations.Aguile, 2013, FileType.StandardDaily)
  Downloader.downloadData(Stations.Aguile, 2013, FileType.StandardWeekly)
  Downloader.downloadData(Stations.Aguile, 2013, FileType.StandardMonthly)
  //Downloader.downloadDataFromAllStations(2013, FileType.RawDaily)
  //Downloader.downloadDataFromAllStations(2013, FileType.RawHourly)
}
