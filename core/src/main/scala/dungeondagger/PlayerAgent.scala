package dungeondagger

/**
 * Created by etsvigun on 20/01/15.
 */
class PlayerAgent extends Agent{
  var nextDirection: Option[Byte] = None

  override def act: Option[Action] = {
    val action = nextDirection.map{Move(this, _)}
    nextDirection = None
    action
  }

  def go(direction:Byte):Unit = {nextDirection = Some(direction)}
}
