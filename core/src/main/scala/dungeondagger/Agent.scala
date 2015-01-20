package dungeondagger

trait Action

case class Move(obj: Agent, direction: Byte) extends Action
//case object Feed extends Action
//case class Spawn(baby: Agent) extends Action
//case object Die extends Action

trait Agent {
  def act: Option[Action]
}
