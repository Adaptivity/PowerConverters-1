package powercrystals.powerconverters.power;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import powercrystals.powerconverters.PowerConverterCore;
import powercrystals.powerconverters.common.TileEntityEnergyBridge;
import powercrystals.powerconverters.position.INeighboorUpdateTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPowerConverter extends BlockContainer
{
	protected IIcon[] _icons;

    public BlockPowerConverter(int blockId, int metaCount)
    {
    	super(Material.iron);
    	setHardness(2.0F);
    	_icons = new IIcon[metaCount * 2];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
    	int offset = ((TileEntityBridgeComponent<?>) world.getTileEntity(x, y, z)).isSideConnectedClient(side) ? 1 : 0;
    	return _icons[world.getBlockMetadata(x, y, z) * 2 + offset];
    }

    @Override
    public IIcon getIcon(int side, int metadata)
    {
    	return _icons[metadata * 2];
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2)
    {
    	return null;
    }

    @Override
    public boolean canProvidePower()
    {
    	return true;
    }

    @Override
    public int damageDropped(int par1)
    {
    	return par1;
    }


    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
    	TileEntity te = world.getTileEntity(x, y, z);
    	if (te != null && te instanceof INeighboorUpdateTile)
    	{
    		((INeighboorUpdateTile) te).onNeighboorChanged();
    		world.markBlockForUpdate(x, y, z);
    	}
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
    	
    	return true;
    }
}
