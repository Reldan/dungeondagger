package dungeondagger

sealed case class Terrain(id: Int, passThrough: Boolean)

object Terrains {

  val Water = Terrain(0, false)
  val Sand  = Terrain(1, true)
  val Lava  = Terrain(2, true)
  val Dirt  = Terrain(3, true)
  val Magic = Terrain(4, true)
  val Grass = Terrain(5, true)
  val Rock  = Terrain(6, true)
  val Stone = Terrain(7, true)

  val All: Map[Int, Terrain] = Seq(Water, Sand, Lava, Dirt, Magic, Grass, Rock, Stone)
    .map(el => el.id -> el)(scala.collection.breakOut)

}
