package dungeondagger

import com.badlogic.gdx.graphics.g2d.{Sprite, Batch}
import com.badlogic.gdx._
import com.badlogic.gdx.graphics.{Texture, GL20}
import com.badlogic.gdx.math.{Vector3, Interpolation}
import com.badlogic.gdx.scenes.scene2d.actions.{DelayAction, SequenceAction, MoveByAction}
import com.badlogic.gdx.scenes.scene2d.{Actor, Stage}

import scala.collection.mutable
import scala.util.Random

class GameScreen(game: DungeonDagger) extends DefaultScreen(game) with InputProcessor {
  def path(hexName: String) =
    s"${game.AssetsPath}/hexagonTiles/Tiles/tile$hexName.png"

  def flowersPath(color: String) =
    s"${game.AssetsPath}/hexagonTiles/Tiles/flower$color.png"

  def treePath(treeType: String) =
    s"${game.AssetsPath}/hexagonTiles/Tiles/tree$treeType.png"

  val textures = Array("Water_full", "Sand", "Dirt", "Grass", "Autumn", "Lava", "Magic", "Rock", "Stone")
    .map(path)
    .map(Gdx.files.internal)
    .map(new Texture(_))

  val flowers = Array("Blue", "Red", "Green", "Red", "White", "Yellow")
    .map(flowersPath)
    .map(Gdx.files.internal)
    .map(new Texture(_))

  val cacti = Array("Cactus_1", "Cactus_2", "Cactus_3")
    .map(treePath)
    .map(Gdx.files.internal)
    .map(new Texture(_))

  val trees = Array("Autumn_high", "Autumn_low", "Autumn_mid",
    "Blue_high", "Blue_low", "Blue_mid",
    "Green_high", "Green_low", "Green_mid")
    .map(treePath)
    .map(Gdx.files.internal)
    .map(new Texture(_))


  Gdx.input.setInputProcessor(this)

  val rand = new Random()

  val person = new PlayerAgent()
  var personPos = (151 * 150) / 2
  val world = new World(width = 150, height = 150)
  def w = world.width
  def h = world.height
  world.addAgent(person, personPos)
  (0 to 50).foreach { _ => world.addAgent(new RandomFrogAgent)}
  (0 to 300).foreach { _ => world.addAgent(new GetAwayFrogReflexAgent())}

  val personTexture = new Texture(Gdx.files.internal(s"${game.AssetsPath}/hexagonTiles/Tiles/alienPink.png"))
  val frogTexture = new Texture(Gdx.files.internal(s"${game.AssetsPath}/hexagonTiles/frog.png"))
  val frogDeadTexture = new Texture(Gdx.files.internal(s"${game.AssetsPath}/hexagonTiles/frog_dead.png"))
  val grassTexture =  new Texture(Gdx.files.internal(s"${game.AssetsPath}/hexagonTiles/Tiles/bushGrass.png"))
//  val personTexture = new Texture(Gdx.files.internal(s"${game.AssetsPath}/hexagonTiles/village.gif"))
  val castleTexture = new Texture(Gdx.files.internal(s"${game.AssetsPath}/hexagonTiles/village.gif"))
  val fishTexture = new Texture(Gdx.files.internal(s"${game.AssetsPath}/hexagonTiles/fish.png"))
  val campfireTexture = new Texture(Gdx.files.internal(s"${game.AssetsPath}/hexagonTiles/campfire.png"))
  val appleTexture = new Texture(Gdx.files.internal(s"${game.AssetsPath}/hexagonTiles/apple.png"))
  val bananaTexture = new Texture(Gdx.files.internal(s"${game.AssetsPath}/hexagonTiles/banana.png"))
  val buildingTexture = new Texture(Gdx.files.internal(s"${game.AssetsPath}/buildings/PNG/stoneDoorWindowBlinds.png"))
  val buildingRoofTexture = new Texture(Gdx.files.internal(s"${game.AssetsPath}/buildings/PNG/stoneRoofShort.png"))

  def agentTexture(a:Agent):Texture = {
    a match {
      case _:PlayerAgent => personTexture
      case _:RandomFrogAgent | _:GetAwayFrogReflexAgent => frogTexture
      case _ => grassTexture
    }
  }

//  Gdx.graphics.setContinuousRendering(false)
  Gdx.graphics.requestRendering()

  val stage: Stage = new Stage()

  override def dispose() {
    stage.dispose()
    personTexture.dispose()
    textures.foreach(_.dispose())
  }

  case class Decoration(texture: Texture, dx: Int, dy: Int, w: Int, h: Int)

