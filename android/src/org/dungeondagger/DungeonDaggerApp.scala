package org.dungeondagger

import android.os.Bundle
import com.badlogic.gdx.backends.android.{AndroidApplication, AndroidApplicationConfiguration}
import dungeondagger.DungeonDagger

class DungeonDaggerApp extends AndroidApplication {
  override def onCreate(savedInstanceState:Bundle) {
    super.onCreate(savedInstanceState)

    val cfg = new AndroidApplicationConfiguration()

    initialize(new DungeonDagger, cfg)
  }
}
