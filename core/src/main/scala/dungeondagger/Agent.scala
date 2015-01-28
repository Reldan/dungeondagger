package dungeondagger

trait Action

case class Move(obj: Agent, direction: Byte) extends Action
//case object Feed extends Action
case class Spawn(baby: Agent) extends Action
case object Die extends Action

object AgentNiche extends Enumeration {
  type AgentKind = Value
  val Front, Grass = Value
}

abstract case class Agent(niche: AgentNiche.Value) {
  def act: Option[Action]
}
