package tfcwailaplugin

import java.util.{List => JList}

import codechicken.lib.raytracer.RayTracer
import com.bioxx.tfc.Items.Pottery.{ItemPotterySmallVessel, ItemPotteryBase}
import com.bioxx.tfc.TileEntities.TEPottery
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.minecraft.item.ItemStack


object PotteryProvider extends ProviderBase[TEPottery] {

  override def getWailaStack(accessor: IWailaDataAccessor, config: IWailaConfigHandler): ItemStack = {
    accessor.getTileEntity match {
      case e: TEPottery =>
        Option(accessor.getPosition.hitVec).map { v =>
          val hitX = v.xCoord - v.xCoord.floor
          val hitZ = v.zCoord - v.zCoord.floor

          // https://github.com/Deadrik/TFCraft/blob/e4f01372ebf8a458705daa73faa6374d2164d38b/src/Common/com/bioxx/tfc/Blocks/Devices/BlockPottery.java#L117
          if (hitX < 0.5 && hitZ < 0.5)
            e.getStackInSlot(0)
          else if (hitX > 0.5 && hitZ < 0.5)
            e.getStackInSlot(1)
          else if (hitX < 0.5 && hitZ > 0.5)
            e.getStackInSlot(2)
          else if (hitX > 0.5 && hitZ > 0.5)
            e.getStackInSlot(3)
          else null
        }.orNull
      case _ => null
    }
  }

  override def getWailaBody(stack: ItemStack,
                            tooltip: JList[String],
                            accessor: IWailaDataAccessor,
                            config: IWailaConfigHandler): JList[String] = {
    if (stack != null) {

      stack.getItem match {
        case i: ItemPotterySmallVessel =>
          val bag = i.loadBagInventory(stack)
          if (bag != null)
            bag.filter(_ != null).foreach { is =>
              tooltip.add(s"${is.getDisplayName} x${is.stackSize}")
            }
        case _ =>
      }
    }
    tooltip
  }

}
