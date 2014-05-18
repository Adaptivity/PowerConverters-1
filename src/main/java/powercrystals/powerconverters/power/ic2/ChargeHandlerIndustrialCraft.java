package powercrystals.powerconverters.power.ic2;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import powercrystals.powerconverters.common.IChargeHandler;
import powercrystals.powerconverters.mods.IndustrialCraft;
import powercrystals.powerconverters.power.PowerSystem;

public class ChargeHandlerIndustrialCraft implements IChargeHandler 
{
    @Override
    public PowerSystem getPowerSystem() 
    {
        return IndustrialCraft.INSTANCE.powerSystem;
    }

    @Override
    public boolean canHandle(ItemStack stack) 
    {
        return !(stack == null || stack.getItem() instanceof IElectricItem);
        
    }

    @Override
    public int charge(ItemStack stack, int energyInput) 
    {
        int eu = (int) (energyInput / IndustrialCraft.INSTANCE.powerSystem.getInternalEnergyPerOutput());
        eu -= ElectricItem.manager.charge(stack, eu, ((IElectricItem) stack.getItem()).getTier(stack), false, false);
        return (int) (eu * IndustrialCraft.INSTANCE.powerSystem.getInternalEnergyPerOutput());
    }

    @Override
    public int discharge(ItemStack stack, int energyRequest) 
    {
        int eu = (int) (energyRequest / IndustrialCraft.INSTANCE.powerSystem.getInternalEnergyPerInput());
        eu = ElectricItem.manager.discharge(stack, eu, ((IElectricItem) stack.getItem()).getTier(stack), false, false);
        return (int) (eu * IndustrialCraft.INSTANCE.powerSystem.getInternalEnergyPerInput());
    }
}
