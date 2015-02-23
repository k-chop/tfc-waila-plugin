package com.github.whelmaze.tfcwailaplugin

import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTTagList, NBTTagCompound}
import net.minecraftforge.common.util.Constants.NBT

import implicits.RichIInventory

object NBTUtil {

  @inline final def buildTagList(inv: IInventory, start: Int, end: Int): NBTTagList = {
    val tagList = new NBTTagList()
    for (i <- start until end) {
      inv.ifNonEmptySlot(i) { is =>
        val tag1 = new NBTTagCompound()
        tag1.setByte("Slot", i.toByte)
        is.writeToNBT(tag1)
        tagList.appendTag(tag1)
      }
    }
    tagList
  }

  @inline final def readItemStacks(nbt: NBTTagCompound, tagListName: String = "Items"): Seq[ItemStack] = {
    val tag = nbt.getTagList(tagListName, NBT.TAG_COMPOUND)
    (0 until tag.tagCount) map { i =>
      val tagC = tag.getCompoundTagAt(i)
      ItemStack.loadItemStackFromNBT(tagC)
    }
  }

  @inline final def readItemStackInSlot(nbt: NBTTagCompound, tagListName: String = "Items", slot: Byte): Option[ItemStack] = {
    val tag = nbt.getTagList(tagListName, NBT.TAG_COMPOUND)
    (0 until tag.tagCount) find { i =>
      val tagC = tag.getCompoundTagAt(i)
      tagC.getByte("Slot") == slot
    } map { i =>
      val tagC = tag.getCompoundTagAt(i)
      ItemStack.loadItemStackFromNBT(tagC)
    }
  }

}
