package tfcwailaplugin

import cpw.mods.fml.common.FMLLog
import mcp.mobius.waila.api.IWailaRegistrar

import com.bioxx.tfc.TileEntities._

object Providers {

  def init(registrar: IWailaRegistrar): Unit = {

    registrar.addConfig("TFCWailaPlugin", "tfcwailaplugin.numberoflog", "Show number of log", false)
    registrar.addConfig("TFCWailaPlugin", "tfcwailaplugin.logperslot", "Show log per slot", false)
    registrar.addConfig("TFCWailaPlugin", "tfcwailaplugin.nologinfo", "No log pile Information", false)

    FMLLog.info("CropProvider")
    registrar.registerHeadProvider(CropProvider, classOf[TECrop])
    registrar.registerBodyProvider(CropProvider, classOf[TECrop])

    FMLLog.info("WorldItemProvider")
    registrar.registerStackProvider(WorldItemProvider, classOf[TEWorldItem])

    FMLLog.info("BarrelProvider")
    registrar.registerBodyProvider(BarrelProvider, classOf[TEBarrel])

    FMLLog.info("OreProvider")
    registrar.registerStackProvider(OreProvider, classOf[TEOre])

    FMLLog.info("PotteryProvider")
    registrar.registerStackProvider(PotteryProvider, classOf[TEPottery])
    registrar.registerBodyProvider(PotteryProvider, classOf[TEPottery])

    FMLLog.info("ToolRackProvider")
    registrar.registerStackProvider(ToolRackProvider, classOf[TileEntityToolRack])

    FMLLog.info("LogPileProvider")
    registrar.registerBodyProvider(LogPileProvider, classOf[TELogPile])
    registrar.registerNBTProvider(LogPileProvider, classOf[TELogPile])

    FMLLog.info("IngotPileProvider")
    registrar.registerStackProvider(IngotPileProvider, classOf[TEIngotPile])
    registrar.registerHeadProvider(IngotPileProvider, classOf[TEIngotPile])
    registrar.registerBodyProvider(IngotPileProvider, classOf[TEIngotPile])

    FMLLog.info("FoodPrepProvider")
    registrar.registerBodyProvider(FoodPrepProvider, classOf[TEFoodPrep])
    registrar.registerNBTProvider(FoodPrepProvider, classOf[TEFoodPrep])

  }

}
