package powercrystals.powerconverters.power.buildcraft;

import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.IPipeTile.PipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import powercrystals.powerconverters.mods.BuildCraft;
import powercrystals.powerconverters.position.BlockPosition;
import powercrystals.powerconverters.power.TileEntityEnergyConsumer;

import java.util.HashMap;
import java.util.Map;

public class TileEntityBuildCraftConsumer extends TileEntityEnergyConsumer<IPowerEmitter> implements IPowerReceptor, IPipeConnection 
{
    private PowerHandler _powerProvider;
    private int _mjLastTick = 0;
    private long _lastTickInjected;

    private Map<ForgeDirection, IPipeTile> _adjacentPipes;
    private Map<ForgeDirection, IPowerEmitter> _adjacentPowerSources;

    public TileEntityBuildCraftConsumer() 
    {
        super(BuildCraft.INSTANCE.powerSystem, 0, IPowerEmitter.class);
        _powerProvider = new PowerHandler(this, PowerHandler.Type.MACHINE);
        _powerProvider.configure(0.0D, 100.0D, 0.0D, 1000.0D); // 25 latency
    }

    @Override
    public void updateEntity() 
    {
        super.updateEntity();
        if (worldObj.getWorldTime() - _lastTickInjected > 1) 
        {
            _lastTickInjected = worldObj.getWorldTime();
            _mjLastTick = 0;
        }
        
        receiveEnergy();
    }

    public void receiveEnergy() 
    {
        if (_lastTickInjected != worldObj.getWorldTime()) 
        {
            _lastTickInjected = worldObj.getWorldTime();
            _mjLastTick = 0;
        }

        double consumed = _powerProvider.useEnergy(0.0D, _powerProvider.getEnergyStored(), false);
        double energyToUse = consumed * getPowerSystem().getInternalEnergyPerInput();
        double leftOvers = (float) storeEnergy(energyToUse, false);

        double finalConsumption = consumed - (leftOvers * getPowerSystem().getInternalEnergyPerInput());
        
        if (finalConsumption > 0.0D) 
        {
            _powerProvider.useEnergy(0.0D, finalConsumption, true);
            _mjLastTick = (int) finalConsumption;
        }
    }

    @Override
    public PowerReceiver getPowerReceiver(ForgeDirection dir) 
    {
        return _powerProvider.getPowerReceiver();
    }

    @Override
    public void doWork(PowerHandler handler)
    {
    }

    @Override
    public double getInputRate() 
    {
        return _mjLastTick;
    }

    @Override
    public World getWorld() 
    {
        return worldObj;
    }

    @Override
    public ConnectOverride overridePipeConnection(PipeType pipeType, ForgeDirection direction) 
    {
    	return pipeType == PipeType.POWER ? ConnectOverride.DEFAULT : ConnectOverride.DISCONNECT;
    }

    @Override
    public void onNeighboorChanged() 
    {
        super.onNeighboorChanged();

        _adjacentPipes = new HashMap<ForgeDirection, IPipeTile>();
        _adjacentPowerSources = new HashMap<ForgeDirection, IPowerEmitter>();

        for(ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) 
        {
            TileEntity te = BlockPosition.getAdjacentTileEntity(this, direction);
            if(te == null) 
            {
                continue;
            }
            
            if(IPipeTile.class.isAssignableFrom(te.getClass())) 
            {
                _adjacentPipes.put(direction, (IPipeTile)te);
            }
            
            else if(IPowerEmitter.class.isAssignableFrom(te.getClass())) 
            {
                _adjacentPowerSources.put(direction, (IPowerEmitter)te);
            }
        }
    }

    @Override
    public boolean isConnected() 
    {
        for(Map.Entry<ForgeDirection, IPipeTile> pipeEntry : _adjacentPipes.entrySet()) 
        {
            if(pipeEntry.getValue().isPipeConnected(pipeEntry.getKey().getOpposite())) 
            {
                return true;
            }
        }
        
        for(Map.Entry<ForgeDirection, IPowerEmitter> emitterEntry : _adjacentPowerSources.entrySet()) 
        {
            if(emitterEntry.getValue().canEmitPowerFrom(emitterEntry.getKey().getOpposite())) 
            {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public boolean isSideConnected(int side) 
    {
        ForgeDirection direction = ForgeDirection.getOrientation(side);
        IPipeTile pipe = _adjacentPipes.get(direction);
        
        if(pipe != null && pipe.isPipeConnected(direction)) 
        {
            return true;
        }
        IPowerEmitter emitter = _adjacentPowerSources.get(direction);
        return emitter != null && emitter.canEmitPowerFrom(direction);
    }

    @Override
    public boolean isSideConnectedClient(int side) 
    {
        TileEntity te = BlockPosition.getAdjacentTileEntity(this, ForgeDirection.getOrientation(side));
        return te != null && (IPipeTile.class.isAssignableFrom(te.getClass()) || IPowerEmitter.class.isAssignableFrom(te.getClass()));
    }

}
