package dungeondagger

import com.badlogic.gdx.graphics.g2d.{Sprite, Batch}
import com.badlogic.gdx._
import com.badlogic.gdx.graphics.{Texture, GL20}
import com.badlogic.gdx.math.{Vector3, Interpolation}
import com.badlogic.gdx.scenes.scene2d.actions.{DelayAction, SequenceAction, MoveByAction}
import com.badlogic.gdx.scenes.scene2d.{Actor, Stage}

import scala.collection.mutable
import scala.util.Random

class GameScreen(game: Game) extends DefaultScreen(game) with InputProcessor {


//  println(Gdx.files.getExternalStoragePath)



  Gdx.input.setInputProcessor(this)

  val rand = new Random()

  val person = new PlayerAgent()
  var personPos = (151 * 150) / 2
  val world = new World(width = 150, height = 150)
  def w = world.width
  def h = world.height
  world.addAgent(person, personPos)
  (0 to 500).foreach { _ => world.addAgent(new RandomFrogAgent(world))}

  val personTexture = new Texture(Gdx.files.internal("data/hexagonTiles/Tiles/alienPink.png"))
  val frogTexture = new Texture(Gdx.files.internal("data/hexagonTiles/frog.png"))
  val frogDeadTexture = new Texture(Gdx.files.internal("data/hexagonTiles/frog_dead.png"))
//  val personTexture = new Texture(Gdx.files.internal("data/hexagonTiles/village.gif"))
  val castleTexture = new Texture(Gdx.files.internal("data/hexagonTiles/village.gif"))
  val fishTexture = new Texture(Gdx.files.internal("data/hexagonTiles/fish.png"))
  val campfireTexture = new Texture(Gdx.files.internal("data/hexagonTiles/campfire.png"))
  val appleTexture = new Texture(Gdx.files.internal("data/hexagonTiles/apple.png"))
  val bananaTexture = new Texture(Gdx.files.internal("data/hexagonTiles/banana.png"))

  val agentSprites = Map(
    AgentKind.Player -> new Sprite(personTexture),
    AgentKind.Frog -> new Sprite(frogTexture))

//  Gdx.graphics.setContinuousRendering(false)
  Gdx.graphics.requestRendering()

  val stage: Stage = new Stage()

  override def dispose() {
    stage.dispose()
    personTexture.dispose()
    TextureForge.disposeTextures()
  }

  case class Decoration(texture: Texture, dx: Int, dy: Int, w: Int, h: Int)



  class HexTile(val terrain: Terrain) extends Actor {
    val tts = TextureForge.tilesTextures(terrain)
    val numberOfPlants = Math.min(tts.size - 1, rand.nextInt(20) match {
      case n if n < 15 => 0
      case n if n < 17 => 1
      case n if n < 19 => 2
      case _ => 3 })
    val variations = tts(numberOfPlants)
    val vn = rand.nextInt(variations.size)
    val emptyTexture = tts(0)(0)
    val decoratedTexture = variations(vn)

    var started = false
    var agent: Option[AgentKind.Value] = None

    val decorations = mutable.MutableList.empty[Decoration]
//    val plants = generatePlants(terrain)
//
//    if (plants.nonEmpty) {
//      plants.foreach { plant =>
//        Decoration(plantTexture(plant), 35, 0, 0, 0) +=: decorations
//      }
//    } else if (terrain == Terrains.Grass && rand.nextInt(300) == 0) {
//      Decoration(campfireTexture, 10, 0, 50, 50) +=: decorations
//    } else if (terrain == Terrains.Grass && rand.nextInt(20) == 0) {
//      Decoration(appleTexture, 10, 0, 50, 50) +=: decorations
//    } else if (terrain == Terrains.Sand && rand.nextInt(20) == 0) {
//      Decoration(bananaTexture, 10, 0, 50, 50) +=: decorations
//    } else if (terrain == Terrains.Water && rand.nextInt(10) == 0) {
//      Decoration(fishTexture, 10, 0, 40, 40) +=: decorations
//    }

    private var corpseTexture:Texture = null
    private var corpseTimer = 0

    def addCorpse(texture:Texture):Unit = {
      corpseTexture = texture
      corpseTimer = 300
    }

    override def draw(batch: Batch, alpha: Float) {
//      Range(0, terrain.height).foreach { i =>
//        batch.draw(emptyTexture, getX, getY + i * 24)
//      }
      batch.draw(decoratedTexture, getX, getY)// + terrain.height * 24)
      val attrY = getY + terrain.height * 24 + 35
//      decorations.foreach {
//        case Decoration(tex, dx, dy, 0, 0) => batch.draw(tex, getX + dx, attrY + dy)
//        case Decoration(tex, dx, dy, w, h) => batch.draw(tex, getX + dx, attrY + dy, w, h)
//      }

      if(corpseTimer>0){
        val c = batch.getColor
        batch.setColor(c.r, c.g, c.b, corpseTimer / 300f)
        batch.draw(corpseTexture, getX, attrY)
        batch.setColor(c)
        corpseTimer -= 1
      }

      agent map agentSprites map {
        batch.draw(_, getX, attrY)
      }
    }
  }

  def tActors = Range(0, w).map { i =>
    Range(0, h).map { j =>
      val tileId = i * w + j
      val terrain = world.map(tileId)
      val t = TextureForge.tiles(terrain.id)
      val x = j * 65 + i * 32
      val tile = new HexTile(terrain)
      tile.setPosition(x, i * 45)
      tile
    }
  }.flatten


  var tileActors = tActors

  tileActors.reverse foreach stage.addActor

  tileActors(0).agent = Some(AgentKind.Player)

  def moveAgent(kind: AgentKind.Value, from: Int, to: Int): Actor = {
    tileActors(from).agent = None
    val newTile = tileActors(to)
    newTile.agent = Some(kind)
    newTile
  }

  val CAMERA_TIMER_MAX = 15f
  var cameraTimer = CAMERA_TIMER_MAX
  var cameraOldPos:Vector3 = Vector3.Zero
  var cameraNewPos:Vector3 = Vector3.Zero

  def movePerson(from: Int, to: Int): Unit = {
    cameraOldPos = stage.getViewport.getCamera.position
    personPos = to
    val centerTile = moveAgent(AgentKind.Player, from, to)
    cameraNewPos = new Vector3(centerTile.getX, centerTile.getY + 35, 0)
    cameraTimer = CAMERA_TIMER_MAX
  }
  movePerson(0, personPos)

  def showAgentsDeath(kind: AgentKind.Value, at: Int):Unit = {
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
      case AgentMoved(a, from, to) if a == person => movePerson(from, to)
      case AgentMoved(a, from, to) => moveAgent(a.kind, from, to)
      case AgentDied(a, at) => showAgentsDeath(a.kind, at)
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
