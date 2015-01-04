package com.github.whelmaze.tfcwailaplugin

import java.util.{List => JList}

import com.bioxx.tfc.TileEntities.{TEFirepit, TESmokeRack}
import com.bioxx.tfc.api.Food
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.minecraft.item.ItemStack
import net.minecraft.util.{MovingObjectPosition, StatCollector}

import scala.language.postfixOps

import implicits.ItemStackAdapter
import util.Coord

object SmokeRackProvider extends ProviderBase[TESmokeRack] with EphemeralCache[Coord, Option[Coord]] {

  private[this] def assignSlotFromPosition(accessor: IWailaDataAccessor): Int = {
    // https://github.com/Deadrik/TFCraft/blob/d63ac3ae957cbc5664bb2bbd19e92d2712a12644/src/Common/com/bioxx/tfc/Blocks/BlockSmokeRack.java#L69

    val meta = accessor.getMetadata
    val v = accessor.getPosition.hitVec
    val hitX = v.xCoord - v.xCoord.floor
    val hitZ = v.zCoord - v.zCoord.floor

    val cc = if ((meta & 1) == 0) hitZ else hitX
    if (cc < 0.5) 0 else 1
  }

  // get ItemStack from player pointing
  private[this] def getTargetStack(accessor: IWailaDataAccessor): Option[ItemStack] = {
    accessor.getTileEntity match {
      case rack: TESmokeRack =>
        val slot = assignSlotFromPosition(accessor)
        Option(rack.getStackInSlot(slot))
      case _ => None
    }
  }

  private[this] final val dl = Seq((0,-1,0),(1,-1,0),(-1,-1,0),(0,-1,1),(0,-1,-1),(0,-2,0),(1,-2,0),(-1,-2,0),(0,-2,1),(0,-2,-1))

  private[this] def isSmoking(rack: TESmokeRack)(accessor: IWailaDataAccessor): Boolean = {
    def findFirepit(pos: MovingObjectPosition) = dl find { case (xx,yy,zz) => sfp(pos.blockX + xx, pos.blockY + yy, pos.blockZ + zz) }
    def sfp(x: Int, y: Int, z: Int): Boolean = accessor.getWorld.getTileEntity(x, y, z).isInstanceOf[TEFirepit]

    val pos = accessor.getPosition
    val c = (pos.blockX, pos.blockY, pos.blockZ)
    val pit: Option[Coord] = cache.getOrElseUpdate(c, findFirepit(pos))
    val light = pit exists { case (x, y, z) =>
      accessor.getWorld.getTileEntity(pos.blockX + x, pos.blockY + y, pos.blockZ + z) match {
        // meta = 1(firepit has fuel and burning) or meta = 2(firepit has no fuel but still burning)
        // judging from fuelLeft and fireTemp need to sync NBT
        case p: TEFirepit => p.getBlockMetadata == 1// || p.getBlockMetadata == 2
        case _ => false
      }
    }
    light// && TFC_Time.getTotalHours <= rack.lastSmokedTime + 1
  }

  override def getWailaStack(accessor: IWailaDataAccessor, config: IWailaConfigHandler): ItemStack = {
    getTargetStack(accessor) orNull
  }

  override def getWailaHead(stack: ItemStack,
                            tooltip: JList[String],
                            accessor: IWailaDataAccessor,
                            config: IWailaConfigHandler): JList[String] = {
    import net.minecraft.util.EnumChatFormatting._

    if (!tooltip.isEmpty)
      tooltip.set(0, s"$WHITE${StatCollector.translateToLocal("tile.SmokeRack.name")}")
    tooltip
  }

  private[this] def isDrying(is: ItemStack): Boolean = {
    val dryTime = Food.getDried(is)
    0 <= dryTime && dryTime < Food.DRYHOURS
  }

  override def getWailaBody(stack: ItemStack,
                            tooltip: JList[String],
                            accessor: IWailaDataAccessor,
                            config: IWailaConfigHandler): JList[String] = {
    import net.minecraft.util.EnumChatFormatting._

    val iso = getTargetStack(accessor)
    iso foreach { is =>
      tooltip add is.toInfoString
      val rack = asTarget(accessor.getTileEntity)
      if (isSmoking(rack)(accessor)) {
        tooltip add s"${DARK_GRAY}Smoking"
      } else if (isDrying(is)) {
        tooltip add s"${DARK_GRAY}Drying"
      }
    }
    tooltip
  }

}
