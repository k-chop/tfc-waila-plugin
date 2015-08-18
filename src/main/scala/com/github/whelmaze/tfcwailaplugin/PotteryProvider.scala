package com.github.whelmaze.tfcwailaplugin

import java.util.{List => JList}

import com.bioxx.tfc.Core.TFC_Time
import com.bioxx.tfc.Items.Pottery.ItemPotterySmallVessel
import com.bioxx.tfc.TileEntities.TEPottery
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.minecraft.item.ItemStack
import net.minecraftforge.common.util.ForgeDirection
import scala.language.postfixOps

import implicits.RichItemStack

object PotteryProvider extends TileEntityProviderBase[TEPottery] {

  override def getWailaStack(accessor: IWailaDataAccessor, config: IWailaConfigHandler): ItemStack = {
    Option(accessor.getTileEntity) collect {
      case e: TEPottery if e.straw < 8 && ForgeDirection.getOrientation(accessor.getPosition.sideHit) == ForgeDirection.UP =>
        val pos = accessor.getPosition

        val v = pos.hitVec
        val hitX = v.xCoord - v.xCoord.floor
        val hitZ = v.zCoord - v.zCoord.floor

        // https://github.com/Deadrik/TFCraft/blob/e4f01372ebf8a458705daa73faa6374d2164d38b/src/Common/com/bioxx/tfc/Blocks/Devices/BlockPottery.java#L117
        if (hitX < 0.5 && hitZ < 0.5)
          e.getStackInSlot(0)
        else if (hitX > 0.5 && hitZ < 0.5)
          e.getStackInSlot(1)
        else if (hitX < 0.5 && hitZ > 0.5)
          e.getStackInSlot(2)
        else if (hitX > 0.5 && hitZ > 0.5)
          e.getStackInSlot(3)
        else null
    } orNull
  }

  override def getWailaBody(stack: ItemStack,
                            tooltip: JList[String],
                            accessor: IWailaDataAccessor,
    config: IWailaConfigHandler): JList[String] = {
    import net.minecraft.util.EnumChatFormatting._

    if (stack != null) {
      val tag = stack.stackTagCompound

      stack.getItem match {
        // solid container
        case i: ItemPotterySmallVessel if stack.getItemDamage == 1 && tag != null && tag.hasKey("Items") =>
          import net.minecraft.util.EnumChatFormatting._

          for {
            bag <- Option(i loadBagInventory stack)
            is <- bag if is != null
          } tooltip.add(s"$GOLD${is.toInfoString}")

        // liquid container(molten metal)
        case i: ItemPotterySmallVessel if stack.getItemDamage == 2 && tag != null =>
          // https://github.com/Deadrik/TFCraft/blob/41eaf222c0310cef84a6fd5d9334e8d11a15263a/src/Common/com/bioxx/tfc/Items/Pottery/ItemPotterySmallVessel.java#L282
          if (tag.hasKey("MetalType") && tag.hasKey("MetalAmount")) {
            val s = tag.getString("MetalType")
            val a = s"$DARK_GREEN${util.translate("gui.metal." + s.replace(" ", ""))}"
            val b = s"(${tag.getInteger("MetalAmount")} ${util.translated.units})"
            tooltip.add(a + b)
          }
          if (tag.hasKey("TempTimer")) {
            val total = TFC_Time.getTotalHours
            val temp = tag.getLong("TempTimer")
            if (total - temp < 11)
              tooltip.add(s"$WHITE${util.translate("gui.ItemHeat.Liquid")}")
            else
              tooltip.add(s"$WHITE${util.translate("gui.ItemHeat.Solidified")}")
          }
        case _ =>
      }
    }
    tooltip
  }

}
