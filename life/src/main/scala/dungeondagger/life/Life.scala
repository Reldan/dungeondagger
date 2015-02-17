package dungeondagger.life

import com.badlogic.gdx.Game

class Life extends Game {
  val AssetsPath = "data"
  val world = new World(150, 150)

  def cell(x: Int, y: Int) = world.cell(x, y)

  override def create(): Unit = {
    setScreen(new LifeScreen(this))
  }
}
