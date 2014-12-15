package tfcwailaplugin

import cpw.mods.fml.common.FMLLog
import mcp.mobius.waila.api.IWailaRegistrar

import com.bioxx.tfc.TileEntities._

object Providers {

  def init(registrar: IWailaRegistrar): Unit = {
    FMLLog.info("CropProvider")
    registrar.registerStackProvider(CropProvider, classOf[TECrop])

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

    FMLLog.info("IngotPileProvider")
    registrar.registerStackProvider(IngotPileProvider, classOf[TEIngotPile])
    registrar.registerHeadProvider(IngotPileProvider, classOf[TEIngotPile])
    registrar.registerBodyProvider(IngotPileProvider, classOf[TEIngotPile])

  }

}
