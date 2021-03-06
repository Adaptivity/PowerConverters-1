package powercrystals.powerconverters.common;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercrystals.powerconverters.PowerConverterCore;
import powercrystals.powerconverters.gui.PCCreativeTab;
import powercrystals.powerconverters.position.INeighboorUpdateTile;
import powercrystals.powerconverters.power.TileEntityBridgeComponent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPowerConverterCommon extends BlockContainer
{
    private IIcon _iconBridge;
    private IIcon _iconChargerOn;
    private IIcon _iconChargerOff;

    public BlockPowerConverterCommon(int i)
    {
        super(Material.clay);
        setHardness(1.0F);
        setBlockName("powerconverters.common");
        setCreativeTab(PCCreativeTab.tab);
    }
    
    @Override
    public TileEntity createTileEntity(World world, int md) 
    {
    	if (md == 0)
    		return new TileEntityEnergyBridge();
	
    	else if (md == 2)
    		return new TileEntityCharger();
	
		return createNewTileEntity(world, md);
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister ir)
    {
        _iconBridge = ir.registerIcon("PowerConverters:" + getUnlocalizedName() + ".bridge");
        _iconChargerOn = ir.registerIcon("PowerConverters:" + getUnlocalizedName() + ".charger.on");
        _iconChargerOff = ir.registerIcon("PowerConverters:" + getUnlocalizedName() + ".charger.off");
    }

    @Override
    public IIcon getIcon(int side, int meta)
    {
    	if (meta == 0)
    		return _iconBridge;
	
    	else if (meta == 2)
    		return _iconChargerOff;

    	return null;
    }

    public IIcon getBlockTexture(IBlockAccess world, int x, int y, int z, int side)
    {
    	int meta = world.getBlockMetadata(x, y, z);
    	TileEntity te = world.getTileEntity(x, y, z);
    	
    	if (te instanceof TileEntityBridgeComponent<?>)
    	{
    		if (meta == 0)
    		{
    			return _iconBridge;
    		}
    		
    		else if (meta == 2)
    		{
    			boolean isConnected = ((TileEntityBridgeComponent<?>) te).isSideConnectedClient(side);
    			
    			if (isConnected)
    			{
    				return _iconChargerOn;
    			} 
    		
    		else
    		{
    			return _iconChargerOff;
    		}
	    }
	}

    	return getIcon(side, meta);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
    	TileEntity te = world.getTileEntity(x, y, z);
    	if (te != null && te instanceof INeighboorUpdateTile)
    	{
    		((INeighboorUpdateTile) te).onNeighboorChanged();
    	}
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
    	return null;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
    {
    	TileEntity te = world.getTileEntity(x, y, z);
    	
    	if (te != null && te instanceof TileEntityBridgeComponent<?>)
    	{
    		TileEntityEnergyBridge bridge = ((TileEntityBridgeComponent<?>) te).getFirstBridge();
    		if (bridge != null)
    		{
    			player.openGui(PowerConverterCore.instance, 0, world, bridge.xCoord, bridge.yCoord, bridge.zCoord);
    		}
    	}
    	
    	else if (te != null && te instanceof TileEntityEnergyBridge)
    	{
    		player.openGui(PowerConverterCore.instance, 0, world, x, y, z);
    	}
    	return true;
    }

    @Override
    public int damageDropped(int i)
    {
    	return i;
    }

    @Override
    public boolean canProvidePower()
    {
    	return true;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
    	int meta = world.getBlockMetadata(x, y, z);
    	
    	if (meta == 2)
    	{
    		float shrinkAmount = 0.125F;
    		return AxisAlignedBB.getAABBPool().getAABB(x, y, z, x + 1, y + 1 - shrinkAmount, z + 1);
    	}
    	
    	else
    	{
    		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    	}
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
    {
    	if (entity instanceof EntityPlayer && world.getBlockMetadata(x, y, z) == 2)
    	{
    		TileEntity te = world.getTileEntity(x, y, z);
    		if (te instanceof TileEntityCharger)
    		{
    			TileEntityCharger charger = (TileEntityCharger) te;
    			charger.setPlayer((EntityPlayer) entity);
    		}
    	}
    }
}
