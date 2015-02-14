package com.github.whelmaze.tfcwailaplugin

import mcp.mobius.waila.api.IWailaRegistrar

object Providers {

  def init(implicit registrar: IWailaRegistrar): Unit = {

    Configs.registerAll(registrar)

    // registration providers
    CropProvider.register(targets = Head, Body)

    WorldItemProvider.register(targets = Stack)

    BarrelProvider.register(targets = Head, Body)

    OreProvider.register(targets = Stack)

    PotteryProvider.register(targets = Stack, Body)

    ToolRackProvider.register(targets = Stack)

    LogPileProvider.register(targets = Body, NBT)

    IngotPileProvider.register(targets = Stack, Head, Body)

    FoodPrepProvider.register(targets = Body, NBT)

    SmokeRackProvider.register(targets = Stack, Head, Body)

    CrucibleProvider.register(targets = Body)

  }

  // TE = TileEntity
  private[this] sealed trait TargetTE
  // E = Entity
  private[this] sealed trait TargetE
  private[this] case object Stack extends TargetTE
  private[this] case object Head extends TargetTE with TargetE
  private[this] case object Body extends TargetTE with TargetE
  private[this] case object Tail extends TargetTE with TargetE
  private[this] case object NBT extends TargetTE with TargetE
  private[this] case object OverrideEntity extends TargetE

  private[this] final implicit class TE[T](provider: TileEntityProviderBase[T]) extends AnyVal {
    def register(targets: TargetTE*)(implicit registrar: IWailaRegistrar): Unit = {
      targets.distinct.foreach {
        case Stack => registrar.registerStackProvider(provider, provider.targetClass)
        case Head => registrar.registerHeadProvider(provider, provider.targetClass)
        case Body => registrar.registerBodyProvider(provider, provider.targetClass)
        case Tail => registrar.registerTailProvider(provider, provider.targetClass)
        case NBT => registrar.registerNBTProvider(provider, provider.targetClass)
      }
      logger.info(s"Registered TileEntity provider ${provider.getClass} for ${provider.targetClass}")
    }
  }

  private[this] final implicit class E[T](provider: EntityProviderBase[T]) extends AnyVal {
    def register(targets: TargetE*)(implicit registrar: IWailaRegistrar): Unit = {
      targets.distinct.foreach {
        case OverrideEntity => registrar.registerOverrideEntityProvider(provider, provider.targetClass)
        case Head => registrar.registerHeadProvider(provider, provider.targetClass)
        case Body => registrar.registerBodyProvider(provider, provider.targetClass)
        case Tail => registrar.registerTailProvider(provider, provider.targetClass)
        case NBT => registrar.registerNBTProvider(provider, provider.targetClass)
      }
      logger.info(s"Registered Entity provider ${provider.getClass} for ${provider.targetClass}")
    }
  }
}
