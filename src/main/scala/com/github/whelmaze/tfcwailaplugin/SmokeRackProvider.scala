package com.github.whelmaze.tfcwailaplugin

import java.util.{List => JList}

import com.bioxx.tfc.Core.{TFC_Time, TFC_Core}
import com.bioxx.tfc.TileEntities.{TEFirepit, TESmokeRack}
import com.bioxx.tfc.api.Food
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTTagList, NBTTagCompound}
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.MovingObjectPosition
import net.minecraft.world.World

import scala.language.postfixOps

import implicits.RichItemStack
import util.Coord

object SmokeRackProvider extends TileEntityProviderBase[TESmokeRack] {

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
        NBTUtil.readItemStackInSlot(accessor.getNBTData, slot = slot.toByte)
      case _ => None
    }
  }

  private[this] val DL_LEN = 10
  private[this] val dlx = Array( 0, 1,-1, 0, 0, 0, 1,-1, 0, 0)
  private[this] val dly = Array(-1,-1,-1,-1,-1,-2,-2,-2,-2,-2)
  private[this] val dlz = Array( 0, 0, 0, 1,-1, 0, 0,-0, 1,-1)

  // TEFirepit meta = 2(firepit has fuel and burning), meta = 1(firepit has no fuel but still burning)
  def isSmokingReady(te: TileEntity) = te.getBlockMetadata == 2

  private[this] def isSmoking(rack: TESmokeRack)(accessor: IWailaDataAccessor): Boolean = {
    def sfp(x: Int, y: Int, z: Int) = accessor.getWorld.getTileEntity(x, y, z)

    def isAvailableSmokingReadyFirepit(pos: MovingObjectPosition): Boolean = {
      @annotation.tailrec def recur(idx: Int): Boolean = idx match {
        case DL_LEN => false
        case i =>
          val te = sfp(pos.blockX + dlx(i), pos.blockY + dly(i), pos.blockZ + dlz(i))
          if (te.isInstanceOf[TEFirepit] && isSmokingReady(te))
            true
          else recur(i+1)
      }
      recur(0)
    }

    isAvailableSmokingReadyFirepit(accessor.getPosition)
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
      tooltip.set(0, s"$WHITE${util.translate("tile.SmokeRack.name")}")
    tooltip
  }

  def isDrying(dryTime: Int): Boolean = 0 <= dryTime && dryTime < Food.DRYHOURS

  override def getWailaBody(stack: ItemStack,
                            tooltip: JList[String],
                            accessor: IWailaDataAccessor,
                            config: IWailaConfigHandler): JList[String] = {
    import net.minecraft.util.EnumChatFormatting._

    val iso = getTargetStack(accessor)
    iso foreach { is =>
      tooltip add is.toInfoString
      //tooltip add s"smokeCounter: ${Food.getSmokeCounter(is)}"
      val rack = asTargetType(accessor.getTileEntity)

      if (!Food.isSmoked(is) && isSmoking(rack)(accessor)) {
        tooltip add s"${DARK_GRAY}Smoking..."
      } else if (!Food.isDried(is)) {
        val slot = assignSlotFromPosition(accessor)
        accessor.getNBTData.getIntArray("driedCounter").lift(slot) foreach { driedCounter =>
          val dryTime = (TFC_Time.getTotalHours - driedCounter).toInt
          if (isDrying(dryTime))
            tooltip add s"${DARK_GRAY}Drying..."
          else if (Food.DRYHOURS <= dryTime && driedCounter != 0) {
            tooltip add s"${GRAY}Well-Dried!"
          }
        }
      }
    }
    tooltip
  }

}
