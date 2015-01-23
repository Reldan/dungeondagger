package dungeondagger

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Pixmap.Format
import com.badlogic.gdx.graphics.{Pixmap, Texture}

import scala.util.Random

/**
 * Created by etsvigun on 23/01/15.
 */
object TextureForge {
  def path(hexName: String) =
    s"data/hexagonTiles/Tiles/tile$hexName.png"

  def flowersPath(color: String) =
    s"data/hexagonTiles/Tiles/flower$color.png"

  def treePath(treeType: String) =
    s"data/hexagonTiles/Tiles/tree$treeType.png"

  val tiles = Array("Water_full", "Sand", "Dirt", "Grass", "Autumn", "Lava", "Magic", "Rock", "Stone")
    .map(path)
    .map(Gdx.files.local)
    .map(new Texture(_))

  val flowers = Array("Blue", "Red", "Green", "Red", "White", "Yellow")
    .map(flowersPath)
    .map(Gdx.files.local)
    .map(new Texture(_))

  val cactiPixmaps = Array("Cactus_1", "Cactus_2", "Cactus_3")
    .map(treePath)
    .map(Gdx.files.local)
    .map(new Pixmap(_))

//  val trees = Array("Autumn_high", "Autumn_low", "Autumn_mid",
//    "Blue_high", "Blue_low", "Blue_mid",
//    "Green_high", "Green_low", "Green_mid")
//    .map(treePath)
//    .map(Gdx.files.local)
//    .map(new Texture(_))

  def plantPixmaps(plant: Plant) = plant match {
    case Plants.Cactus => cactiPixmaps
    case Plants.Tree => treesPixmaps
    case Plants.Flower => flowersPixmaps
  }

  def plantsPixmapsOnTerrain(terrain: Terrain): Seq[Pixmap] = {
    Plants.All.filter(_.canGrow(terrain)) map plantPixmaps flatten
  }


  val flowersPixmaps = Array("Blue", "Red", "Green", "Red", "White", "Yellow")
    .map(flowersPath)
    .map(Gdx.files.local)
    .map(new Pixmap(_))

  val treesPixmaps = Array("Autumn_high", "Autumn_low", "Autumn_mid",
    "Blue_high", "Blue_low", "Blue_mid",
    "Green_high", "Green_low", "Green_mid")
    .map(treePath)
    .map(Gdx.files.local)
    .map(new Pixmap(_))



  val plantLocations = Vector((40, 20), (20, 40), (50, 45))


  def emptyTileTexture(terrain:Terrain) = new Texture(Gdx.files.local(path(terrain.name)))

  val rand = new Random()

  Pixmap.setFilter(Pixmap.Filter.NearestNeighbour)

  val tilesTextures:Map[Terrain, Vector[Vector[Texture]]] = Terrains.All.values.map{ terrain =>

    def emptyTilePixmap = new Pixmap(Gdx.files.local(path(terrain.name)))

    val plantPixmaps = plantsPixmapsOnTerrain(terrain)
    val tileWithDecorationsTextures = (1 to Math.min(3, plantPixmaps.size)).map{ plantsNumber =>
      plantPixmaps.combinations(plantsNumber).map{combination =>
        val pixmap = new Pixmap(emptyTilePixmap.getWidth, emptyTilePixmap.getHeight + 90 + 24 * terrain.height, Format.RGBA8888)

        (terrain.height to 0 by -1).foreach { i =>
          pixmap.drawPixmap(emptyTilePixmap, 0, 90 + i * 24)
        }

        combination.zipWithIndex.foreach{
        case (plant:Pixmap, i:Int) =>
          val (x,y) = plantLocations(i)
          pixmap.drawPixmap(plant, x - plant.getWidth/2, y + 90 - plant.getHeight) //  + rand.nextInt(5), y + rand.nextInt(5))
        }

      new Texture(pixmap)
    }.toVector}.toVector


    val pixmap = new Pixmap(emptyTilePixmap.getWidth, emptyTilePixmap.getHeight + 90 + 24 * terrain.height, Format.RGBA8888)

    (terrain.height to 0 by -1).foreach { i =>
      pixmap.drawPixmap(emptyTilePixmap, 0, 90 + i * 24)
    }

    (terrain, Vector(new Texture(pixmap)) +: tileWithDecorationsTextures)
  }.toMap

  println(tilesTextures.map{
    case (terrain, textures) => (terrain.name, textures.map(_.size))
  })

  def disposeTextures() = {
    tiles foreach {_.dispose}
  }
}