  def generatePlants(terrain: Terrain): Seq[Plant] = {
    val cactus = if (Plants.Cactus.canGrow(terrain) && rand.nextInt(10) == 0) Some(Plants.Cactus) else None
    val tree = if (Plants.Tree.canGrow(terrain) && rand.nextInt(10) == 0) Some(Plants.Tree) else None
    val flower = if (Plants.Flower.canGrow(terrain) && rand.nextInt(10) == 0) Some(Plants.Flower) else None
    List(cactus, tree, flower).flatten
  }
  
  def plantTexture(plant: Plant) = plant match {
    case Plants.Cactus => cacti(rand.nextInt(cacti.size))
    case Plants.Tree => trees(rand.nextInt(trees.size))
    case Plants.Flower => flowers(rand.nextInt(flowers.size))
  }

  class HexTile(val worldCell: World.Cell, val building: Boolean) extends Actor {
    val texture = textures(worldCell.terrain.id)
    var started = false
    var agent: Option[Agent] = None //TODO consider just showing agents from worldCell, but what about animation then?

    val decorations = mutable.MutableList.empty[Decoration]
    val plants = generatePlants(worldCell.terrain)

    if (building) {
      Decoration(buildingRoofTexture, 0, 15, 65, 65) +=: decorations
      Decoration(buildingTexture, 0, 0, 65, 65) +=: decorations
    }
    else if (plants.nonEmpty) {
      plants.foreach { plant =>
        Decoration(plantTexture(plant), 35, 0, 0, 0) +=: decorations
      }
    } else if (worldCell.terrain == Terrains.Grass && rand.nextInt(300) == 0) {
      Decoration(campfireTexture, 10, 0, 50, 50) +=: decorations
    } else if (worldCell.terrain == Terrains.Grass && rand.nextInt(20) == 0) {
      Decoration(appleTexture, 10, 0, 50, 50) +=: decorations
    } else if (worldCell.terrain == Terrains.Sand && rand.nextInt(20) == 0) {
      Decoration(bananaTexture, 10, 0, 50, 50) +=: decorations
    } else if (worldCell.terrain == Terrains.Water && rand.nextInt(10) == 0) {
      Decoration(fishTexture, 10, 0, 40, 40) +=: decorations
    }


    private var corpseTexture:Texture = null
    private var corpseTimer = 0

    def addCorpse(texture:Texture):Unit = {
      corpseTexture = texture
      corpseTimer = 300
    }

    override def draw(batch: Batch, alpha: Float) {
      Range(0, worldCell.terrain.height + 1).foreach { i =>
        batch.draw(texture, getX, getY + i * 24)
      }
      val attrY = getY + worldCell.terrain.height * 24 + 35
      decorations.foreach {
        case Decoration(tex, dx, dy, 0, 0) => batch.draw(tex, getX + dx, attrY + dy)
        case Decoration(tex, dx, dy, w, h) => batch.draw(tex, getX + dx, attrY + dy, w, h)
      }

      if(corpseTimer>0){
        val c = batch.getColor
        batch.setColor(c.r, c.g, c.b, corpseTimer / 300f)
        batch.draw(corpseTexture, getX, attrY)
        batch.setColor(c)
        corpseTimer -= 1
      }

      agent map agentTexture foreach {
        batch.draw(_, getX, attrY)
      }
    }
  }

  def tActors = Range(0, w).map { i =>
    Range(0, h).map { j =>
      val tileId = i * w + j
      val x:Float = j * 65f + i * 32f
      val y:Float = i * 49f
      val tile = new HexTile(world.map(tileId), world.buildings.contains(tileId))
      tile.setPosition(x, y)
      tile
    }
  }.flatten


  var tileActors = tActors

  val VisibleXRadius = 14
  val VisibleYRadius = 10

  case class TilesBounds(leftX: Int, topY: Int, rightX: Int, bottomY: Int)

  def visibleAreaBounds(center: Int): TilesBounds = {
    val cy = center / w
    val cx = center % w
    TilesBounds(Math.max(cx - VisibleXRadius, 0), Math.min(cy + VisibleYRadius, h - 1),
      Math.min(cx + VisibleXRadius, w - 1), Math.max(cy - VisibleYRadius, 0))
  }

  def area(bounds: TilesBounds): IndexedSeq[Int] = {
    val TilesBounds(l, t, r, b) = bounds
    for (y <- t to b by -1; x <- l to r) yield y * w + x
  }

  def addArea(bounds: TilesBounds): Unit = {
    area(bounds).foreach { i => stage.addActor(tileActors(i))}
  }

  def addAreaOnTopOrLeft(bounds: TilesBounds): Unit = {
    area(bounds).reverse.map(tileActors(_)).foreach { a =>
      stage.addActor(a)
      a.toBack()
    }
  }

  def removeArea(bounds: TilesBounds): Unit = {
    area(bounds).foreach { i => tileActors(i).remove()}
  }

