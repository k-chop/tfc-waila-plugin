package com.github.whelmaze.tfcwailaplugin

import java.util.{List => JList}

import com.bioxx.tfc.Core.Player.SkillStats.SkillRank
import com.bioxx.tfc.Core.TFC_Core
import com.bioxx.tfc.Food.{ItemFoodTFC, CropIndexPepper, CropIndex, CropManager}
import com.bioxx.tfc.TileEntities.TECrop
import com.bioxx.tfc.api.Constant.Global
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.minecraft.item.ItemStack


object CropProvider extends ProviderBase[TECrop] with EphemeralCache[Int, String] {

  override def getWailaHead(stack: ItemStack,
                   tooltip: JList[String],
                   accessor: IWailaDataAccessor,
                   config: IWailaConfigHandler): JList[String] = {
    import net.minecraft.util.EnumChatFormatting._

    accessor.getTileEntity match {
      case tec: TECrop =>
        def updateF(ci: CropIndex, p: Boolean = false) = {
          val newIs = new ItemStack(if (p) ci.Output2 else ci.Output1)
          ItemFoodTFC.createTag(newIs)
          newIs.getDisplayName
        }

        val str = CropManager.getInstance.getCropFromId(tec.cropId) match {
          case pepper: CropIndexPepper if pepper.Output2 != null =>
            cache.getOrElseUpdate(tec.cropId, updateF(pepper, p = true))
          case others: CropIndex if others.Output1 != null =>
            cache.getOrElseUpdate(tec.cropId, updateF(others))
          case _ =>
            "Unknown"
        }
        if (!tooltip.isEmpty) {
          tooltip.set(0, s"${WHITE}Crop ($str)")
        }
      case _ =>
    }
    tooltip
  }

  override def getWailaBody(stack: ItemStack,
                   tooltip: JList[String],
                   accessor: IWailaDataAccessor,
                   config: IWailaConfigHandler): JList[String] = {
    import net.minecraft.util.EnumChatFormatting._
    import net.minecraft.util.StatCollector.translateToLocal
    import implicits.JavaEnumOrdering._

    // https://github.com/Deadrik/TFCraft/commit/055d3559bbfc257b0b4f6e0e8384631be661bf11
    val rank = TFC_Core.getSkillStats(accessor.getPlayer).getSkillRank(Global.SKILL_AGRICULTURE)
    if (SkillRank.Expert <= rank) {
      val nuType = accessor.getTileEntity match {
        case tec: TECrop =>
          CropManager.getInstance.getCropFromId(tec.cropId).getCycleType match {
            case 0 => s"$RED${translateToLocal("gui.Nutrient.A")}"
            case 1 => s"$GOLD${translateToLocal("gui.Nutrient.B")}"
            case 2 => s"$YELLOW${translateToLocal("gui.Nutrient.C")}"
            case _ => ""
          }
        case _ => ""
      }
      tooltip.add(nuType)
    }
    tooltip
  }

}
