package io.sunstrike.quandim.blocks.tiles

import net.minecraft.tileentity.TileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.common.ForgeDirection

import mantle.blocks.iface.{IMasterLogic, IServantLogic}
import mantle.world.{DirectionUtils, CoordTuple}
import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.relauncher.Side

import io.sunstrike.quandim.lib.Repo.logger
import mantle.debug.{DebugData, IDebuggable}

/**
 * Portal frame TE
 *
 * @author Sunstrike <sun@sunstrike.io>
 */
class TilePortalController extends TileEntity with IDebuggable with IMasterLogic {

  private var ticks = 0
  private var portalActive = false
  private var firstCorner:CoordTuple = null
  private var secondCorner:CoordTuple = null

  override def updateEntity() {
    ticks += 1

    // Reset tick counter and check for structure
    if (ticks > 100) {
      if (!portalActive && FMLCommonHandler.instance().getEffectiveSide == Side.SERVER) attemptStructureBuild()
      ticks = 0
    }
  }

  def attemptStructureBuild() {
    // Check for frames immediately around the controller
    var blocks:Array[(TilePortalFrame, ForgeDirection)] = Array()
    for (i <- ForgeDirection.VALID_DIRECTIONS) {
      val x = xCoord + i.offsetX
      val y = yCoord + i.offsetY
      val z = zCoord + i.offsetZ
      val te = worldObj.getBlockTileEntity(x, y, z)
      if (te.isInstanceOf[TilePortalFrame])
        blocks ++= Array((te.asInstanceOf[TilePortalFrame], i)) // I don't even...
    }

    if (blocks.size != 2) return
  }

  def createOrDestroyPortal(create:Boolean) {

  }

  def portalEnteredByPlayer(player:EntityPlayer) {

  }

  // IMasterLogic
  def notifyChange(servant: IServantLogic, x: Int, y: Int, z: Int) {

  }

  // IDebuggable
  def getDebugInfo(player: EntityPlayer): DebugData = {
    val strs = Array(s"ticks: $ticks")
    new DebugData(player, getClass, strs)
  }
}
