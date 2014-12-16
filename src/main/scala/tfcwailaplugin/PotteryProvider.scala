package tfcwailaplugin

import java.util.{List => JList}

import com.bioxx.tfc.Core.TFC_Time
import com.bioxx.tfc.Food.ItemFoodTFC
import com.bioxx.tfc.Items.Pottery.ItemPotterySmallVessel
import com.bioxx.tfc.TileEntities.TEPottery
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.minecraft.item.ItemStack
import net.minecraft.util.{StatCollector, EnumChatFormatting}
import net.minecraftforge.common.util.ForgeDirection

import implicits.ItemFoodTFCAdapter
import implicits.ItemStackAdapter

object PotteryProvider extends ProviderBase[TEPottery] {

  override def getWailaStack(accessor: IWailaDataAccessor, config: IWailaConfigHandler): ItemStack = {
    accessor.getTileEntity match {
      case e: TEPottery =>
        val pos = accessor.getPosition

        if (e.straw < 8) {
          if (ForgeDirection.getOrientation(pos.sideHit) == ForgeDirection.UP) {
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
          } else null
        } else null
      case _ => null // null null null null...
    }
  }

  override def getWailaBody(stack: ItemStack,
                            tooltip: JList[String],
                            accessor: IWailaDataAccessor,
    config: IWailaConfigHandler): JList[String] = {
    def T(str: String) = StatCollector.translateToLocal(str)

    if (stack != null) {
      val tag = stack.stackTagCompound

      stack.getItem match {
        // solid container
        case i: ItemPotterySmallVessel if stack.getItemDamage == 1 & tag != null =>
          if (tag.hasKey("Items")) {
            val bag = i.loadBagInventory(stack)
            if (bag != null) {
              bag.filter(_ != null).foreach { is =>
                val str = is.getItem match {
                  case food: ItemFoodTFC =>
                    food.toSimpleInfoString(is)
                  case _ =>
                    is.toInfoString
                }
                tooltip.add(str)
              }
            }
          }
        // liquid container(molten metal)
        case i: ItemPotterySmallVessel if stack.getItemDamage == 2 && tag != null =>
          // https://github.com/Deadrik/TFCraft/blob/41eaf222c0310cef84a6fd5d9334e8d11a15263a/src/Common/com/bioxx/tfc/Items/Pottery/ItemPotterySmallVessel.java#L282
          if (tag.hasKey("MetalType")) {
            val s = tag.getString("MetalType")
            val a = s"${EnumChatFormatting.DARK_GREEN}${StatCollector.translateToLocal("gui.metal." + s.replace(" ", ""))}"
            val b = if (tag.hasKey("MetalAmount")) {
              val amount = tag.getInteger("MetalAmount")
              s" ($amount Unit)"
            } else ""
            tooltip.add(a + b)
          }
          if (tag.hasKey("TempTimer")) {
            val total = TFC_Time.getTotalHours
            val temp = tag.getLong("TempTimer")
            if (total - temp < 11)
              tooltip.add(s"${EnumChatFormatting.WHITE}${StatCollector.translateToLocal("gui.ItemHeat.Liquid")}")
            else
              tooltip.add(s"${EnumChatFormatting.WHITE}${StatCollector.translateToLocal("gui.ItemHeat.Solidified")}")
          }
        case _ =>
      }
    }
    tooltip
  }

}
