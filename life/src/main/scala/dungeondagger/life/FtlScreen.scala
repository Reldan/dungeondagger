package dungeondagger.life

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.{GL20, Texture}
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.{Input, InputProcessor, Gdx, Screen}

class FtlScreen(val game: FTL) extends Screen with InputProcessor {

  val stage: Stage = new Stage()
  val spriteBatch = new SpriteBatch()
  val titleTexture = new Texture(Gdx.files.internal(s"${game.AssetsPath}/title.png"))
  val waterTexture = new Texture(Gdx.files.internal(s"${game.AssetsPath}/water.png"))
  val bananaTexture = new Texture(Gdx.files.internal(s"${game.AssetsPath}/deck.png"))
  val boardTexture = new Texture(Gdx.files.internal(s"${game.AssetsPath}/board.png"))
  val pirateTexture = new Texture(Gdx.files.internal(s"${game.AssetsPath}/pirate.png"))

  Gdx.input.setInputProcessor(this)
  val PixelSize = 32
  val ScreenWidth = 38
  val ScreenHeight = 25

  def drawPixel(texture: Texture, x: Int, y: Int, x0: Int, y0: Int) =
    spriteBatch.draw(texture, (x + x0) * PixelSize, (ScreenHeight - 1 - y - y0) * PixelSize, PixelSize, PixelSize)

  def drawShip(x0: Int, y0: Int) = {

    var y = 0
    var x = 0

    while (y < game.ship.height) {
      x = 0
      while (x < game.ship.width) {
        game.ship.rooms(y)(x) match {
          case 'X' => drawPixel(titleTexture, x, y, x0, y0)
          case 'W' => drawPixel(waterTexture, x, y, x0, y0)
          case _ => drawPixel(bananaTexture, x, y, x0, y0)
        }
        x += 1
      }
      y += 1
    }

    game.ship.pirates.foreach(p => drawPixel(pirateTexture, p.x, p.y, x0, y0))
  }

  def drawShipWindow(x0: Int, y0: Int, width: Int, height: Int): Unit = {
    var i = 0
    while (i <= width) {
      drawPixel(boardTexture, i, 0, x0, y0)
      drawPixel(boardTexture, i, height, x0, y0)
      i += 1
    }

    i = 0

    while (i <= height) {
      drawPixel(boardTexture, 0, i, x0, y0)
      drawPixel(boardTexture, width, i, x0, y0)
      i += 1
    }

    drawShip(x0 + 1, y0 + 1)
    game.ship.movePirates

  }


  override def render(delta: Float): Unit = {
    Gdx.gl.glClearColor(1, 1, 1, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    spriteBatch.begin()
    drawShipWindow(0, 0, 27, 10)
    spriteBatch.end()

  }

  override def hide(): Unit = {}

  override def resize(width: Int, height: Int): Unit = {}

  override def dispose(): Unit = {}

  override def pause(): Unit = {}

  override def show(): Unit = {}

  override def resume(): Unit = {}

  override def keyDown(keycode: Int): Boolean = {
    keycode match {
    case (Input.Keys.ESCAPE) => System.exit(0)
    case (Input.Keys.UP) =>
      print("u[")
    case (Input.Keys.R) =>
      print("u[")
    case _ =>
  }
  true
}

  override def mouseMoved(screenX: Int, screenY: Int): Boolean = true

  override def keyTyped(character: Char): Boolean = true

  override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = true

  override def keyUp(keycode: Int): Boolean =true

  override def scrolled(amount: Int): Boolean = true

  override def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = true

  override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = true
}