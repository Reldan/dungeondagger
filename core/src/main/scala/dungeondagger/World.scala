package dungeondagger

//import scala.collection.mutable
import scala.util.Random


trait Event

case class AgentMoved(agent: Agent, from: Int, to: Int) extends Event


case class World(height: Int = 150, width: Int = 150) {

  case class AgentState(agent: Agent, var position: Int)

  var agentStates = Vector.empty[AgentState]

  def addAgent(agent:Agent, position:Int = rand.nextInt(map.size)): Unit = {
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

  def applyDirectionToPosition(pos: Int, dir: Int): Option[Int] = {
    //    val d = pos % 2
    dir match {
      case 0 if pos + width < height * width && (canPass(pos + width) || !canPass(pos)) => Some(pos + width)
      case 1 if pos % width != width - 1 && (canPass(pos + 1) || !canPass(pos)) => Some(pos + 1)
      case 2 if pos - width >= 0 && (canPass(pos - width) || !canPass(pos)) => Some(pos - width)
      case 3 if pos % width != 0 && (canPass(pos - 1) || !canPass(pos)) => Some(pos - 1)
      case _ => None
    }
  }

  def step(): Set[Event] = {
    agentStates.map {
      case s@AgentState(agent, pos) =>
        agent.act.map { action => (s.agent, action)}.flatMap {
          case (actor, Move(a, dir)) =>
            applyDirectionToPosition(pos, dir).map {
              case nextPosition =>
                s.position = nextPosition
                AgentMoved(a, pos, nextPosition)
            }
        }
    }.flatten.toSet
  }
}
