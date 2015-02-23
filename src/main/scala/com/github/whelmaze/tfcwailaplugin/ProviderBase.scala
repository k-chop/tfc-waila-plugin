package com.github.whelmaze.tfcwailaplugin

import mcp.mobius.waila.api._
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack

import java.util.{List => JList}

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

import scala.reflect.ClassTag

abstract class ProviderBase[A: ClassTag, B] {

  def targetClass: Class[A] = implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]]

  def asTargetType(target: B)(implicit ev: A <:< B): A = target.asInstanceOf[A]

}

abstract class TileEntityProviderBase[T: ClassTag] extends ProviderBase[T, TileEntity] with IWailaDataProvider {

  def getNBTData(player: EntityPlayerMP, te: TileEntity, tag: NBTTagCompound, world: World, x: Int, y: Int, z: Int): NBTTagCompound = {
    if (te != null)
      te.writeToNBT(tag)
    tag
  }

  def getWailaStack(accessor: IWailaDataAccessor, config: IWailaConfigHandler): ItemStack = null

  def getWailaHead(stack: ItemStack, tooltip: JList[String], accessor: IWailaDataAccessor, config: IWailaConfigHandler): JList[String] = tooltip

  def getWailaBody(stack: ItemStack, tooltip: JList[String], accessor: IWailaDataAccessor, config: IWailaConfigHandler): JList[String] = tooltip

  def getWailaTail(stack: ItemStack, tooltip: JList[String], accessor: IWailaDataAccessor, config: IWailaConfigHandler): JList[String] = tooltip
}

abstract class EntityProviderBase[T: ClassTag] extends ProviderBase[T, Entity] with IWailaEntityProvider {

  def getNBTData(player: EntityPlayerMP, ent: Entity, tag: NBTTagCompound, world: World): NBTTagCompound = {
    if (ent != null)
      ent.writeToNBT(tag)
    tag
  }

  def getWailaOverride(accessor: IWailaEntityAccessor, config: IWailaConfigHandler): Entity = null

  def getWailaHead(entity: Entity, tooltip: JList[String], accessor: IWailaEntityAccessor, config: IWailaConfigHandler): JList[String] = tooltip

  def getWailaBody(entity: Entity, tooltip: JList[String], accessor: IWailaEntityAccessor, config: IWailaConfigHandler): JList[String] = tooltip

  def getWailaTail(entity: Entity, tooltip: JList[String], accessor: IWailaEntityAccessor, config: IWailaConfigHandler): JList[String] = tooltip

}