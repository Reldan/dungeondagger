package dungeondagger.life

class Field(val height: Int, val width: Int, data: Array[Boolean]) {

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
      (x - 1, y - 1)).count {
      case(x, y) => x >= 0 && y >= 0 && y < height && x < width && cell(x, y)
    }

  def nextGenCell(pos: Int) = {
    val x = pos % width
    val y = pos / width
    val neighbors = liveNeighbors(x, y)
    cell(x, y) match {
      case false if neighbors == 3 => true
      case true if neighbors == 2 || neighbors == 3 => true
      case _ => false
    }
  }

  def produceNext() = {
    new Field(height, width, Range(0, height * width).map(i => nextGenCell(i)).toArray)
  }

}

