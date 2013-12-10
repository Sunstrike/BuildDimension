package io.sunstrike.quandim

import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.network.NetworkMod
import cpw.mods.fml.common.event.{FMLPostInitializationEvent, FMLInitializationEvent, FMLPreInitializationEvent}

import io.sunstrike.quandim.lib.Repo._
import io.sunstrike.quandim.lib.{Blocks, Config}
import net.minecraftforge.common.Configuration

/**
 * Main mod object
 *
 * @author Sunstrike <sun@sunstrike.io>
 */
@Mod(modid = modId, name = modName, version = modVersion, modLanguage = "scala")
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
object QuantumDimensions {

  @EventHandler
  def preInit(evt:FMLPreInitializationEvent) {
    logger.info(s"$modName ($modVersion) loading.")
    Config.loadConfig(new Configuration(evt.getSuggestedConfigurationFile))
    Blocks.setupBlocks()
  }

  @EventHandler
  def init(evt:FMLInitializationEvent) {

  }

  @EventHandler
  def postInit(evt:FMLPostInitializationEvent) {

  }

}
