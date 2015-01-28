package dungeondagger

class PlayerAgent extends Agent(AgentNiche.Front){
  var nextDirection: Option[Byte] = None

  override def act: Option[Action] = {
    val action = nextDirection.map{Move(this, _)}
    nextDirection = None
    action
  }

  def go(direction:Byte):Unit = {nextDirection = Some(direction)}
}
