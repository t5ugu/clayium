package mods.clayium.core;

import mods.clayium.block.ClayiumBlocks;
import mods.clayium.gui.GuiHandler;
import mods.clayium.item.ClayiumItems;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = ClayiumCore.ModId, version = ClayiumCore.Version, name = ClayiumCore.ModName)
public class ClayiumCore {
    public static final String ModId = "clayium";
    public static final String ModName = "Clayium";
    public static final String Version = "0.0.0";

    @Instance(ClayiumCore.ModId)
    public static ClayiumCore instance;

    public static ClayiumCore instance() {
        if (instance == null)
            instance = new ClayiumCore();
        return instance;
    }

    @SidedProxy(clientSide = "mods.clayium.core.ClayiumClientProxy", serverSide = "mods.clayium.core.ClayiumServerProxy")
    public static IClayiumProxy proxy;

    public static final SimpleNetworkWrapper packetHandler = NetworkRegistry.INSTANCE.newSimpleChannel("clayium:channel");

    public static Logger logger = LogManager.getLogger("clayium");

    public static final CreativeTabs tabClayium = new CreativeTabs("clayium") {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Items.CLAY_BALL);
        }
    };

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ClayiumBlocks.initBlocks();
        ClayiumItems.initItems();

        MinecraftForge.EVENT_BUS.register(this);
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);

        /* Register Smelt Recipes */
        GameRegistry.addSmelting(ClayiumItems.rawClayRollingPin, new ItemStack(ClayiumItems.clayRollingPin), 1F);
        GameRegistry.addSmelting(ClayiumItems.rawClaySlicer, new ItemStack(ClayiumItems.claySlicer), 1F);
        GameRegistry.addSmelting(ClayiumItems.rawClaySpatula, new ItemStack(ClayiumItems.claySpatula), 1F);

        proxy.registerTileEntities();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        ForgeChunkManager.setForcedChunkLoadingCallback(ClayiumCore.instance(), null);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        proxy.serverLoad(event);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(ClayiumBlocks.getBlocks().toArray(new Block[0]));
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(ClayiumBlocks.getItems().toArray(new Item[0]));
        event.getRegistry().registerAll(ClayiumItems.getItems().toArray(new Item[0]));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) {
        for (Item block : ClayiumBlocks.getItems()) {
            ModelLoader.setCustomModelResourceLocation(block, 0,
                    new ModelResourceLocation(block.getRegistryName(), "inventory"));
        }

        for (Item item : ClayiumItems.getItems()) {
            ModelLoader.setCustomModelResourceLocation(item, 0,
                    new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
    }

    static {
        FluidRegistry.enableUniversalBucket();
    }
}