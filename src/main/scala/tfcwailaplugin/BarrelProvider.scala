package tfcwailaplugin

import com.bioxx.tfc.Core.TFCFluid
import com.bioxx.tfc.TileEntities.TEBarrel
import com.bioxx.tfc.api.Constant.Global
import com.bioxx.tfc.api.Food
import com.bioxx.tfc.api.Interfaces.IFood
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.minecraft.item.{Item, ItemStack}

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
    is <- Option(b.getStackInSlot(0))
    fs <- Option(b.getFluidStack) if b.getSealed
  } yield {
    import net.minecraft.util.StatCollector.translateToLocal

    def isBrining =
      fs.getFluid == TFCFluid.BRINE && !Food.isBrined(is) && Option(b.recipe).nonEmpty && isValidFoodGroup(is.getItem)

    def isPickling =
      fs.getFluid == TFCFluid.VINEGAR && !Food.isPickled(is) && Food.isBrined(is) &&
        Food.getWeight(is) / fs.amount <= Global.FOOD_MAX_WEIGHT / b.getMaxLiquid &&
        Option(b.recipe).isEmpty && isValidFoodGroup(is.getItem)

    def isPreserving =
      fs.getFluid == TFCFluid.VINEGAR && Food.isPickled(is) &&
        Food.getWeight(is) / b.getFluidStack.amount <= Global.FOOD_MAX_WEIGHT/b.getMaxLiquid*2 &&
        Option(b.recipe).isEmpty

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
          e.storage.view.filter(_ != null).take(3).foreach {
            tooltip add _.toInfoString
          }
          if (3 < itemCount) tooltip.add(s"... ($itemCount items)")
        } else
          e.ifNonEmptySlot(0)(tooltip add _.toInfoString)
        // fluid container
        Option(e.getFluidStack).foreach { f =>
          tooltip.add(s"${f.getLocalizedName} : ${f.amount} mb")
        }
      case _ =>
    }
    tooltip
  }


}
