package tfcwailaplugin

import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor, IWailaDataProvider}
import net.minecraft.item.ItemStack

import java.util.{List => JList}

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World


trait ProviderBase[T] extends IWailaDataProvider {

  def asTarget(te: TileEntity)(implicit ev: T <:< TileEntity): T = te.asInstanceOf[T]

  def getNBTData(te: TileEntity, tag: NBTTagCompound, world: World, x: Int, y: Int, z: Int): NBTTagCompound = {
    if (te != null)
      te.writeToNBT(tag)
    tag
  }

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
