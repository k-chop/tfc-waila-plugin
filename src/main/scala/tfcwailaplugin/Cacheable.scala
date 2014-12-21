package tfcwailaplugin

import net.minecraft.item.ItemStack

import scala.collection.mutable


trait Cacheable[A, B] {
  protected[this] val cache = mutable.WeakHashMap.empty[A, B]
}
