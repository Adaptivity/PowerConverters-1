package powercrystals.powerconverters;

import com.google.common.base.Throwables;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import powercrystals.powerconverters.common.BlockPowerConverterCommon;
import powercrystals.powerconverters.common.ItemBlockPowerConverterCommon;
import powercrystals.powerconverters.common.TileEntityCharger;
import powercrystals.powerconverters.common.TileEntityEnergyBridge;
import powercrystals.powerconverters.gui.PCGUIHandler;
import powercrystals.powerconverters.mods.BuildCraft;
import powercrystals.powerconverters.mods.IndustrialCraft;
import powercrystals.powerconverters.mods.EnderIO;
import powercrystals.powerconverters.mods.base.LoaderBase;
import powercrystals.powerconverters.power.PowerSystem;
import powercrystals.powerconverters.power.steam.BlockPowerConverterSteam;
import powercrystals.powerconverters.power.steam.ItemBlockPowerConverterSteam;
import powercrystals.powerconverters.power.steam.TileEntitySteamConsumer;
import powercrystals.powerconverters.power.steam.TileEntitySteamProducer;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

@Mod(modid = PowerConverterCore.modId, name = PowerConverterCore.modName, dependencies = "after:BuildCraft|Energy;after:IC2;after:Railcraft;after:EnderIO")

public final class PowerConverterCore 
{
    public static final String modId = "PowerConverters";
    public static final String modName = "Power Converters";
    public static String version = "1.2.2";

    static 
    {
        Properties prop = new Properties();

        try 
        {
            InputStream stream = PowerConverterCore.class.getClassLoader().getResourceAsStream("version.properties");
            prop.load(stream);
            stream.close();
        }
        catch (Exception e) 
        {
            //noinspection ThrowableResultOfMethodCallIgnored
            Throwables.propagate(e);
        }

        version = String.format("%s_build-%s", prop.getProperty("version"), prop.getProperty("build_number"));
    }

    public static final String guiFolder = modId + ":" + "textures/gui/";

    public static Block converterBlockCommon;
    public static Block converterBlockSteam;

    @Mod.Instance(modId)
    public static PowerConverterCore instance;

    public static int blockIdEnderIO;
    public static int blockIdIndustrialCraft;
    public static int blockIdBuildCraft;

    public static int bridgeBufferSize;
    public static int throttleSteamConsumer;
    public static int throttleSteamProducer;
    public static PowerSystem powerSystemSteam;
    public static boolean powerSystemSteamEnabled;

    private static int blockIdCommon;
    private static int blockIdSteam;
    private LoaderBase[] bases = new LoaderBase[] { BuildCraft.INSTANCE,IndustrialCraft.INSTANCE, EnderIO.INSTANCE };

    @EventHandler
    public void preInit(FMLPreInitializationEvent evt) 
    {
        evt.getModMetadata().version = PowerConverterCore.version;

        File dir = evt.getModConfigurationDirectory();
        loadConfig(dir);

        converterBlockCommon = new BlockPowerConverterCommon(blockIdCommon);
        GameRegistry.registerBlock(converterBlockCommon, ItemBlockPowerConverterCommon.class, converterBlockCommon.getUnlocalizedName());
        GameRegistry.registerTileEntity(TileEntityEnergyBridge.class, "powerConverterEnergyBridge");
        GameRegistry.registerTileEntity(TileEntityCharger.class, "powerConverterUniversalCharger");

        if(powerSystemSteamEnabled) 
        {
            converterBlockSteam = new BlockPowerConverterSteam(blockIdSteam);
            GameRegistry.registerBlock(converterBlockSteam, ItemBlockPowerConverterSteam.class, converterBlockSteam.getUnlocalizedName());
            GameRegistry.registerTileEntity(TileEntitySteamConsumer.class, "powerConverterSteamConsumer");
            GameRegistry.registerTileEntity(TileEntitySteamProducer.class, "powerConverterSteamProducer");
        }

        powerSystemSteam = new PowerSystem("Steam", "STEAM", 500, 500,/*875, 875,*/null, null, "mB/t");
        PowerSystem.registerPowerSystem(powerSystemSteam);
        for (LoaderBase base : bases)
            base.load(LoaderBase.Stage.PREINIT);
        }

