package dungeondagger

sealed case class Terrain(
   id: Int,
   name: String,
   height: Int,
   passThrough: Boolean,
   temperature: Int)

case class Plant(id: Int, minTemperature: Int, maxTemperature: Int) {
  def canGrow(terrain: Terrain) =
    terrain.temperature <= maxTemperature && terrain.temperature >= minTemperature && terrain != Terrains.Water
}

object Plants {
  val Tree  = Plant(0, 10, 30)
  val Cactus = Plant(1, 25, 50)
  val Flower = Plant(2, 20, 25)

  val All = Seq(Tree, Cactus, Flower)
}


object Terrains {

  val Water = Terrain(0, "Water", 0, false, 10)
  val Sand  = Terrain(1, "Sand", 1, true, 40)
  val Dirt  = Terrain(2, "Dirt", 1, true, 20)
  val Grass = Terrain(3, "Grass", 1, true, 20)
  val Lava  = Terrain(4, "Lava", 1, true, 300)
  val Magic = Terrain(5, "Magic", 1, true, 0)
  val Rock  = Terrain(6, "Rock", 2, false, 0)
  val Stone = Terrain(7, "Stone", 2, false, 0)

  val All: Map[Int, Terrain] = Seq(Water, Sand, Lava, Dirt, Magic, Grass, Rock, Stone)
    .map(el => el.id -> el)(scala.collection.breakOut)

}
