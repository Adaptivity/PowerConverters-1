package powercrystals.powerconverters.power.ic2;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import powercrystals.powerconverters.PowerConverterCore;
import powercrystals.powerconverters.gui.PCCreativeTab;
import powercrystals.powerconverters.power.BlockPowerConverter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPowerConverterIndustrialCraft extends BlockPowerConverter
{
    public BlockPowerConverterIndustrialCraft(int blockId)
    {
    	super(blockId, 10);
    	setBlockName("powerconverters.ic2");
    	setCreativeTab(PCCreativeTab.tab);
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) 
    {
    	if (metadata == 0)
    		return new TileEntityIndustrialCraftConsumer(0);
	
    	else if (metadata == 1)
    		return new TileEntityIndustrialCraftProducer(0);
	
    	else if (metadata == 2)
    		return new TileEntityIndustrialCraftConsumer(1);
	
    	else if (metadata == 3)
    		return new TileEntityIndustrialCraftProducer(1);
	
    	else if (metadata == 4)
    		return new TileEntityIndustrialCraftConsumer(2);
	
    	else if (metadata == 5)
    		return new TileEntityIndustrialCraftProducer(2);
	
    	else if (metadata == 6)
    		return new TileEntityIndustrialCraftConsumer(3);
	
    	else if (metadata == 7)
    		return new TileEntityIndustrialCraftProducer(3);
	
    	else if (metadata == 8)
    		return new TileEntityIndustrialCraftConsumer(4);
	
    	else if (metadata == 9)
    		return new TileEntityIndustrialCraftProducer(4);

    	return super.createNewTileEntity(world, metadata);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister ir) 
    {
      _icons[0] = ir.registerIcon("PowerConverters:" + getUnlocalizedName() + ".lv.consumer.off");
      _icons[1] = ir.registerIcon("PowerConverters:" + getUnlocalizedName() + ".lv.consumer.on");
      _icons[2] = ir.registerIcon("PowerConverters:" + getUnlocalizedName() + ".lv.producer.off");
      _icons[3] = ir.registerIcon("PowerConverters:" + getUnlocalizedName() + ".lv.producer.on");
      _icons[4] = ir.registerIcon("PowerConverters:" + getUnlocalizedName() + ".mv.consumer.off");
      _icons[5] = ir.registerIcon("PowerConverters:" + getUnlocalizedName() + ".mv.consumer.on");
      _icons[6] = ir.registerIcon("PowerConverters:" + getUnlocalizedName() + ".mv.producer.off");
      _icons[7] = ir.registerIcon("PowerConverters:" + getUnlocalizedName() + ".mv.producer.on");
      _icons[8] = ir.registerIcon("PowerConverters:" + getUnlocalizedName() + ".hv.consumer.off");
      _icons[9] = ir.registerIcon("PowerConverters:" + getUnlocalizedName() + ".hv.consumer.on");
      _icons[10] = ir.registerIcon("PowerConverters:" + getUnlocalizedName() + ".hv.producer.off");
      _icons[11] = ir.registerIcon("PowerConverters:" + getUnlocalizedName() + ".hv.producer.on");
      _icons[12] = ir.registerIcon("PowerConverters:" + getUnlocalizedName() + ".ev.consumer.off");
      _icons[13] = ir.registerIcon("PowerConverters:" + getUnlocalizedName() + ".ev.consumer.on");
      _icons[14] = ir.registerIcon("PowerConverters:" + getUnlocalizedName() + ".ev.producer.off");
      _icons[15] = ir.registerIcon("PowerConverters:" + getUnlocalizedName() + ".ev.producer.on");
      _icons[16] = ir.registerIcon("PowerConverters:" + getUnlocalizedName() + ".uv.consumer.off");
      _icons[17] = ir.registerIcon("PowerConverters:" + getUnlocalizedName() + ".uv.consumer.on");
      _icons[18] = ir.registerIcon("PowerConverters:" + getUnlocalizedName() + ".uv.producer.off");
      _icons[19] = ir.registerIcon("PowerConverters:" + getUnlocalizedName() + ".uv.producer.on");
    }
  }
