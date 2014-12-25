package com.github.whelmaze.tfcwailaplugin

import scala.collection.mutable

trait EphemeralCache[A, B] {

  // These cached values are available only several tens of seconds, probably.
  protected[this] val cache = mutable.WeakHashMap.empty[A, B]
}
