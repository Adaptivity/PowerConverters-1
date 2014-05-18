package powercrystals.powerconverters.power.enderio;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemBlockPowerConverterRF extends ItemBlock 
{
	public ItemBlockPowerConverterRF(Block block) 
	{
		super(block);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public int getMetadata(int i) 
    {
        return i;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) 
    {
        int md = itemstack.getItemDamage();
        if (md == 0) return "powerconverters.te.consumer";
        if (md == 1) return "powerconverters.te.producer";
        return "unknown";
    }

    @SuppressWarnings
    (
    	{ 
    		"unchecked", 
    		"rawtypes" 
    	}
    )
    
    @Override
    public void getSubItems(Item item, CreativeTabs creativeTab, List subTypes) 
    {
        for (int i = 0; i <= 1; i++) 
        {
            subTypes.add(new ItemStack(item, 1, i));
        }
    }
}
