package dungeondagger

import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch}
import com.badlogic.gdx._
import com.badlogic.gdx.graphics.{Texture, GL20}

import scala.util.Random

class GameScreen(game: Game) extends DefaultScreen(game) with InputProcessor {
  var batch: SpriteBatch = new SpriteBatch()
  def path(hexName: String) = s"data/hexagonTiles/Tiles/tile$hexName.png"
  val textures = List("Autumn", "Grass", "Lava", "Dirt", "Magic", "Rock", "Sand", "Stone", "Water")
    .map(path)
    .map(Gdx.files.internal)
    .map(new Texture(_))
    .toArray

  Gdx.input.setInputProcessor(this)

  val rand = new Random()

  val Height = 15
  val Width = 18

  val map: Array[Int] = Array.fill(Height * Width){ rand.nextInt(6)}

  var person = 0
  val personTexture = new Texture(Gdx.files.internal("data/hexagonTiles/Tiles/alienPink.png"))

  override def dispose() {
    batch.dispose()
    textures.map(_.dispose())
  }

  override def render(delta: Float) {
    Gdx.gl.glClearColor(1, 1, 1, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    batch.begin()

    Range(0, Height).reverse.foreach{ i =>
      Range(0, Width).foreach { j =>
        val tileId = i * Width + j
        val t = textures(map(tileId))
        val x = j * 65 + (i % 2) * 32
        batch.draw(t, x, i * 49)
        if (tileId == person) {
          batch.draw(personTexture, x, i * 50 + 35)
        }
      }
    }

    batch.end()
  }

  override def keyDown(keycode: Int): Boolean = {
    keycode match {
      case(Input.Keys.UP) if person + Width < Height * Width => person += Width
      case(Input.Keys.DOWN) if person - Width >= 0 => person -= Width
      case(Input.Keys.RIGHT) if person % Width != Width - 1 => person += 1
      case(Input.Keys.LEFT) if person % Width != 0 => person -= 1
      case _ =>
    }
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
