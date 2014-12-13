package tfcwailaplugin

import com.bioxx.tfc.TileEntities.TEBarrel
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

object implicits {

  implicit final class IInventoryAdapter(val inv: IInventory) extends AnyVal {

    def getSlotOpt(i: Int) = Option(inv.getStackInSlot(i))

    // equal Option#foreach
    @inline def ifNonEmptySlot[T](i: Int)(f: ItemStack => T): Unit = {
      val is = inv.getStackInSlot(i)
      if (is != null) f(is)
    }

    @inline def getSlotOrElse(i: Int)(default: ItemStack): ItemStack = {
      val is = inv.getStackInSlot(i)
      if (is != null) default else is
    }

  }

  implicit final class TEBarrelAdapter(val e: TEBarrel) extends AnyVal {

    @inline def fluidStackOpt: Option[FluidStack] = Option(e.getFluidStack)
  }

}
