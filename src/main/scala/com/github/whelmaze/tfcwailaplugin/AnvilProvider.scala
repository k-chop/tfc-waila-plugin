package com.github.whelmaze.tfcwailaplugin

import java.util.{List => JList}

import com.bioxx.tfc.Blocks.Devices.BlockAnvil
import com.bioxx.tfc.Items.ItemBlocks.{ItemAnvil, ItemAnvil1, ItemAnvil2}
import com.bioxx.tfc.TFCBlocks
import com.bioxx.tfc.TileEntities.TEAnvil
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.minecraft.block.Block
import net.minecraft.item.ItemStack


object AnvilProvider extends ProviderBase[TEAnvil] {

  override def getWailaHead(stack: ItemStack,
                            tooltip: JList[String],
                            accessor: IWailaDataAccessor,
                            config: IWailaConfigHandler): JList[String] = {
    import net.minecraft.util.EnumChatFormatting._

    val t = BlockAnvil.getAnvilTypeFromMeta(accessor.getMetadata)
    Option(accessor.getStack).map(_.getItem).collect {
      case a: ItemAnvil => a.MetaNames(t)
    } foreach { name =>
      val tip = s"$WHITE$name Anvil"
      if (!tooltip.isEmpty) tooltip.set(0, tip)
    }

    tooltip
  }
}
