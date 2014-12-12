package tfcwailaplugin

import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event.{FMLInterModComms, FMLPostInitializationEvent, FMLInitializationEvent}
import cpw.mods.fml.common.{FMLLog, Mod}

@Mod(name = "TFC Waila Plugin", modid = "tfcwailaplugin", version = "0.0.1", dependencies = "required-after:terrafirmacraft;required-after:Waila", modLanguage = "scala")
object ModTFCWailaPlugin {

  @EventHandler
  def init(event: FMLInitializationEvent): Unit = {
    FMLInterModComms.sendMessage("Waila", "register", "tfcwailaplugin.Providers.init")
  }

}
