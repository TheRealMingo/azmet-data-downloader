package app

object Main{
  private[app] val Welcome : String = "Welcome to the AZMET data archive downloader.\nType ? and enter to see what all you can do."
  private[app] val Valediction : String = "Exitting program, have a good day!"

  def main(args: Array[String]) : Unit = {
    println(Welcome)
    CommandLoop.runLoop(true)
    println(Valediction)
  }
}
