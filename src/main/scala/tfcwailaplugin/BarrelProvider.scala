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

import implicits.{IInventoryAdapter, TEBarrelAdapter}

object BarrelProvider extends ProviderBase[TEBarrel] {

  private[this] def isValidFoodGroup: PartialFunction[Item, Boolean] = {
    case f: IFood => f.getFoodGroup match {
      case Fruit | Vegetable | Protein => true
      case _ => false
    }
    case _ => false
  }

  private[this] def stateString(b: TEBarrel): String = (for {
    is <- b.getSlotOpt(0)
    fs <- b.fluidStackOpt if b.getSealed
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
        // sealing
        if (e.getSealed) {
          val msg = stateString(e)
          tooltip.add(s"[Sealed${if (msg.nonEmpty) s" / $msg" else ""}]")
        }
        // solid container
        val itemCount = e.getInvCount
        if (1 <= itemCount) {
          e.storage.view.filter(_ != null).take(3).foreach { i =>
            tooltip.add(s"${i.getDisplayName} x${i.stackSize}")
          }
          if (3 < itemCount) tooltip.add(s"... ($itemCount items)")
        } else
          e.ifNonEmptySlot(0)(item => tooltip.add(s"${item.getDisplayName} x${item.stackSize}"))
        // fluid container
        e.fluidStackOpt.foreach { f =>
          tooltip.add(s"${f.getLocalizedName} : ${f.amount} mb")
        }
      case _ =>
    }
    tooltip
  }


}
