package io.sunstrike.quandim.blocks.tiles

import net.minecraft.tileentity.TileEntity
import mantle.blocks.iface.{IMasterLogic, IServantLogic}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import mantle.world.CoordTuple
import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.relauncher.Side
import mantle.debug.{DebugData, IDebuggable}

/**
 * Portal frame TE
 *
 * @author Sunstrike <sun@sunstrike.io>
 */
class TilePortalFrame extends TileEntity with IServantLogic with IDebuggable {

  private var ticks = 0
  private var master:IMasterLogic = null
  private var masterLocation:CoordTuple = null

  override def updateEntity() {
    ticks += 1

    // Reset tick counter and verify master
    if (ticks > 100) {
      if (FMLCommonHandler.instance().getEffectiveSide == Side.SERVER && masterLocation != null && !verifyMaster(master, worldObj, masterLocation.x, masterLocation.y, masterLocation.z))
        invalidateMaster(master, worldObj, masterLocation.x, masterLocation.y, masterLocation.z)

      ticks = 0
    }
  }

  // IServantLogic
  def getMasterPosition: CoordTuple = masterLocation

  def notifyMasterOfChange() {
    if (master != null)
      master.notifyChange(this, xCoord, yCoord, zCoord)
  }

  def setPotentialMaster(master: IMasterLogic, world: World, xMaster: Int, yMaster: Int, zMaster: Int): Boolean = {
    if (world.getBlockTileEntity(xMaster, yMaster, zMaster).isInstanceOf[TilePortalController]) {
      true
    } else false
  }

  def verifyMaster(master: IMasterLogic, world: World, xMaster: Int, yMaster: Int, zMaster: Int): Boolean = {
    if (world.getBlockTileEntity(xMaster, yMaster, zMaster).isInstanceOf[TilePortalController]) {
      this.master = master
      masterLocation = new CoordTuple(xMaster, yMaster, zMaster)
      true
    } else false
  }

  def invalidateMaster(master: IMasterLogic, world: World, xMaster: Int, yMaster: Int, zMaster: Int) {
    this.master = null
    masterLocation = null
  }

  // IDebuggable
  def getDebugInfo(player: EntityPlayer): DebugData = {
    val strs = Array(
      s"ticks: $ticks, master: $master",
      s"masterLoc: $masterLocation"
    )
    new DebugData(player, getClass, strs)
  }
}
