package dungeondagger

import com.badlogic.gdx.backends.lwjgl.{LwjglApplication, LwjglApplicationConfiguration}

object Main extends App {
  val cfg = new LwjglApplicationConfiguration
  cfg.title = "Dungeon dagger"
  cfg.height = 800
  cfg.width = 1200
  cfg.forceExit = true
  new LwjglApplication(new DungeonDagger, cfg)
}
