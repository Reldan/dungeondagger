package dungeondagger

import com.badlogic.gdx.{Game, Screen}

case class DefaultScreen(game: Game) extends Screen {
  override def render(delta: Float): Unit = {}

  override def hide(): Unit = {}

  override def resize(width: Int, height: Int): Unit = {}

  override def dispose(): Unit = {}

  override def pause(): Unit = {}

  override def show(): Unit = {}

  override def resume(): Unit = {}
}
