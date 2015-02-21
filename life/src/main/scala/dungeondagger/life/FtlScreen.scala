package dungeondagger.life

import com.badlogic.gdx.graphics.g2d.{TextureRegion, SpriteBatch}
import com.badlogic.gdx.graphics.{GL20, Texture}
import com.badlogic.gdx.{Input, InputProcessor, Gdx, Screen}

class FtlScreen(val game: FTL) extends Screen with InputProcessor {
  val spriteBatch   = new SpriteBatch()
  val titleTexture  = new Texture(Gdx.files.internal(s"${game.AssetsPath}/title.png"))
  val cannonTexture = new Texture(Gdx.files.internal(s"${game.AssetsPath}/cannon.png"))
  val waterTexture  = new Texture(Gdx.files.internal(s"${game.AssetsPath}/water.png"))
  val deckTexture   = new Texture(Gdx.files.internal(s"${game.AssetsPath}/deck.png"))
  val boardTexture  = new Texture(Gdx.files.internal(s"${game.AssetsPath}/board.png"))
  val pirateTexture = new Texture(Gdx.files.internal(s"${game.AssetsPath}/pirate.png"))
  val ladderTexture = new Texture(Gdx.files.internal(s"${game.AssetsPath}/ladder.png"))

  val pirateTextureRegion = new TextureRegion(pirateTexture)

  Gdx.input.setInputProcessor(this)
  val PixelSize = 32
  val ScreenWidth = 38
  val ScreenHeight = 25

  def drawPixel(texture: Texture, x: Int, y: Int, x0: Int, y0: Int, flipX: Boolean, flipY: Boolean) =
    spriteBatch.draw(texture, (x + x0) * PixelSize, (ScreenHeight - 1 - y - y0) * PixelSize,
      PixelSize, PixelSize, 0, 0, 8, 8, flipX, flipY)

  def directionToDegree(char: Char) = char match {
    case 'U' => 0f
    case 'D' => 180f
    case 'R' => -90f
    case 'L' => 90f
  }

  def drawPixel(texture: TextureRegion, x: Int, y: Int, x0: Int, y0: Int, direction: Char) =
    spriteBatch.draw(texture, (x + x0) * PixelSize, (ScreenHeight - 1 - y - y0) * PixelSize,
      16, 16, PixelSize, PixelSize, 1, 1, directionToDegree(direction))


  def drawPixel(texture: Texture, x: Int, y: Int, x0: Int, y0: Int) =
    spriteBatch.draw(texture, (x + x0) * PixelSize, (ScreenHeight - 1 - y - y0) * PixelSize, PixelSize, PixelSize)

  def drawShip(ship: Ship, x0: Int, y0: Int) = {

    var y = 0
    var x = 0

    while (y < ship.height) {
      x = 0
      while (x < ship.width) {
        ship.rooms(y)(x) match {
          case 'X' => drawPixel(titleTexture, x, y, x0, y0)
          case '^' =>
            drawPixel(titleTexture, x, y, x0, y0)
            drawPixel(cannonTexture, x, y, x0, y0)
          case 'v' =>
            drawPixel(titleTexture, x, y, x0, y0)
            drawPixel(cannonTexture, x, y, x0, y0, false, true)
          case '0' => drawPixel(deckTexture, x, y, x0, y0)
          case 'L' => drawPixel(ladderTexture, x, y, x0, y0)
          case _ =>
        }
        x += 1
      }
      y += 1
    }

    ship.pirates.foreach(p => drawPixel(pirateTextureRegion, p.x, p.y, x0, y0, p.front))
    ship.captain.foreach(p => drawPixel(pirateTextureRegion, p.x, p.y, x0, y0, p.front))
  }

  def drawShipWindow(ship: Ship, x0: Int, y0: Int, width: Int, height: Int): Unit = {
    var i = 0
    var j = 0

    while (i < height) {
      j = 0
      while (j < width) {
        drawPixel(waterTexture, j, i, x0, y0)
        j += 1
      }
      i += 1
    }
    i = 0
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


    drawShip(ship, x0 + width / 2 - ship.width / 2 + 1,
             y0 + height / 2 - ship.height / 2)
    ship.movePirates

  }


  override def render(delta: Float): Unit = {
    Gdx.gl.glClearColor(1, 1, 1, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    spriteBatch.begin()
    drawShipWindow(game.caravel, 0, 0, 27, 10)
    drawShipWindow(game.ship, 0, 11, 27, 10)
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
    case (Input.Keys.UP) => game.caravel.moveCaptain('U')
    case (Input.Keys.DOWN) => game.caravel.moveCaptain('D')
    case (Input.Keys.RIGHT) => game.caravel.moveCaptain('R')
    case (Input.Keys.LEFT) => game.caravel.moveCaptain('L')
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