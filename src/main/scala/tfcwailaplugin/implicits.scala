package tfcwailaplugin

import com.bioxx.tfc.TileEntities.TEBarrel
import net.minecraft.item.{ItemStack, Item}
import net.minecraftforge.fluids.FluidStack

object implicits {

  // Utilities for TEBarrel (scala friendly)
  implicit class TEBarrelAdapter(e: TEBarrel) extends AnyVal {

    def fluidStackOpt: Option[FluidStack] = Option(e.getFluidStack)

    def slotOpt(i: Int) = Option(e.getStackInSlot(i))

    def ifSlotAvailable[T](i: Int)(f: ItemStack => T): Unit = {
      val a = e.getStackInSlot(i)
      if (a != null)
        f(a)
    }
  }

}
