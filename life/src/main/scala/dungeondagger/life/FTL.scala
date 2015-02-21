package dungeondagger.life

import com.badlogic.gdx.Game

class FTL extends Game {
  val AssetsPath = "data"
//  val world = new World(150, 100, 2)
  val ship = new Boat
  val caravel = new Caravel

//  def cell(x: Int, y: Int) = world.cell(x, y)

  override def create(): Unit = {
    setScreen(new FtlScreen(this))
  }
}
