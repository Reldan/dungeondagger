package dungeondagger

import scala.util.Random

/**
 * Created by etsvigun on 20/01/15.
 */
class World(
             val height:Int = 150,
             val width:Int =  150) {

  val rand = new Random()
  val gen = Generator.newGen()
  var map: Array[Terrain] = generateMap

  def generateMap = Generator.terrain(width, height, Terrains.All.size - 1, gen).map{
    Terrains.All
  }

  def regenerateMap() = {
    gen.setSeed(rand.nextInt(1000))
    map = generateMap
  }

  def canPass(newPosition: Int) = map(newPosition).passThrough

}
