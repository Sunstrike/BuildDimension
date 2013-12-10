package io.sunstrike.quandim.blocks

import mantle.blocks.MantleBlock
import net.minecraft.block.material.Material
import net.minecraft.world.World
import net.minecraft.tileentity.TileEntity
import io.sunstrike.quandim.blocks.tiles.{TilePortalFrame, TilePortalController}
import net.minecraft.creativetab.CreativeTabs

/**
 * Portal frame block
 *
 * @author Sunstrike <sun@sunstrike.io>
 */
class BlockPortalFrame(id:Int) extends MantleBlock(id, Material.circuits) {

  setCreativeTab(CreativeTabs.tabRedstone)
  setHardness(0.5f)
  setResistance(0.5f)
  setUnlocalizedName("portal.frame")

  override def hasTileEntity(meta: Int) = true

  override def createTileEntity(world: World, meta: Int): TileEntity = new TilePortalFrame

}
