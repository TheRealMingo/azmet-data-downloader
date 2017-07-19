package app

object Stations {
  val Aguile : Int = 7
  val Bonita : Int = 9
  val Bowie : Int = 33
  val Buckeye : Int = 26
  val Coolidge : Int = 5
  val DesertRidge : Int = 27
  val Harquahala : Int = 23
  val Maricopa : Int = 6
  val Mesa : Int = 29
  val Mohave : Int = 20
  val Mohave2 : Int = 28
  val Paloma : Int = 19
  val Parker1 : Int = 8
  val Parker2 : Int = 35
  val Payson : Int = 32
  val Prescott : Int = 31
  val PhoenixEncanto : Int = 15
  val PhoenixGreenway : Int = 12
  val QueenCreek : Int = 22
  val Roll : Int = 24
  val Safford : Int = 4
  val Sahuarita : Int = 38
  val SanSimon : Int = 37
  val YumaNorthGila : Int = 14
  val YumaSouth : Int = 36
  val YumaValley : Int = 2
  val Tucson : Int = 1

  val stationName : Map[Int, String] = Map(
    Stations.Aguile -> "Aguile",
    Stations.Bonita -> "Bonita",
    Stations.Bowie -> "Bowie",
    Stations.Buckeye -> "Buckeye",
    Stations.Coolidge -> "Coolidge",
    Stations.DesertRidge -> "DesertRidge",
    Stations.Harquahala -> "Harquahala",
    Stations.Maricopa -> "Maricopa",
    Stations.Mesa -> "Mesa",
    Stations.Mohave -> "Mohave",
    Stations.Mohave2 -> "Mohave2",
    Stations.Paloma -> "Paloma",
    Stations.Parker1 -> "Parker1",
    Stations.Parker2 -> "Packer2",
    Stations.Payson -> "Payson",
    Stations.Prescott -> "Prescott",
    Stations.PhoenixEncanto -> "PhoenixEncanto",
    Stations.PhoenixGreenway -> "PhoenixGreenway",
    Stations.QueenCreek -> "QueenCreek",
    Stations.Roll -> "Roll",
    Stations.Safford -> "Safford",
    Stations.Sahuarita -> "Sahuarita",
    Stations.SanSimon -> "SanSimon",
    Stations.Tucson -> "Tucson",
    Stations.YumaNorthGila -> "YumaNorthGila",
    Stations.YumaSouth -> "YumaSouth",
    Stations.YumaValley -> "YumaValley"
  )

  val stationNumber : Map[String, Int] = stationName.map(_.swap)
  val stationNames : List[String] = stationName.values.toList
}
