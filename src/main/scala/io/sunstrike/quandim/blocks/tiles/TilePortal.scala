package io.sunstrike.quandim.blocks.tiles

import net.minecraft.tileentity.TileEntity
import net.minecraft.entity.player.EntityPlayer
import mantle.lib.CoreRepo

class TilePortal extends TileEntity {

  var controller:TilePortalController = null

  def onEntityIntersection(player:EntityPlayer) {
    if (controller != null) controller.portalEnteredByPlayer(player) else CoreRepo.logger.warning("[TilePortal] Somehow handling intersect with no controller!")
  }

}
