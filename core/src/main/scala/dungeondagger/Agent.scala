package dungeondagger

trait Action

case class Move(obj: Agent, direction: Byte) extends Action
//case object Feed extends Action
//case class Spawn(baby: Agent) extends Action
//case object Die extends Action

object AgentKind extends Enumeration {
  type AgentKind = Value
  val Player, Frog, Fish = Value
}

abstract case class Agent(kind: AgentKind.Value) {
  def act: Option[Action]
}
