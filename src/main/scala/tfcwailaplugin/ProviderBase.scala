package tfcwailaplugin

import java.util.List

import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor, IWailaDataProvider}
import net.minecraft.item.ItemStack

import java.util.{List => JList}


trait ProviderBase[T] extends IWailaDataProvider {

  def getWailaStack(accessor: IWailaDataAccessor, config: IWailaConfigHandler): ItemStack = null

  def getWailaHead(stack: ItemStack,
                            tooltip: JList[String],
                            accessor: IWailaDataAccessor,
                            config: IWailaConfigHandler): JList[String] = tooltip

  def getWailaBody(stack: ItemStack,
                            tooltip: JList[String],
                            accessor: IWailaDataAccessor,
                            config: IWailaConfigHandler): JList[String] = tooltip

  def getWailaTail(stack: ItemStack,
                            tooltip: JList[String],
                            accessor: IWailaDataAccessor,
                            config: IWailaConfigHandler): JList[String] = tooltip
}
