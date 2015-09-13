package com.github.whelmaze.tfcwailaplugin

import java.util.{List => JList}

import com.bioxx.tfc.Core.Metal.Alloy
import com.bioxx.tfc.TileEntities.TECrucible
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.minecraft.item.ItemStack

import scala.collection.JavaConversions._


object CrucibleProvider extends TileEntityProviderBase[TECrucible] {

  override def getWailaBody(stack: ItemStack, tooltip: JList[String], accessor: IWailaDataAccessor, config: IWailaConfigHandler): JList[String] = {
    import implicits.RichMetal
    import net.minecraft.util.EnumChatFormatting._

    accessor.getTileEntity match {
      case c: TECrucible =>
        Option(c.currentAlloy) foreach {
          case alloy: Alloy if alloy.alloyIngred.nonEmpty =>
            // Output
            val as = alloy.outputType.name
            val aa = math.round(alloy.outputAmount)
            tooltip add s"${util.translated.output}: $WHITE$UNDERLINE$as$RESET ($aa ${util.translated.units})"
            // MoltenAlloys
            alloy.alloyIngred.foreach { m =>
              val ms = m.metalType.localizedName
              tooltip add f"$ms: $GREEN${m.metal}%.2f%%"
            }
          case _ =>
        }
      case _ =>
    }
    tooltip
  }

}
