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
    map{World.Cell(_)}

  def regenerateMap() = {
    gen.setSeed(World.rand.nextInt(1000))
    map = generateMap
  }

  def canPass(newPosition: Int) = map(newPosition).terrain.passThrough

  val dirToDXY = Map(
    0 ->(0, 1),
    1 ->(1, 0),
    2 ->(1, -1),
    3 ->(0, -1),
    4 ->(-1, 0),
    5 ->(-1, 1)
  )

  def applyDirectionToPosition(pos: Int, dir: Int): Option[Int] = {
    val (dx, dy) = dirToDXY(dir)
    val y = pos / width
    val x = pos % width
    val destination = width * (y + dy) + x + dx //TODO redundand check
    if (destination < 0 || destination >= map.size || ((x == width - 1) && dx > 0)) {
      None
    } else Some(destination)
  }

  def legalAction(aa:(AgentState,Action)):Boolean = aa match {
    case (AgentState(agent, pos), Move(obj, dir)) if obj == agent =>
      applyDirectionToPosition(pos, dir) match {
        case Some(destination) =>
          (canPass(destination) || !canPass(pos)) && map(destination).agents.isEmpty
        case None => false
      }
    case _ => true
  }

  def processAction(aa:(AgentState,Action)):Option[Event] = aa match {
    case (state@AgentState(agent, position), Move(obj, dir)) =>
      if(legalAction(aa)){ //TODO Checking again if moving there is still legal. Bollocks.
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
    val agentsAndActions = agentStates.map{
      case state@AgentState(agent, _) =>
        agent.act.map{action => (state, action)}
    }.flatten

    agentsAndActions filter legalAction flatMap processAction
  }
}

object World{
  case class Cell(terrain: Terrain, var agents: Vector[Agent] = Vector.empty[Agent])
  val rand = new Random()
}
