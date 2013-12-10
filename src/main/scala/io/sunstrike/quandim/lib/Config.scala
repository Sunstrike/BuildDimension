package io.sunstrike.quandim.lib

import net.minecraftforge.common.Configuration

import io.sunstrike.quandim.lib.Repo._

/**
 * Mod configuration handler
 *
 * @author Sunstrike <sun@sunstrike.io>
 */
object Config {

  def loadConfig(conf:Configuration) {
    logger.info("Loading/creating config file: " + conf)
    conf.load()

    portalFrameID = conf.getBlock("PortalFrame", portalFrameID).getInt
    portalStrongFrameID = conf.getBlock("StrongPortalFrame", portalStrongFrameID).getInt
    portalID = conf.getBlock("Portal", portalID).getInt

    conf.save()
  }

  // Block IDs
  var portalFrameID = 2000
  var portalControllerID = 2001
  var portalStrongFrameID = 2002
  var portalStrongControllerID = 2003
  var portalID = 2004

}
