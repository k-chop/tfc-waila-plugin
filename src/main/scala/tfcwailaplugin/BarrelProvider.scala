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

  private[this] def isValidFoodGroup: PartialFunction[Item, Boolean] = {
    case f: IFood => f.getFoodGroup match {
      case Fruit | Vegetable | Protein => true
      case _ => false
    }
    case _ => false
  }

  private[this] def stateString(b: TEBarrel): String = (for {
    is <- Option(b.getStackInSlot(0))
    fs <- Option(b.getFluidStack) if b.getSealed
  } yield {
    def isBrining =
      b.recipe != null && fs.getFluid == TFCFluid.BRINE && !Food.isBrined(is) && isValidFoodGroup(is.getItem)

    def isPickling =
      b.recipe == null && !Food.isPickled(is) && Food.isBrined(is) &&
        Food.getWeight(is) / fs.amount <= Global.FOOD_MAX_WEIGHT / b.getMaxLiquid &&
        fs.getFluid == TFCFluid.VINEGAR && isValidFoodGroup(is.getItem)

    def isPreserving =
      b.recipe == null && Food.isPickled(is) && fs.getFluid == TFCFluid.VINEGAR &&
        Food.getWeight(is) / b.getFluidStack.amount <= Global.FOOD_MAX_WEIGHT/b.getMaxLiquid*2

    if (isBrining)
      s"${StatCollector.translateToLocal("gui.barrel.brining")}"
    else if (isPickling)
      s"${StatCollector.translateToLocal("gui.barrel.pickling")}"
    else if (isPreserving)
      s"${StatCollector.translateToLocal("gui.barrel.preserving")}"
    else
      ""
  }).getOrElse("")

  override def getWailaBody(stack: ItemStack,
                   tooltip: JList[String],
                   accessor: IWailaDataAccessor,
                   config: IWailaConfigHandler): JList[String] = {
    accessor.getTileEntity match {
      case e: TEBarrel =>
        if (e.getSealed) {
          val msg = stateString(e)
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
