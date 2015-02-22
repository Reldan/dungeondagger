package dungeondagger.life

import scala.util.Random

trait Ship {
  val random = new Random()

  def roomsStr: Array[Array[String]]
  def rooms = roomsStr.map(deck => deck.map(_.toArray))

  def width: Int = rooms(0)(0).size
  def height: Int = rooms(0).size
  var pirates: List[Pirate]
  var captain: Option[Pirate]

  def deckCount = roomsStr.size
  var activeDeck: Int

  val dir = Map('U' -> (0, -1), 'D' -> (0, 1), 'L' -> (-1, 0), 'R' -> (1, 0))

  def action(): Unit = captain.foreach { p =>
    dir.get(p.front).map(el => (p.x + el._1, p.y + el._2)).filter(el => cannonCell(el._1, el._2)).foreach{
      case(x, y) => fireCannon(x, y)
    }

    dir.get(p.front).map(el => (p.x + el._1, p.y + el._2)).filter(el => ladderCell(el._1, el._2)).foreach{
      case(x, y) => if (activeDeck == 0) activeDeck += 1 else activeDeck -= 1
    }
  }

  def moveCaptain(direction: Char) = captain = captain.map { movePerson(_, direction) }

  def movePerson(p: Pirate, direction: Char) = {
    dir.get(direction).collect{ case(x, y) if validCell(p.x + x, p.y + y) => new Pirate(p.x + x, p.y + y, direction) }.getOrElse{
      new Pirate(p.x, p.y, direction)
    }
  }

  private def maybeCell(x: Int, y: Int) =
    if (x > 0 && y > 0 && x < width && y < height) Some(rooms(activeDeck)(y)(x))
    else None

  private def validCell(x: Int, y: Int) = maybeCell(x, y).exists(_ == '0')
  private def cannonCell(x: Int, y: Int) = maybeCell(x, y).exists(el => el == '^' || el == 'v' )
  private def ladderCell(x: Int, y: Int) = maybeCell(x, y).exists(_ == 'L')

  var firingCannons = List.empty[(Int, Int, Int)]

  def fireCannon(x: Int, y: Int) = firingCannons ::= (x, y, 100)

  def movePirates() = {
    firingCannons = firingCannons.filter(_._3 > 1).map(el => (el._1, el._2, el._3 - 1))
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
    Array(
      "WWWWWWWWWWWWWXXXXXWWWWWWWW",
      "WWWWWWWWX^X^X00000XXXWWWWW",
      "WWXXXXXX0000000000000XXWWW",
      "WX0000000000000XX000000XWW",
      "XX0000000X000L0XX000000XXX",
      "WX0000000000000XX000000XWW",
      "WWXXXXXX0000000000000XXWWW",
      "WWWWWWWWXvXvX00000XXXWWWWW",
      "WWWWWWWWWWWWWXXXXXWWWWWWWW"
    ),
    Array(
      "WWWWWWWWWWWWWWWWWWWWWWWWWW",
      "WWWWWWWWWWWWWXXXXXWWWWWWWW",
      "WWWWWWWWXXXXX00000XXXWWWWW",
      "WWXXXXXX0000000000000XXWWW",
      "WXX0000000000L000000000XWW",
      "WWXXXXXX0000000000000XXWWW",
      "WWWWWWWWXXXXX00000XXXWWWWW",
      "WWWWWWWWWWWWWXXXXXWWWWWWWW",
      "WWWWWWWWWWWWWWWWWWWWWWWWWW"
    )
  )

  var activeDeck = deckCount - 1

  var captain: Option[Pirate] = Some(new Pirate(5, 4, 'U'))

  var pirates: List[Pirate] = List.empty
}

class Boat extends Ship {
  val roomsStr = Array(Array(
    "XXXXWW",
    "X000XX",
    "XXXXWW"
  ))

  var activeDeck = deckCount - 1

  var captain: Option[Pirate] = None

  var pirates: List[Pirate] = List(new Pirate(1, 1, 'U'))
}


case class Pirate(x: Int, y: Int, front: Char) {
  require(List('U', 'D', 'L', 'R').exists(_ == front))
}