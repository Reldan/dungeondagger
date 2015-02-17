package dungeondagger.life

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.{GL20, Texture}
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.{Input, InputProcessor, Gdx, Screen}

class LifeScreen(val game: Life) extends Screen with InputProcessor {

  val stage: Stage = new Stage()
  val spriteBatch = new SpriteBatch()
  val appleTexture = new Texture(Gdx.files.internal(s"${game.AssetsPath}/hexagonTiles/apple.png"))
  val bananaTexture = new Texture(Gdx.files.internal(s"${game.AssetsPath}/hexagonTiles/banana.png"))

  Gdx.input.setInputProcessor(this)

  override def render(delta: Float): Unit = {
    Gdx.gl.glClearColor(1, 1, 1, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

//    if(cameraTimer>0){
//      val p = new Vector3(cameraNewPos).sub(cameraOldPos).scl(1 - cameraTimer / CAMERA_TIMER_MAX).add(cameraOldPos)
//      stage.getViewport.getCamera.position.set(p)
//      cameraTimer -= 1
//    }
    spriteBatch.begin()
    Range(0, game.world.height).foreach { i =>
      Range(0, game.world.width).foreach { j =>
        if (game.cell(j, i)) spriteBatch.draw(appleTexture, j * 10, i * 10, 10, 10)
        else spriteBatch.draw(bananaTexture, j * 10, i * 10, 10, 10)
      }

    }
    spriteBatch.end()

//    stage.act(Gdx.graphics.getDeltaTime)
//    stage.draw()
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
      game.world.step()
      print("u[")
    case (Input.Keys.R) =>
      game.world.rand()
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