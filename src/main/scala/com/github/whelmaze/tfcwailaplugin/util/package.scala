package com.github.whelmaze.tfcwailaplugin

import net.minecraft.util.StatCollector.{translateToLocal => t}

package object util {
  type Coord = (Int, Int, Int)

  object translated {
    def units = t("gui.units")
    def output = t("gui.Output")
  }

  // alias to StatCollector.translateToLocal
  val translate: String => String = t
}
