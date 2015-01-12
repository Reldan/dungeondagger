package dungeondagger

import com.badlogic.gdx.backends.lwjgl.{LwjglApplication, LwjglApplicationConfiguration}

object Main extends App {
  val cfg = new LwjglApplicationConfiguration
  cfg.title = "Dungeon dagger"
  cfg.height = 480
  cfg.width = 800
  cfg.forceExit = false
  new LwjglApplication(new DungeonDagger, cfg)
}
