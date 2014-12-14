package tfcwailaplugin

import com.bioxx.tfc.TileEntities.TEBarrel
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

object implicits {

  implicit final class IInventoryAdapter(val inv: IInventory) extends AnyVal {

    private[this] def slot(i: Int) = inv.getStackInSlot(i)

    def getSlotOpt(i: Int) = Option(inv.getStackInSlot(i))

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

  implicit final class TEBarrelAdapter(val e: TEBarrel) extends AnyVal {

    @inline def fluidStackOpt: Option[FluidStack] = Option(e.getFluidStack)
  }

}
