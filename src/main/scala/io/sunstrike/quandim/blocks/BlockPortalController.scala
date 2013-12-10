package io.sunstrike.quandim.blocks

import mantle.blocks.MantleBlock
import net.minecraft.block.material.Material
import net.minecraft.world.World
import net.minecraft.tileentity.TileEntity
import io.sunstrike.quandim.blocks.tiles.{TilePortalController, TilePortalFrame}
import net.minecraft.creativetab.CreativeTabs

/**
  * Portal frame block
  *
  * @author Sunstrike <sun@sunstrike.io>
  */
class BlockPortalController(id:Int) extends MantleBlock(id, Material.circuits) {

  setCreativeTab(CreativeTabs.tabRedstone)
  setHardness(0.5f)
  setResistance(0.5f)
  setUnlocalizedName("portal.controller")

  override def hasTileEntity(meta: Int) = true

  override def createTileEntity(world: World, meta: Int): TileEntity = new TilePortalController

 }
