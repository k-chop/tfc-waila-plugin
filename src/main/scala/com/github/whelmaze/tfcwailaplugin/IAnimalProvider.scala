package com.github.whelmaze.tfcwailaplugin

import java.util.{List => JList}

import com.bioxx.tfc.api.Entities.IAnimal
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaEntityAccessor}
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World

object IAnimalProvider extends EntityProviderBase[IAnimal] {

  override def getNBTData(player: EntityPlayerMP, ent: Entity, tag: NBTTagCompound, world: World): NBTTagCompound = {
    ent match {
      case a: IAnimal =>
        tag.setBoolean("Pregnant", a.isPregnant)
      case _ =>
    }
    tag
  }

  override def getWailaBody(entity: Entity, tooltip: JList[String], accessor: IWailaEntityAccessor, config: IWailaConfigHandler): JList[String] = {
    import net.minecraft.util.EnumChatFormatting._

    entity match {
      case a: IAnimal =>
        val nbt = accessor.getNBTData
        if (nbt.getBoolean("Pregnant"))
          tooltip.add(s"${LIGHT_PURPLE}Pregnant")
      case _ =>
    }
    tooltip
  }

}
