package dungeondagger.life

import scala.util.Random

case class World(val width: Int, val height: Int) {
  require(width >= 0)
  require(height >= 0)
  val random = new Random()

  var field = new Field(height, width, Array.fill(width * height){random.nextBoolean()})

  def cell(x: Int, y: Int) = field.cell(x, y)

  def step(): Unit = {
    field = field.produceNext()
  }

  def rand() = field = new Field(height, width, Array.fill(width * height){random.nextBoolean()})

}
