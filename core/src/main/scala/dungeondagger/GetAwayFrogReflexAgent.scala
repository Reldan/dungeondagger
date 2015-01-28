package dungeondagger

import scala.util.Random

/** A reflex agent, gets away from other species
  * by picking the furthest cell from any the agents of other types
  * present in the current field of view
  */
class GetAwayFrogReflexAgent extends Agent(AgentNiche.Front) {
  override def act(fieldOfVision: FieldOfVision): Option[Action] = {
    val otherSpeciesLocationInView = fieldOfVision.data.filter {
      case (_, World.Cell(_, agents: Vector[Agent])) if agents.exists {
        case _: GetAwayFrogReflexAgent => false
        case _ => true
      } => true
      case _ => false
    }.map { _._1 }

    if (otherSpeciesLocationInView.isEmpty) {
      randomMove
    } else {
      val destination = Random.shuffle(Adjacencies.Adjacent).maxBy { location =>
        otherSpeciesLocationInView.map {
          _.stepsTo(location)
        }.sum
      }
      Some(Move(this, Adjacencies.dxyToDir(destination)))
    }
  }

  private def randomMove = if (World.rand.nextBoolean()) {
    Some(Move(this, World.rand.nextInt(6).toByte))
  } else if (World.rand.nextInt(100) == 0) {
    Some(Die)
  } else None
}
