package io.sunstrike.quandim.blocks.tiles

import net.minecraft.tileentity.TileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.common.ForgeDirection

import mantle.blocks.iface.{IMasterLogic, IServantLogic}
import mantle.world.{DirectionUtils, CoordTuple}
import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.relauncher.Side

import io.sunstrike.quandim.lib.Repo._
import io.sunstrike.quandim.lib.Blocks._
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

  private var portalBlocksToUpdate:List[CoordTuple] = null
  private var updatePortalController = false

  override def updateEntity() {
    if (updatePortalController) {
      for (bl <- portalBlocksToUpdate) {
        val te = worldObj.getBlockTileEntity(bl.x, bl.y, bl.z)
        if (te.isInstanceOf[TilePortal])
          te.asInstanceOf[TilePortal].controller = this
      }

      updatePortalController = false
    }

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
        blocks :+= (te.asInstanceOf[TilePortalFrame], i)
    }

    if (blocks.size != 2) return

    if (DirectionUtils.isRightAngles(blocks(0)._2, blocks(1)._2)) {
      attemptBuildFromCorner(blocks)
    } else if (blocks(0)._2.getOpposite == blocks(1)._2) {
      attemptBuildInFrame(blocks)
    }
  }

  // Used if controller is (probably) in a corner of the frame (this is the nice and simple case)
  private def attemptBuildFromCorner(bls:Array[(TilePortalFrame, ForgeDirection)]) {
    var horDir, verDir:ForgeDirection = null
    if (DirectionUtils.isHorizontal(bls(0)._2)) {
      horDir = bls(0)._2
      verDir = bls(1)._2
    } else {
      horDir = bls(1)._2
      verDir = bls(0)._2
    }

    val horDiff = checkRowTileLength(horDir)
    val verDiff = checkRowTileLength(verDir)
    logger.info(s"[Corner Build] horDiff: $horDiff, verDiff: $verDiff")
    if (horDiff < 2 || verDiff < 3) return // Too small...

    // Get all other frame blocks for verification
    var frames:List[CoordTuple] = List()
    for (horOff <- 1 to horDiff; verOff <- 1 to verDiff) {
      if (verOff == verDiff)
        frames :+= new CoordTuple(horDir.offsetX * horOff + xCoord, verDir.offsetY * verOff + yCoord, horDir.offsetZ * horOff + zCoord)
      else if (horOff == horDiff)
        frames :+= new CoordTuple(horDir.offsetX * horOff + xCoord, verDir.offsetY * verOff + yCoord, horDir.offsetZ * horOff + zCoord)
    }

    for (coord <- frames) {
      if (!worldObj.getBlockTileEntity(coord.x, coord.y, coord.z).isInstanceOf[TilePortalFrame]) return
    }

    for (h <- 1 to horDiff)
      frames :+= new CoordTuple(horDir.offsetX * h + xCoord, yCoord, horDir.offsetZ * h + zCoord)

    for (v <- 1 to verDiff)
      frames :+= new CoordTuple(xCoord, verDir.offsetY * v + yCoord, zCoord)

    for (bl <- frames)
      if (!worldObj.getBlockTileEntity(bl.x, bl.y, bl.z).asInstanceOf[IServantLogic].verifyMaster(this, worldObj, xCoord, yCoord, zCoord)) return

    logger.info("Valid structure!")
    firstCorner = new CoordTuple(xCoord + horDir.offsetX, yCoord + verDir.offsetY, zCoord + horDir.offsetZ)
    secondCorner = new CoordTuple(xCoord + horDir.offsetX * (horDiff - 1), yCoord + verDir.offsetY * (verDiff - 1), zCoord + horDir.offsetZ * (horDiff - 1))

    createOrDestroyPortal(true)
  }

  // Helper for detecting rows
  private def checkRowTileLength(d:ForgeDirection):Int = {
    var ret = 0
    for (diff <- 1 to 9) {
      if (worldObj.getBlockTileEntity(xCoord + diff * d.offsetX, yCoord + diff * d.offsetY, zCoord + diff * d.offsetZ).isInstanceOf[TilePortalFrame])
        ret = diff
      else return ret
    }

    ret
  }

  // Used if the controller is in a side of the frame
  private def attemptBuildInFrame(bls:Array[(TilePortalFrame, ForgeDirection)]) {

  }

  // Create/Remove portal blocks from the frame using the 2 corners (false destroys the portal)
  private def createOrDestroyPortal(create:Boolean) {
    val diffX = secondCorner.x - firstCorner.x
    val diffY = secondCorner.y - firstCorner.y
    val diffZ = secondCorner.z - firstCorner.z

    val signX = if (diffX < 0) -1 else 1
    val signY = if (diffY < 0) -1 else 1
    val signZ = if (diffZ < 0) -1 else 1

    var blocks:List[CoordTuple] = List()
    for (dx <- 0 to diffX by signX; dy <- 0 to diffY by signY; dz <- 0 to diffZ by signZ) {
      blocks :+= new CoordTuple(firstCorner.x + dx, firstCorner.y + dy, firstCorner.z + dz)
    }

    if (!create) {
      // Destroy all the things
      for (bl <- blocks) {
        worldObj.destroyBlock(bl.x, bl.y, bl.z, false)
      }
      portalActive = false
      portalBlocksToUpdate = null

      return
    }

    // Fail if blocks in portal space, remove old portal blocks
    for (bl <- blocks) {
      if (worldObj.getBlockId(bl.x, bl.y, bl.z) == portal.blockID)
        worldObj.destroyBlock(bl.x, bl.y, bl.z, false)
      if (!worldObj.isAirBlock(bl.x, bl.y, bl.z)) {
        logger.info(s"Apparently $bl is NOT an air block!")
        return
      }
    }

    logger.info("[Portal] All air.")

    for (bl <- blocks) {
      worldObj.setBlock(bl.x, bl.y, bl.z, portal.blockID)
      logger.info(s"[Portal] Setting portal at $bl")
    }

    portalActive = true
    updatePortalController = true
    portalBlocksToUpdate = blocks
  }

  // Fired by a player intersecting a portal block
  def portalEnteredByPlayer(player:EntityPlayer) {
    logger.info("Intersected with " + this)
  }

  // IMasterLogic
  def notifyChange(servant: IServantLogic, x: Int, y: Int, z: Int) {
    if (!servant.isInstanceOf[TilePortalFrame]) return // This should never happen...
    val fr = servant.asInstanceOf[TilePortalFrame]

    if (fr.isInvalid && portalActive) {
      createOrDestroyPortal(false)
    }
  }

  override def invalidate() {
    if (portalActive) createOrDestroyPortal(false)
  }

  override def onChunkUnload() {
    if (portalActive) createOrDestroyPortal(false)
  }

  // IDebuggable
  def getDebugInfo(player: EntityPlayer): DebugData = {
    val strs = Array(s"ticks: $ticks")
    new DebugData(player, getClass, strs)
  }
}
