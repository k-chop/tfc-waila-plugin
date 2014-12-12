package tfcwailaplugin

import com.bioxx.tfc.Core.TFCFluid
import com.bioxx.tfc.TileEntities.TEBarrel
import com.bioxx.tfc.api.Constant.Global
import com.bioxx.tfc.api.Food
import com.bioxx.tfc.api.Interfaces.IFood
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.minecraft.item.{Item, ItemStack}
import com.bioxx.tfc.api.Enums.EnumFoodGroup.{Fruit, Vegetable, Protein}
import net.minecraft.util.StatCollector

import java.util.{List => JList}


object BarrelProvider extends ProviderBase[TEBarrel] {

  private[BarrelProvider] sealed trait Cond
  private[BarrelProvider] case object Brining extends Cond
  private[BarrelProvider] case object Pickling extends Cond
  private[BarrelProvider] case object Preserving extends Cond
  private[BarrelProvider] case object Normal extends Cond

  private[this] def isValidFoodGroup: PartialFunction[Item, Boolean] = {
    case f: IFood => f.getFoodGroup match {
      case Fruit | Vegetable | Protein => true
      case _ => false
    }
    case _ => false
  }

  private[this] def state(b: TEBarrel): Cond = (for {
    is <- Option(b.getStackInSlot(0))
    fs <- Option(b.getFluidStack) if b.getSealed
  } yield {
    if (b.recipe != null && fs.getFluid == TFCFluid.BRINE && !Food.isBrined(is) && isValidFoodGroup(is.getItem) )
      Brining
    else if (b.recipe == null && !Food.isPickled(is) && Food.isBrined(is) &&
             Food.getWeight(is) / fs.amount <= Global.FOOD_MAX_WEIGHT / b.getMaxLiquid &&
             fs.getFluid == TFCFluid.VINEGAR && isValidFoodGroup(is.getItem))
      Pickling
    else if (b.recipe == null && Food.isPickled(is) && fs.getFluid == TFCFluid.VINEGAR &&
             Food.getWeight(is) / b.getFluidStack.amount <= Global.FOOD_MAX_WEIGHT/b.getMaxLiquid*2)
      Preserving
    else
      Normal
  }).getOrElse(Normal)

  override def getWailaBody(stack: ItemStack,
                   tooltip: JList[String],
                   accessor: IWailaDataAccessor,
                   config: IWailaConfigHandler): JList[String] = {
    accessor.getTileEntity match {
      case e: TEBarrel =>
        if (e.getSealed) {
          val msg = state(e) match {
            case Brining => s"${StatCollector.translateToLocal("gui.barrel.brining")}"
            case Pickling => s"${StatCollector.translateToLocal("gui.barrel.pickling")}"
            case Preserving => s"${StatCollector.translateToLocal("gui.barrel.preserving")}"
            case _ => ""
          }
          tooltip.add(s"[Sealed${if (msg.nonEmpty) s" / $msg" else ""}]")
        }
        Option(e.getFluidStack).foreach { f =>
          val item = e.getStackInSlot(0)
          if (item != null)
            tooltip.add(s"${item.getDisplayName} x ${item.stackSize}")
          tooltip.add(s"${f.getLocalizedName} : ${f.amount} mb")
        }
      case _ =>
    }
    tooltip
  }


}
