package com.github.whelmaze.tfcwailaplugin

import com.bioxx.tfc.Food.ItemFoodTFC
import com.bioxx.tfc.api.Util.Helper
import net.minecraft.inventory.IInventory
import net.minecraft.item.{Item, ItemStack}

object implicits {

  implicit final class IInventoryAdapter(val inv: IInventory) extends AnyVal {

    private[this] def slot(i: Int) = inv.getStackInSlot(i)

    // equal Option#foreach
    @inline def ifNonEmptySlot[T](i: Int)(f: ItemStack => T): Unit = {
      val is = slot(i)
      if (is != null) f(is)
    }

    @inline def getSlotOrElse(i: Int)(default: ItemStack): ItemStack = {
      val is = slot(i)
      if (is != null) default else is
    }

    @inline def foreachSlot[T](f: ItemStack => T): Unit = {
      var i = 0
      val size = inv.getSizeInventory
      while(i < size) {
        val is = slot(i)
        if (is != null) f(is)
        i += 1
      }
    }

  }

  implicit final class ItemStackAdapter(val is: ItemStack) extends AnyVal {
    import net.minecraft.util.EnumChatFormatting._

    @inline def toInfoString: String = is.getItem match {
      case i: ItemFoodTFC => i.toSimpleInfoString(is)
      case i: Item => s"${is.getDisplayName} x${is.stackSize}"
      case _ => s"$WHITE*** ${GOLD}Invalid ItemStack $WHITE***"
    }
  }

  implicit final class ItemFoodTFCAdapter(val food: ItemFoodTFC) extends AnyVal {

    // https://github.com/Deadrik/TFCraft/blob/9da2409d74b5de3b2e252528e582a6fd9241cd28/src/Common/com/bioxx/tfc/Items/Pottery/ItemPotterySmallVessel.java#L304
    // return "<name> <weight>oz <decay>%"
    @inline def toSimpleInfoString(is: ItemStack): String = {
      import net.minecraft.util.EnumChatFormatting._

      val decay = is.getTagCompound.getFloat("foodDecay")
      val weight = Helper.roundNumber(is.getTagCompound.getFloat("foodWeight"), 100)
      val decayStr = if (decay <= 0) "" else s" $DARK_GRAY${Helper.roundNumber(decay / weight * 100, 10)}%"
      s"$GOLD${is.getDisplayName} $WHITE${weight}oz $decayStr"
    }
  }

}