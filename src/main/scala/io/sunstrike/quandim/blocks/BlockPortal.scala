package io.sunstrike.quandim.blocks

import mantle.blocks.MantleBlock
import net.minecraft.world.World
import net.minecraft.tileentity.TileEntity
import io.sunstrike.quandim.blocks.tiles.TilePortal
import net.minecraft.block.material.Material
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.AxisAlignedBB
import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.relauncher.Side

class BlockPortal(id:Int) extends MantleBlock(id, Material.portal) {

  setBlockUnbreakable()
  setUnlocalizedName("portal.portal")

  override def hasTileEntity(meta: Int) = true

  override def createTileEntity(world: World, meta: Int): TileEntity = new TilePortal

  override def getCollisionBoundingBoxFromPool(par1World: World, par2: Int, par3: Int, par4: Int): AxisAlignedBB = null

  override def onEntityCollidedWithBlock(world: World, x: Int, y: Int, z: Int, entity: Entity) {
    if (FMLCommonHandler.instance().getEffectiveSide != Side.SERVER) return
    val te = world.getBlockTileEntity(x, y, z)
    if (te.isInstanceOf[TilePortal] && entity.isInstanceOf[EntityPlayer])
      te.asInstanceOf[TilePortal].onEntityIntersection(entity.asInstanceOf[EntityPlayer])
  }

}
