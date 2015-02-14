package com.github.whelmaze.tfcwailaplugin

import com.bioxx.tfc.Core.TFCFluid
import com.bioxx.tfc.TFCItems
import com.bioxx.tfc.TileEntities.TEBarrel
import com.bioxx.tfc.api.Constant.Global
import com.bioxx.tfc.api.Food
import com.bioxx.tfc.api.Interfaces.IFood
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.minecraft.item.{Item, ItemStack}

import java.util.{List => JList}

import implicits._
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

object BarrelProvider extends TileEntityProviderBase[TEBarrel] {

  private[this] def canBriningAndPickling(item: Item): Boolean = {
    import com.bioxx.tfc.api.Enums.EnumFoodGroup._

    item match {
      case f: IFood => f.getFoodGroup match {
        case Fruit | Vegetable | Protein => true
        case _ if f == TFCItems.Cheese => true
        case _ => false
      }
      case _ => false
    }
  }

  private[this] def stateString(b: TEBarrel): String = (for {
    is <- Option(b.getStackInSlot(0))
    fs <- Option(b.getFluidStack) if b.getSealed
  } yield {
    // https://github.com/Deadrik/TFCraft/blob/708511cdbb25d0b2c356eacc129334f8e81243a3/src/Common/com/bioxx/tfc/GUI/GuiBarrel.java#L315-343

    def isBrining =
      fs.getFluid == TFCFluid.BRINE && !Food.isBrined(is) && Option(b.recipe).nonEmpty && canBriningAndPickling(is.getItem)

    def isPickling =
      fs.getFluid == TFCFluid.VINEGAR && !Food.isPickled(is) && Food.isBrined(is) &&
        Food.getWeight(is) / fs.amount <= Global.FOOD_MAX_WEIGHT / b.getMaxLiquid &&
        Option(b.recipe).isEmpty && canBriningAndPickling(is.getItem)

    def isPreserving =
      fs.getFluid == TFCFluid.VINEGAR && Food.isPickled(is) &&
        Food.getWeight(is) / b.getFluidStack.amount <= Global.FOOD_MAX_WEIGHT/b.getMaxLiquid*2 &&
        Option(b.recipe).isEmpty

    if (isBrining)
      s"${util.translate("gui.barrel.brining")}"
    else if (isPickling)
      s"${util.translate("gui.barrel.pickling")}"
    else if (isPreserving)
      s"${util.translate("gui.barrel.preserving")}"
    else
      ""
  }).getOrElse("")

  override def getWailaHead(stack: ItemStack,
                            tooltip: JList[String],
                            accessor: IWailaDataAccessor,
                            config: IWailaConfigHandler): JList[String] = {
    import net.minecraft.util.EnumChatFormatting._

    accessor.getTileEntity match {
      case e: TEBarrel =>
        // sealed
        if (e.getSealed && !tooltip.isEmpty) {
          val msg = stateString(e)
          val barrelName = tooltip.get(0)
          tooltip.set(0, s"$WHITE$barrelName $DARK_AQUA[Sealed${if (msg.nonEmpty) s"/$DARK_GREEN$msg" else ""}$DARK_AQUA]")
        }
      case _ =>
    }

    tooltip
  }

  override def getWailaBody(stack: ItemStack,
                   tooltip: JList[String],
                   accessor: IWailaDataAccessor,
                   config: IWailaConfigHandler): JList[String] = {

    accessor.getTileEntity match {
      case e: TEBarrel =>
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
