package com.github.whelmaze.tfcwailaplugin

import mcp.mobius.waila.api.IWailaRegistrar

import com.bioxx.tfc.TileEntities._

object Providers {

  private[this] sealed trait Target
  private[this] case object Stack extends Target
  private[this] case object Head extends Target
  private[this] case object Body extends Target
  private[this] case object Tail extends Target
  private[this] case object NBT extends Target

  private[this] def provide[T](provider: ProviderBase[T], targets: Target*)(implicit registrar: IWailaRegistrar): Unit = {
    targets.distinct.foreach {
      case Stack => registrar.registerStackProvider(provider, provider.targetClass)
      case Head => registrar.registerHeadProvider(provider, provider.targetClass)
      case Body => registrar.registerBodyProvider(provider, provider.targetClass)
      case Tail => registrar.registerTailProvider(provider, provider.targetClass)
      case NBT => registrar.registerNBTProvider(provider, provider.targetClass)
    }
  }

  def init(implicit registrar: IWailaRegistrar): Unit = {

    Configs.registerAll(registrar)

    // registration providers
    provide(CropProvider, targets = Head, Body)

    provide(WorldItemProvider, targets = Stack)

    provide(BarrelProvider, targets = Head, Body)

    provide(OreProvider, targets = Stack)

    provide(PotteryProvider, targets = Stack, Body)

    provide(ToolRackProvider, targets = Stack)

    provide(LogPileProvider, targets = Body, NBT)

    provide(IngotPileProvider, targets = Stack, Head, Body)

    provide(FoodPrepProvider, targets = Body, NBT)

    provide(SmokeRackProvider, targets = Stack, Head, Body)

    provide(CrucibleProvider, targets = Body)

  }

}
