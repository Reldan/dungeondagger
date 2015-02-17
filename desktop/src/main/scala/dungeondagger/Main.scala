package dungeondagger

import com.badlogic.gdx.backends.lwjgl.{LwjglApplication, LwjglApplicationConfiguration}
import dungeondagger.life.Life

object Main extends App {
  val cfg = new LwjglApplicationConfiguration
  cfg.title = "Dungeon dagger"
  cfg.height = 800
  cfg.width = 1200
  cfg.forceExit = true
  new LwjglApplication(new Life {
    override val AssetsPath = "android/assets/data"
  }, cfg)
}
