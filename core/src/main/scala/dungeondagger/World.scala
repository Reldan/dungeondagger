package dungeondagger

import scala.util.Random


trait Event

case class AgentMoved(agent: Agent, from: Int, to: Int) extends Event


case class World(height: Int = 150, width: Int = 150) {

  case class AgentState(agent: Agent, var position: Int)

  var agentStates = Vector.empty[AgentState]

  def addAgent(agent: Agent, position: Int = rand.nextInt(map.size)): Unit = {
    agentStates = new AgentState(agent, position) +: agentStates
  }

  //  class Cell(val terrain: Terrain = Terrains.Grass,
  //             val agents: Vector[Agent] = Vector.empty[Agent])


  val rand = new Random()
  val gen = Generator.newGen()
  var map: Array[Terrain] = generateMap

  def generateMap = Generator.terrain(width, height, Terrains.All.size - 1, gen).map {
    Terrains.All
  }

  def regenerateMap() = {
    gen.setSeed(rand.nextInt(1000))
    map = generateMap
  }

  def canPass(newPosition: Int) = map(newPosition).passThrough

  val dirToDXY = Map(
    0 ->(0, 1),
    1 ->(1, 0),
    2 ->(+1, -1),
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

  def legalAction(aa:(AgentState,Action)):Boolean = { aa match { //TODO why doesn't it compile without aa match
    case (AgentState(agent, pos), Move(obj, dir)) if obj == agent =>
      applyDirectionToPosition(pos, dir) match {
        case Some(destination) =>
          (canPass(destination) || !canPass(pos)) && !agentStates.map{_.position}.contains(destination) //TODO refactor to cells usage
        case None => false
      }
    case _ => true
  }}

  def step(): Seq[Event] = {
    agentStates.map {
      case s@AgentState(agent, pos) =>
        agent.act.map { action => (s, action)} filter legalAction flatMap {
          case (s@AgentState(a, p), Move(obj, dir)) =>
            applyDirectionToPosition(pos, dir).map {
              case nextPosition =>
                s.position = nextPosition
                AgentMoved(a, pos, nextPosition)
            }
        }
    }.flatten
  }
}
