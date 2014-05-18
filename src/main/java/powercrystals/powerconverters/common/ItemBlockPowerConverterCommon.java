package powercrystals.powerconverters.common;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemBlockPowerConverterCommon extends ItemBlock 
{
	public ItemBlockPowerConverterCommon(Block block) 
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
        if (md == 0) return "powerconverters.common.bridge";
        if (md == 2) return "powerconverters.common.charger";
        return "unknown";
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void getSubItems(Item item, CreativeTabs creativeTab, List subTypes) 
    {
        subTypes.add(new ItemStack(item, 1, 0));
        subTypes.add(new ItemStack(item, 1, 2));
    }
}
