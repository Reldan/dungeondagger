package dungeondagger.life

import scala.util.Random

trait Ship {
  val random = new Random()

  def roomsStr: Array[String]
  def rooms = roomsStr.map(_.toArray)

  def width: Int = rooms(0).size
  def height: Int = rooms.size
  var pirates: List[Pirate]
  var captain: Option[Pirate]

  def moveCaptain(direction: Char) = captain = captain.map { movePerson(_, direction) }

  def movePerson(p: Pirate, direction: Char) = {
   direction match {
      case 'U' if validCell(p.x, p.y - 1) => new Pirate(p.x, p.y - 1, direction)
      case 'D' if validCell(p.x, p.y + 1) => new Pirate(p.x, p.y + 1, direction)
      case 'L' if validCell(p.x - 1, p.y) => new Pirate(p.x - 1, p.y, direction)
      case 'R' if validCell(p.x + 1, p.y) => new Pirate(p.x + 1, p.y, direction)
      case _ => new Pirate(p.x, p.y, direction)
    }
  }

  private def validCell(x: Int, y: Int) =
    x > 0 && y > 0 && x < width && y < height && rooms(y)(x) == '0'

  def movePirates() = {
    pirates = pirates.map { p =>
      random.nextInt(100) match {
        case (0) => movePerson(p, 'U')
        case (1) => movePerson(p, 'D')
        case (2) => movePerson(p, 'L')
        case (3) => movePerson(p, 'R')
        case _ => p
      }
    }
  }
}

class Caravel extends Ship {
  val roomsStr = Array(
    "WWWWWWWWWWWWWXXXXXWWWWWWWW",
    "WWWWWWWWX^X^X00000XXXWWWWW",
    "WWXXXXXX0000000000000XXWWW",
    "WX0000000000000XX000000XWW",
    "XX0000000X000L0XX000000XXX",
    "WX0000000000000XX000000XWW",
    "WWXXXXXX0000000000000XXWWW",
    "WWWWWWWWXvXvX00000XXXWWWWW",
    "WWWWWWWWWWWWWXXXXXWWWWWWWW"
  )
  var captain: Option[Pirate] = Some(new Pirate(5, 5, 'U'))

  var pirates: List[Pirate] = List.empty
}

class Boat extends Ship {
  val roomsStr = Array(
    "XXXXWW",
    "X000XX",
    "XXXXWW"
  )

  var captain: Option[Pirate] = None

  var pirates: List[Pirate] = List(new Pirate(1, 1, 'U'))
}


case class Pirate(x: Int, y: Int, front: Char) {
  require(List('U', 'D', 'L', 'R').exists(_ == front))
}