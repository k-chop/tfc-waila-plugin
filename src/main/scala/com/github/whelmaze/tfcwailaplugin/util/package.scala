package com.github.whelmaze.tfcwailaplugin

import net.minecraft.util.StatCollector.{translateToLocal => t}

package object util {
  type Coord = (Int, Int, Int)

  object translated {
    val units = t("gui.units")
  }

  // alias to StatCollector.translateToLocal
  val translate: String => String = t
}
