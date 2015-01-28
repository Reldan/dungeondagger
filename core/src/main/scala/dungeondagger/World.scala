package dungeondagger

import scala.util.Random


trait Event

case class AgentMoved(agent: Agent, from: Int, to: Int) extends Event

case class AgentDied(agent: Agent, at: Int) extends Event


case class World(height: Int = 150, width: Int = 150) {

  case class AgentState(agent: Agent, var position: Int)

  var agentStates = Vector.empty[AgentState]

  def addAgent(agent: Agent, position: Int = World.rand.nextInt(map.size)): Unit = {
    agentStates = new AgentState(agent, position) +: agentStates
  }
//  def removeAgent(agent: Agent): Unit = {
//    agentStates = agentStates filterNot agent.==
//  }

  val gen = Generator.newGen()
  var map: Array[World.Cell] = generateMap
  var buildings = Array.fill(100) {
    World.rand.nextInt(height * width)
  }.toSet

  def generateMap = Generator.terrain(width, height, Terrains.All.size - 1, gen).
    map(Terrains.All).
    map { World.Cell(_) }

  def regenerateMap() = {
    gen.setSeed(World.rand.nextInt(1000))
    map = generateMap
  }

  def canPass(newPosition: Int) = map(newPosition).terrain.passThrough

  def grabFieldOfVision(position: Int, adjacencies: IndexedSeq[Dxy]): FieldOfVision = {
    val y = position / width
    val x = position % width
    val data = adjacencies.map { a =>
      val destination = (y + a.dy) * width + x + a.dx
      if (destination < 0 || destination >= map.size || (x + a.dx < 0) || (x + a.dx > width - 1)) {
        None
      } else Some((a, map(destination)))
    }.flatten.toMap
    FieldOfVision(data)
  }

  def applyDirectionToPosition(pos: Int, dir: Int): Option[Int] = {
    val Dxy(dx, dy) = Adjacencies.dirToDxy(dir)
    val y = pos / width
    val x = pos % width
    val destination = width * (y + dy) + x + dx //TODO redundand check
    if (destination < 0 || destination >= map.size || ((x == width - 1) && dx > 0)) {
      None
    } else Some(destination)
  }

  def legalAction(aa: (AgentState, Action)): Boolean = aa match {
    case (AgentState(agent, pos), Move(obj, dir)) if obj == agent =>
      applyDirectionToPosition(pos, dir) match {
        case Some(destination) =>
          (canPass(destination) || !canPass(pos)) && map(destination).agents.isEmpty
        case None => false
      }
    case _ => true
  }

  def processAction(aa: (AgentState, Action)): Option[Event] = aa match {
    case (state@AgentState(agent, position), Move(obj, dir)) =>
      if (legalAction(aa)) {
        //TODO Checking again if moving there is still legal. Bollocks.
        applyDirectionToPosition(position, dir).map { nextPosition =>
          state.position = nextPosition
          map(position).agents = Vector.empty[Agent]
          map(nextPosition).agents = Vector(agent)
          AgentMoved(agent, position, nextPosition)
        }
      } else None
    case (state@AgentState(agent, position), Die) =>
      agentStates = agentStates filterNot state.==
      Some(AgentDied(agent, position))
  }

  def step(): Iterable[Event] = {
    val agentsAndActions = agentStates.map {
      case state@AgentState(agent, pos) =>
        agent.act(grabFieldOfVision(pos, Adjacencies.Radius2Eyeshot)).map { action => (state, action)}
    }.flatten

    agentsAndActions filter legalAction flatMap processAction
  }
}

object World {

  case class Cell(terrain: Terrain, var agents: Vector[Agent] = Vector.empty[Agent]) //TODO instances accessible to agents, make immutable

  val rand = new Random()
}

case class Dxy(dx: Int, dy: Int) {
  def +(that: Dxy): Dxy =
    Dxy(dx + that.dx, dy + that.dy)

  def *(that: Int): Dxy =
    Dxy(dx * that, dy * that)

  def /(that: Int): Dxy =
    Dxy(dx / that, dy / that)

  def stepsTo(that: Dxy): Int = {
    val i = dx - that.dx
    val j = dy - that.dy
    val ai = Math.abs(i)
    val aj = Math.abs(j)
    if (Math.signum(i) * Math.signum(j) > 0) {
      ai + aj
    } else Math.max(ai, aj)
  }
}

object Adjacencies {
  val Adjacent = Vector(Dxy(0, 1),
    Dxy(1, 0),
    Dxy(1, -1),
    Dxy(0, -1),
    Dxy(-1, 0),
    Dxy(-1, 1))

  def dirToDxy(i: Int) = Adjacent(i)

  val dxyToDir: Map[Dxy, Byte] = Adjacent.zipWithIndex.map { t => (t._1, t._2.toByte)}.toMap

  val SelfAndAdjacent = Dxy(0, 0) +: Adjacent
  val Radius2Eyeshot = SelfAndAdjacent ++ Adjacent.map {
    _ * 2
  } ++
    (Adjacent.map {
      _ * 2
    } :+ Adjacent(0)).sliding(2).map { pair => (pair(0) + pair(1)) / 2}
}
