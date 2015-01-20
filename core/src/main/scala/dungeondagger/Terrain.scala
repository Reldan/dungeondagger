package dungeondagger

sealed case class Terrain(id: Int, height: Int, passThrough: Boolean)

object Terrains {

  val Water = Terrain(0, 0, false)
  val Sand  = Terrain(1, 1, true)
  val Dirt  = Terrain(2, 1, true)
  val Grass = Terrain(3, 1, true)
  val Lava  = Terrain(4, 1, true)
  val Magic = Terrain(5, 1, true)
  val Rock  = Terrain(6, 2, false)
  val Stone = Terrain(7, 2, false)

  val All: Map[Int, Terrain] = Seq(Water, Sand, Lava, Dirt, Magic, Grass, Rock, Stone)
    .map(el => el.id -> el)(scala.collection.breakOut)

}