    @EventHandler
    public void init(FMLInitializationEvent evt) throws Exception 
    {
    	for (LoaderBase base : bases)
    		base.load(LoaderBase.Stage.INIT);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent evt) throws Exception 
    {
    	for (LoaderBase base : bases)
    		base.load(LoaderBase.Stage.POSTINIT);

    	System.out.println("+++++++++++++++++++++++++[PowerConverters][NOTICE]+++++++++++++++++++++++++");
    	System.out.println("Default power ratios are based on FTB standards and not all mods follow these");
    	System.out.println("If you find conflicting ratios in your pack, please adjust the config acoordingly or remove PC");
    	System.out.println("These conflicts are not the fault of any mod author. It is the nature on minecraft");
    	System.out.println("-------------------------[PowerConverters][STEAM]-------------------------");
    	System.out.println("Default steam ratios, while based on standards, will create infinite energy loops");
    	System.out.println("To prevent over powered infinite energy, use a steam throttle values of less than 5");
    	System.out.println("+++++++++++++++++++++++++[PowerConverters][NOTICE]+++++++++++++++++++++++++");

        registerRecipes();
        
        if (powerSystemSteamEnabled) 
        {
            loadSteamConverters();
        }

	NetworkRegistry.INSTANCE.registerGuiHandler(instance, new PCGUIHandler());
	MinecraftForge.EVENT_BUS.register(instance);
	
		bases = null;
    }

    private void registerRecipes() 
    {
        ItemStack stackGold = new ItemStack(Items.gold_ingot);
        ItemStack stackRedstone = new ItemStack(Items.redstone);
        ItemStack stackIron = new ItemStack(Items.iron_ingot);
        ItemStack stackGlass = new ItemStack(Blocks.glass);
        ItemStack stackDiamond = new ItemStack(Items.diamond);

        Object entryGold = tryOreDict("ingotGold", stackGold);
        Object entryRedstone = tryOreDict("dustRedstone", stackRedstone);
        Object entryIron = tryOreDict("ingotIron", stackIron);
        Object entryGlass = tryOreDict("blockGlass", stackGlass);
        Object entryDiamond = tryOreDict("gemDiamond", stackDiamond);
        
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(converterBlockCommon, 1, 0), true, new Object[]
        {
                // Energy Bridge
                "GRG",
                "LDL",
                "GRG",
                'R', entryRedstone,
                'G', entryGold,
                'L', entryGlass,
                'D', entryDiamond
        }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(converterBlockCommon, 1, 2), true, new Object[]
        {
                // Universal charger
                "GRG",
                "ICI",
                "GRG",
                'R', entryRedstone,
                'G', entryGold,
                'I', entryIron,
                'C', Blocks.chest
        }));
    }

    public static Object tryOreDict(String name, ItemStack itemStack) 
    {
        for(ItemStack ore : OreDictionary.getOres(name)) 
        {
            if(ore.isItemEqual(itemStack)) 
            {
                return name;
            }
        }
        return itemStack;
    }

    private void loadSteamConverters() throws Exception 
    {
	    if (Loader.isModLoaded("Railcraft")) 
	    {
            ItemStack stackGold = new ItemStack(Items.gold_ingot);
            Object entryGold = tryOreDict("ingotGold", stackGold);

            ItemStack stackIndustrialEngine = GameRegistry.findItemStack("Railcraft", "machine.beta.engine.steam.low", 1);

            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(converterBlockSteam, 1, 0), true,
                    // Steam consumer
                    "G G",
                    " E ",
                    "G G",
                    'G', entryGold,
                    'E', stackIndustrialEngine));
	    }

	    GameRegistry.addShapelessRecipe(new ItemStack(converterBlockSteam, 1, 1), new ItemStack(converterBlockSteam, 1, 0));
	    GameRegistry.addShapelessRecipe(new ItemStack(converterBlockSteam, 1, 0), new ItemStack(converterBlockSteam, 1, 1));
    }

    private static void loadConfig(File dir) 
    {
    	dir = new File(new File(dir, modId.toLowerCase()), "common.cfg");
    	Configuration c = new Configuration(dir);

    	bridgeBufferSize = c.get(Configuration.CATEGORY_GENERAL, "BridgeBufferSize", 160000000).getInt();
    	throttleSteamConsumer = c.get("Throttles", "Steam.Consumer", 1000, "mB/t").getInt();
    	throttleSteamProducer = c.get("Throttles", "Steam.Producer", 1000, "mB/t (Sugested value for mod exploit handling = 1; this does not diminish steam return)").getInt();

    	PowerSystem.loadConfig(c);
    	c.save();
    }
}
