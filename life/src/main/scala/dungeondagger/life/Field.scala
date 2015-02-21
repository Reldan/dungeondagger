package dungeondagger.life

class Field(val height: Int, val width: Int, data: Array[Int]) {

  def cell(x: Int, y: Int) = data(x + y * width)

  def liveNeighbors(x: Int, y: Int) =
    List(
      (x + 1, y),
      (x - 1, y),
      (x, y - 1),
      (x, y + 1),
      (x + 1, y + 1),
      (x + 1, y - 1),
      (x - 1, y + 1),
      (x - 1, y - 1)).collect {
      case(x, y) if x >= 0 && y >= 0 && y < height && x < width && cell(x, y) != 0 => cell(x, y)
    }.groupBy(x => x)

  def nextGenCell(pos: Int) = {
    val x = pos % width
    val y = pos / width
    val neighbors = liveNeighbors(x, y)
    cell(x, y) match {
      case 0 if neighbors.exists{ _._2.size == 3 } => neighbors.find(_._2.size == 3).get._1
      case x if x != 0 && neighbors.contains(x) && neighbors(x).size == neighbors.maxBy(_._2.size)._2.size && (neighbors(x).size == 2 || neighbors(x).size == 3) => x
      case _ => 0
    }
  }

  def produceNext() = {
    new Field(height, width, Range(0, height * width).map(i => nextGenCell(i)).toArray)
  }

}

