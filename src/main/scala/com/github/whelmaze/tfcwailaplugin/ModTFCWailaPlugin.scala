package com.github.whelmaze.tfcwailaplugin

import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event.{FMLInterModComms, FMLInitializationEvent}
import cpw.mods.fml.common.Mod

@Mod(name = "TFC Waila Plugin", modid = "tfcwailaplugin", version = "0.1.3", dependencies = "required-after:terrafirmacraft;required-after:Waila", modLanguage = "scala")
object ModTFCWailaPlugin {

  @EventHandler
  def init(event: FMLInitializationEvent): Unit = {
    FMLInterModComms.sendMessage("Waila", "register", "com.github.whelmaze.tfcwailaplugin.Providers.init")
  }

}
