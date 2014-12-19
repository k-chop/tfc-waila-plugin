package tfcwailaplugin

import com.bioxx.tfc.Core.TFCFluid
import com.bioxx.tfc.TileEntities.TEBarrel
import com.bioxx.tfc.api.Constant.Global
import com.bioxx.tfc.api.Food
import com.bioxx.tfc.api.Interfaces.IFood
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.StatCollector

import java.util.{List => JList}

import implicits._

object BarrelProvider extends ProviderBase[TEBarrel] {

  private[this] def isValidFoodGroup(item: Item): Boolean = {
    import com.bioxx.tfc.api.Enums.EnumFoodGroup._

    item match {
      case f: IFood => f.getFoodGroup match {
        case Fruit | Vegetable | Protein => true
        case _ => false
      }
      case _ => false
    }
  }

  private[this] def stateString(b: TEBarrel): String = (for {
    is <- b.getSlotOpt(0)
    fs <- b.fluidStackOpt if b.getSealed
  } yield {
    import net.minecraft.util.StatCollector.translateToLocal

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
      s"${translateToLocal("gui.barrel.brining")}"
    else if (isPickling)
      s"${translateToLocal("gui.barrel.pickling")}"
    else if (isPreserving)
      s"${translateToLocal("gui.barrel.preserving")}"
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
          e.storage.view.filter(_ != null).take(3).foreach { is =>
            tooltip.add(is.toInfoString)
          }
          if (3 < itemCount) tooltip.add(s"... ($itemCount items)")
        } else
          e.ifNonEmptySlot(0)(is => tooltip.add(is.toInfoString))
        // fluid container
        e.fluidStackOpt.foreach { f =>
          tooltip.add(s"${f.getLocalizedName} : ${f.amount} mb")
        }
      case _ =>
    }
    tooltip
  }


}