  def shiftVisibleArea(from: Int, to: Int): Unit = {
    val TilesBounds(ol, ot, or, ob) = visibleAreaBounds(from)
    val TilesBounds(nl, nt, nr, nb) = visibleAreaBounds(to)
    if (nl > ol)
      removeArea(TilesBounds(ol, ot, ol, ob))
    if (nl < ol)
      addAreaOnTopOrLeft(TilesBounds(nl, nt, nl, nb))

    if (nr < or)
      removeArea(TilesBounds(or, ot, or, ob))
    if (nr > or)
      addArea(TilesBounds(nr, nt, nr, nb))

    if (nb > ob)
      removeArea(TilesBounds(ol, ob, or, ob))
    if (nb < ob)
      addArea(TilesBounds(nl, nb, nr, nb))

    if (nt < ot)
      removeArea(TilesBounds(ol, ot, or, ot))
    if (nt > ot)
      addAreaOnTopOrLeft(TilesBounds(nl, nt, nr, nt))
  }

  tileActors(0).agent = Some(person)

  def moveAgent(a:Agent, from: Int, to: Int): Actor = {
    tileActors(from).agent = None
    val newTile = tileActors(to)
    newTile.agent = Some(a)
    newTile
  }

  val CAMERA_TIMER_MAX = 15f
  var cameraTimer = CAMERA_TIMER_MAX
  var cameraOldPos:Vector3 = Vector3.Zero
  var cameraNewPos:Vector3 = Vector3.Zero

  def movePerson(from: Int, to: Int): Unit = {
    cameraOldPos = stage.getViewport.getCamera.position
    personPos = to
    val centerTile = moveAgent(person, from, to)
    cameraNewPos = new Vector3(centerTile.getX, centerTile.getY + 35, 0)
    cameraTimer = CAMERA_TIMER_MAX
    shiftVisibleArea(from, to)
  }
  movePerson(personPos, personPos)
  addArea(visibleAreaBounds(personPos))

  def showAgentsDeath(a: Agent, at: Int):Unit = {
    tileActors(at).agent = None
    tileActors(at).addCorpse(frogDeadTexture)
  }

  def wobble(): Unit = {
    val center = tileActors(personPos)
    tileActors foreach { a =>
      val dx = center.getX - a.getX
      val dy = center.getY - a.getY
      val r2 = dx * dx + dy * dy
      val there = new MoveByAction()
      val back = new MoveByAction()
      there.setDuration(1.7f)
      back.setDuration(1.7f)
      val delay = new DelayAction()
      delay.setDuration(r2 / 100000)
      there.setInterpolation(Interpolation.circleOut)
      back.setInterpolation(Interpolation.circleIn)
      val h = 10000000 / (50000 + r2)
      there.setAmountY(h)
      back.setAmountY(-h)
      val action = new SequenceAction(delay, there, back)
      a.addAction(action)
    }
  }

  def processWorldEvents(): Unit = {
    world.step() foreach {
      case AgentMoved(a:PlayerAgent, from, to) => movePerson(from, to)
      case AgentMoved(a, from, to) => moveAgent(a, from, to)
      case AgentDied(a, at) => showAgentsDeath(a, at)
      case _ =>
    }
  }

  override def render(delta: Float) {
    Gdx.gl.glClearColor(1, 1, 1, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    if(cameraTimer>0){
      val p = new Vector3(cameraNewPos).sub(cameraOldPos).scl(1 - cameraTimer / CAMERA_TIMER_MAX).add(cameraOldPos)
      stage.getViewport.getCamera.position.set(p)
      cameraTimer -= 1
    }

    stage.act(Gdx.graphics.getDeltaTime)
    stage.draw()
  }


  override def keyDown(keycode: Int): Boolean = {
    keycode match {
      case (Input.Keys.ESCAPE) => System.exit(0)
      case (Input.Keys.UP | Input.Keys.NUMPAD_8 | Input.Keys.NUMPAD_9) => person.go(0)
      case (Input.Keys.RIGHT | Input.Keys.NUMPAD_6) => person.go(1)
      case (Input.Keys.NUMPAD_3) => person.go(2)
      case (Input.Keys.DOWN | Input.Keys.NUMPAD_2 | Input.Keys.NUMPAD_1) => person.go(3)
      case (Input.Keys.LEFT | Input.Keys.NUMPAD_4) => person.go(4)
      case (Input.Keys.NUMPAD_7) => person.go(5)
      case (Input.Keys.R) =>
        world.regenerateMap()
        tileActors = tActors
        stage.clear()
        tileActors.reverse foreach stage.addActor
      case (Input.Keys.W) => wobble()
      case _ =>
    }
    processWorldEvents()
    true
  }

  override def mouseMoved(screenX: Int, screenY: Int): Boolean = false

  override def keyTyped(character: Char): Boolean = false

  override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false

  override def keyUp(keycode: Int): Boolean = false

  override def scrolled(amount: Int): Boolean = false

  override def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false

  override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = false
}
