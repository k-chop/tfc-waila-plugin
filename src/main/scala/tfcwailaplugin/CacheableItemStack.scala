package tfcwailaplugin

import net.minecraft.item.ItemStack

import scala.collection.mutable


trait CacheableItemStack {
  protected[this] val cache = mutable.WeakHashMap.empty[Int, ItemStack]
}
