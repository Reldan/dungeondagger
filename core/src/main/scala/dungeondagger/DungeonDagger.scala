package dungeondagger

import com.badlogic.gdx.Game

class DungeonDagger extends Game {
  override def create(): Unit = {
    setScreen(new GameScreen(this))
  }
}

