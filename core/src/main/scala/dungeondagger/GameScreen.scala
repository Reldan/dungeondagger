package dungeondagger

import com.badlogic.gdx.graphics.g2d.{Sprite, Batch}
import com.badlogic.gdx._
import com.badlogic.gdx.graphics.{Texture, GL20}
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.{DelayAction, SequenceAction, MoveByAction}
import com.badlogic.gdx.scenes.scene2d.{Actor, Stage}

import scala.collection.mutable
import scala.util.Random

class GameScreen(game: Game) extends DefaultScreen(game) with InputProcessor {
  def path(hexName: String) =
    s"data/hexagonTiles/Tiles/tile$hexName.png"

  def flowersPath(color: String) =
    s"data/hexagonTiles/Tiles/flower$color.png"

  def treePath(treeType: String) =
    s"data/hexagonTiles/Tiles/tree$treeType.png"

  println(Gdx.files.getExternalStoragePath)
  val textures = Array("Water_full", "Sand", "Dirt", "Grass", "Autumn", "Lava", "Magic", "Rock", "Stone")
    .map(path)
    .map(Gdx.files.local)
    .map(new Texture(_))

  val flowers = Array("Blue", "Red", "Green", "Red", "White", "Yellow")
    .map(flowersPath)
    .map(Gdx.files.local)
    .map(new Texture(_))

  val trees = Array("Autumn_high", "Autumn_low", "Autumn_mid",
    "Blue_high", "Blue_low", "Blue_mid",
    "Cactus_1", "Cactus_2", "Cactus_3",
    "Green_high", "Green_low", "Green_mid")
    .map(treePath)
    .map(Gdx.files.local)
    .map(new Texture(_))


  Gdx.input.setInputProcessor(this)

  val rand = new Random()

  val person = new PlayerAgent()
  var personPos = (151 * 150) / 2
  val world = new World(width = 150, height = 150)
  def w = world.width
  def h = world.height
  world.addAgent(person, personPos)
  (0 to 500).foreach{_ => world.addAgent(new RandomFrogAgent(world))}

  val personTexture = new Texture(Gdx.files.internal("data/hexagonTiles/Tiles/alienPink.png"))
  val frogTexture = new Texture(Gdx.files.internal("data/hexagonTiles/frog.png"))
//  val personTexture = new Texture(Gdx.files.internal("data/hexagonTiles/village.gif"))
  val castleTexture = new Texture(Gdx.files.internal("data/hexagonTiles/village.gif"))
  val fishTexture = new Texture(Gdx.files.internal("data/hexagonTiles/fish.png"))
  val campfireTexture = new Texture(Gdx.files.internal("data/hexagonTiles/campfire.png"))
  val agentSprites = Map(AgentKind.Player -> new Sprite(personTexture),
                         AgentKind.Frog   -> new Sprite(frogTexture))

  val stage:Stage = new Stage()

  override def dispose() {
    stage.dispose()
    personTexture.dispose()
    textures.map(_.dispose())
  }

  class HexTile(texture: Texture, val terrain: Terrain) extends Actor {
    case class Decoration(texture:Texture, dx: Int, dy:Int, w:Int, h:Int)

    var started = false
    var agent : Option[AgentKind.Value] = None

    val decorations = mutable.MutableList.empty[Decoration]

    if(terrain != Terrains.Water && rand.nextInt(10) == 0){
      val flowerTexture = flowers(rand.nextInt(flowers.size))
      Decoration(flowerTexture, 35, 0, 0, 0) +=: decorations
    } else if(terrain != Terrains.Water && rand.nextInt(15) == 0){
      val treeTexture = trees(rand.nextInt(trees.size))
      Decoration(treeTexture, 35, 0, 0, 0) +=: decorations
    } else if(terrain == Terrains.Grass && rand.nextInt(300) == 0){
      Decoration(campfireTexture, 10, 0, 50, 50) +=: decorations
    } else if(terrain == Terrains.Water && rand.nextInt(10) == 0){
      Decoration(fishTexture, 10, 0, 40, 40) +=: decorations
    }

    override def draw(batch:Batch, alpha:Float){
      Range(0, terrain.height + 1).foreach { i =>
        batch.draw(texture, getX, getY + i * 24 )
      }
      val attrY = getY + terrain.height * 24 + 35
      decorations.foreach {
        case Decoration(tex, dx, dy, 0, 0) => batch.draw(tex, getX + dx, attrY + dy)
        case Decoration(tex, dx, dy, w, h) => batch.draw(tex, getX + dx, attrY + dy, w, h)
      }

      agent map agentSprites map {batch.draw(_, getX, attrY)}
    }
  }

  def tActors = Range(0, w).map{ i =>
    Range(0, h).map { j =>
      val tileId = i * w + j
      val terrain = world.map(tileId)
      val t = textures(terrain.id)
      val x = j * 65 + (i % 2) * 32
      val tile = new HexTile(t, terrain)
      tile.setPosition(x,i * 49)
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

  def movePerson(from: Int, to: Int): Unit ={
    personPos = to
    val centerTile = moveAgent(AgentKind.Player, from, to)
    stage.getViewport.getCamera.position.set(centerTile.getX, centerTile.getY + 35, 0)
  }
  movePerson(0, personPos)

  def wobble(): Unit ={
    val center = tileActors(personPos)
    tileActors foreach { a =>
      val dx = center.getX - a.getX
      val dy = center.getY - a.getY
      val r2 = dx * dx + dy * dy
//      val r = Math.sqrt(r2).toFloat
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
      val action = new SequenceAction(delay,there,back)
      a.addAction(action)
    }
  }

  def processWorldEvents():Unit = {
    world.step() foreach {
      case AgentMoved(a, from, to) if a == person => movePerson(from, to)
      case AgentMoved(a, from, to) => moveAgent(a.kind, from, to)
      case _ =>
    }
  }

  override def render(delta: Float) {
    Gdx.gl.glClearColor(1, 1, 1, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    stage.act(Gdx.graphics.getDeltaTime)
    stage.draw()
  }


  override def keyDown(keycode: Int): Boolean = {
    keycode match {
      case(Input.Keys.ESCAPE) => System.exit(0)
      case(Input.Keys.UP) => person.go(0)
      case(Input.Keys.DOWN) => person.go(2)
      case(Input.Keys.RIGHT) => person.go(1)
      case(Input.Keys.LEFT) => person.go(3)
      case(Input.Keys.R) =>
        world.regenerateMap()
        tileActors = tActors
        stage.clear()
        tileActors.reverse foreach stage.addActor
      case(Input.Keys.W) => wobble()
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
