package io.sunstrike.quandim.lib

import io.sunstrike.quandim.lib.Config._
import mantle.blocks.MantleBlock
import io.sunstrike.quandim.blocks.{BlockPortalController, BlockPortalFrame}
import cpw.mods.fml.common.registry.GameRegistry
import io.sunstrike.quandim.blocks.tiles.{TilePortalController, TilePortalFrame}

/**
 * Block storage
 *
 * @author Sunstrike <sun@sunstrike.io>
 */
object Blocks {

  def setupBlocks() {
    portalFrame = new BlockPortalFrame(portalFrameID)
    portalController = new BlockPortalController(portalControllerID)
    //portalStrongFrame = new BlockStrongFrame(portalStrongFrameID)
    //portalStrongController = new BlockStrongController(portalStrongControllerID)
    //portal = new BlockPortal(portalID)

    registerBlocks()
  }

  private def registerBlocks() {
    GameRegistry.registerBlock(portalFrame, "portalFrame")
    GameRegistry.registerTileEntity(classOf[TilePortalFrame], "portalFrame")
    GameRegistry.registerBlock(portalController, "portalController")
    GameRegistry.registerTileEntity(classOf[TilePortalController], "portalController")
  }

  var portalFrame:MantleBlock = null
  var portalController:MantleBlock = null
  var portalStrongFrame:MantleBlock = null
  var portalStrongController:MantleBlock = null
  var portal:MantleBlock = null

}
