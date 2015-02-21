package dungeondagger.life

import scala.util.Random

trait Ship {
  def width: Int
  def height: Int
}

class Kestrel extends Ship {
  //0 - no
  val random = new Random()

  val rooms = Array(
    "WWWWWWWWWWWWWXXXXXWWWWWWWW",
    "WWWWWWWWXXXXX00000XXXWWWWW",
    "WWXXXXXX0000000000000XXWWW",
    "WX0000000000000XX000000XWW",
    "XX0000000X00000XX000000XXX",
    "WX0000000000000XX000000XWW",
    "WWXXXXXX0000000000000XXWWW",
    "WWWWWWWWXXXXX00000XXXWWWWW",
    "WWWWWWWWWWWWWXXXXXWWWWWWWW"
  ).map(_.toArray)

  val width = rooms(0).size
  val height = rooms.size
  var pirates = List(new Pirate(5, 5))

  def movePirates = {
    pirates = pirates.map { p =>
      random.nextInt(100) match {
        case (0) if (p.y + 1 < height) && rooms(p.y + 1)(p.x) == '0' => new Pirate(p.x, p.y + 1)
        case (1) if (p.y - 1 > 0) && rooms(p.y - 1)(p.x) == '0' => new Pirate(p.x, p.y - 1)
        case (2) if (p.x + 1 < width) && rooms(p.y)(p.x + 1) == '0' => new Pirate(p.x + 1, p.y)
        case (3) if (p.x - 1 > 0) && rooms(p.y)(p.x - 1) == '0' => new Pirate(p.x - 1, p.y)
        case _ => p
      }
    }
  }
}

trait Person {
  def x: Int
  def y: Int
}

class Pirate(val x: Int, val y: Int) extends Person {}