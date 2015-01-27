package dungeondagger

import com.badlogic.gdx.Game

class DungeonDagger extends Game {
  val AssetsPath = "data"

  override def create(): Unit = {
    setScreen(new GameScreen(this))
  }
}
