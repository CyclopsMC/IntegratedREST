package org.cyclops.integratedrest;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import org.apache.logging.log4j.Level;
import org.cyclops.cyclopscore.config.ConfigHandler;
import org.cyclops.cyclopscore.init.ModBaseVersionable;
import org.cyclops.cyclopscore.init.RecipeHandler;
import org.cyclops.cyclopscore.proxy.ICommonProxy;
import org.cyclops.integratedrest.api.http.request.IRequestHandlerRegistry;
import org.cyclops.integratedrest.api.json.IValueTypeJsonHandlerRegistry;
import org.cyclops.integratedrest.http.HttpServer;
import org.cyclops.integratedrest.http.request.RequestHandlerRegistry;
import org.cyclops.integratedrest.http.request.RequestHandlers;
import org.cyclops.integratedrest.json.ValueTypeJsonHandlerRegistry;
import org.cyclops.integratedrest.json.ValueTypeJsonHandlers;

/**
 * The main mod class of this mod.
 * @author rubensworks (aka kroeserr)
 *
 */
@Mod(
        modid = Reference.MOD_ID,
        name = Reference.MOD_NAME,
        useMetadata = true,
        version = Reference.MOD_VERSION,
        dependencies = Reference.MOD_DEPENDENCIES,
        guiFactory = "org.cyclops.integratedrest.GuiConfigOverview$ExtendedConfigGuiFactory",
        certificateFingerprint = Reference.MOD_FINGERPRINT
)
public class IntegratedRest extends ModBaseVersionable {
    
    /**
     * The proxy of this mod, depending on 'side' a different proxy will be inside this field.
     * @see SidedProxy
     */
    @SidedProxy(clientSide = "org.cyclops.integratedrest.proxy.ClientProxy", serverSide = "org.cyclops.integratedrest.proxy.CommonProxy")
    public static ICommonProxy proxy;
    
    /**
     * The unique instance of this mod.
     */
    @Instance(value = Reference.MOD_ID)
    public static IntegratedRest _instance;

    protected final HttpServer server;

    public IntegratedRest() {
        super(Reference.MOD_ID, Reference.MOD_NAME, Reference.MOD_VERSION);
        server = new HttpServer();
    }

    @Override
    protected RecipeHandler constructRecipeHandler() {
        return new RecipeHandler(this);
    }

    /**
     * The pre-initialization, will register required configs.
     * @param event The Forge event required for this.
     */
    @EventHandler
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        // Registries
        getRegistryManager().addRegistry(IRequestHandlerRegistry.class, RequestHandlerRegistry.getInstance());
        getRegistryManager().addRegistry(IValueTypeJsonHandlerRegistry.class, ValueTypeJsonHandlerRegistry.getInstance());

        RequestHandlers.load();
        ValueTypeJsonHandlers.load();

        super.preInit(event);
    }
    
    /**
     * Register the config dependent things like world generation and proxy handlers.
     * @param event The Forge event required for this.
     */
    @EventHandler
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }
    
    /**
     * Register the event hooks.
     * @param event The Forge event required for this.
     */
    @EventHandler
    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }
    
    /**
     * Register the things that are related to server starting, like commands.
     * @param event The Forge event required for this.
     */
    @EventHandler
    @Override
    public void onServerStarting(FMLServerStartingEvent event) {
        super.onServerStarting(event);
    }

    /**
     * Register the things that are related to server starting.
     * @param event The Forge event required for this.
     */
    @EventHandler
    @Override
    public void onServerStarted(FMLServerStartedEvent event) {
        super.onServerStarted(event);
        server.initialize();
    }

    /**
     * Register the things that are related to server stopping, like persistent storage.
     * @param event The Forge event required for this.
     */
    @EventHandler
    @Override
    public void onServerStopping(FMLServerStoppingEvent event) {
        server.deinitialize();
        super.onServerStopping(event);
    }

    @Override
    public CreativeTabs constructDefaultCreativeTab() {
        // Uncomment the following line and specify an item config class to add a creative tab
        // return new ItemCreativeTab(this, new ItemConfigReference(ITEM CONFIG CLASS));
        return null;
    }

    @Override
    public void onGeneralConfigsRegister(ConfigHandler configHandler) {
        configHandler.add(new GeneralConfig());
    }

    @Override
    public ICommonProxy getProxy() {
        return proxy;
    }

    /**
     * Log a new info message for this mod.
     * @param message The message to show.
     */
    public static void clog(String message) {
        clog(Level.INFO, message);
    }
    
    /**
     * Log a new message of the given level for this mod.
     * @param level The level in which the message must be shown.
     * @param message The message to show.
     */
    public static void clog(Level level, String message) {
        IntegratedRest._instance.getLoggerHelper().log(level, message);
    }
    
}
