package com.github.whelmaze.tfcwailaplugin

import cpw.mods.fml.common.FMLLog
import mcp.mobius.waila.api.IWailaRegistrar

import com.bioxx.tfc.TileEntities._

object Providers {

  val modName = "TFCWailaPlugin"
  val keyNamePrefix = "tfcwailaplugin"

  private[this] sealed trait Target
  private[this] case object Stack extends Target
  private[this] case object Head extends Target
  private[this] case object Body extends Target
  private[this] case object Tail extends Target
  private[this] case object NBT extends Target

  private[this] def configure(registrar: IWailaRegistrar) = {
    def add(keyName: String, description: String, default: Boolean) = registrar.addConfig(modName, s"$keyNamePrefix.$keyName", description, default)

    // configs
    add("numberoflog", "Show number of log", default = false)
    add("logperslot", "Show log per slot", default = false)
    add("nologinfo", "No log pile Info", default = false)
  }

  private[this] def provide(provider: ProviderBase[_], clazz: Class[_], targets: Target*)(implicit registrar: IWailaRegistrar): Unit = {
    targets.distinct.foreach {
      case Stack => registrar.registerStackProvider(provider, clazz)
      case Head => registrar.registerHeadProvider(provider, clazz)
      case Body => registrar.registerBodyProvider(provider, clazz)
      case Tail => registrar.registerTailProvider(provider, clazz)
      case NBT => registrar.registerNBTProvider(provider, clazz)
    }
  }

  def init(implicit registrar: IWailaRegistrar): Unit = {

    configure(registrar)

    // registration providers
    provide(CropProvider, classOf[TECrop], targets = Head, Body)

    provide(WorldItemProvider, classOf[TEWorldItem], targets = Stack)

    provide(BarrelProvider, classOf[TEBarrel], targets = Head, Body)

    provide(OreProvider, classOf[TEOre], targets = Stack)

    provide(PotteryProvider, classOf[TEPottery], targets = Stack, Body)

    provide(ToolRackProvider, classOf[TileEntityToolRack], targets = Stack)

    provide(LogPileProvider, classOf[TELogPile], targets = Body, NBT)

    provide(IngotPileProvider, classOf[TEIngotPile], targets = Stack, Head, Body)

    provide(FoodPrepProvider, classOf[TEFoodPrep], targets = Body, NBT)

    provide(AnvilProvider, classOf[TEAnvil], targets = Head)

    provide(SmokeRackProvider, classOf[TESmokeRack], targets = Stack, Head, Body)

  }

}
