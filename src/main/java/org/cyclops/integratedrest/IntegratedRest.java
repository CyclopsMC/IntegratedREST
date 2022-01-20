package org.cyclops.integratedrest;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.Level;
import org.cyclops.cyclopscore.config.ConfigHandler;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.cyclopscore.infobook.IInfoBookRegistry;
import org.cyclops.cyclopscore.init.ItemGroupMod;
import org.cyclops.cyclopscore.init.ModBaseVersionable;
import org.cyclops.cyclopscore.proxy.IClientProxy;
import org.cyclops.cyclopscore.proxy.ICommonProxy;
import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import org.cyclops.integrateddynamics.infobook.OnTheDynamicsOfIntegrationBook;
import org.cyclops.integratedrest.api.http.request.IRequestHandlerRegistry;
import org.cyclops.integratedrest.api.json.IValueTypeJsonHandlerRegistry;
import org.cyclops.integratedrest.block.BlockHttpConfig;
import org.cyclops.integratedrest.blockentity.BlockEntityHttpConfig;
import org.cyclops.integratedrest.client.model.HttpVariableModelProviders;
import org.cyclops.integratedrest.evaluate.HttpVariableFacadeHandler;
import org.cyclops.integratedrest.http.HttpServer;
import org.cyclops.integratedrest.http.request.RequestHandlerRegistry;
import org.cyclops.integratedrest.http.request.RequestHandlers;
import org.cyclops.integratedrest.inventory.container.ContainerHttpConfig;
import org.cyclops.integratedrest.json.ValueTypeJsonHandlerRegistry;
import org.cyclops.integratedrest.json.ValueTypeJsonHandlers;
import org.cyclops.integratedrest.proxy.ClientProxy;
import org.cyclops.integratedrest.proxy.CommonProxy;

/**
 * The main mod class of this mod.
 * @author rubensworks (aka kroeserr)
 *
 */
@Mod(Reference.MOD_ID)
public class IntegratedRest extends ModBaseVersionable<IntegratedRest> {

    public static IntegratedRest _instance;

    protected final HttpServer server;

    public IntegratedRest() {
        super(Reference.MOD_ID, (instance) -> _instance = instance);
        server = new HttpServer();

        MinecraftForge.EVENT_BUS.addListener(this::onApiServerStarted);
        MinecraftForge.EVENT_BUS.addListener(this::onApiServerStopping);
    }

    @Override
    protected void setup(FMLCommonSetupEvent event) {
        super.setup(event);

        // Registries
        getRegistryManager().addRegistry(IRequestHandlerRegistry.class, RequestHandlerRegistry.getInstance());
        getRegistryManager().addRegistry(IValueTypeJsonHandlerRegistry.class, ValueTypeJsonHandlerRegistry.getInstance());

        IntegratedDynamics._instance.getRegistryManager().getRegistry(IVariableFacadeHandlerRegistry.class).registerHandler(HttpVariableFacadeHandler.getInstance());

        if (MinecraftHelpers.isClientSide()) {
            HttpVariableModelProviders.load();
        }

        RequestHandlers.load();
        ValueTypeJsonHandlers.load();

        // Initialize info book
        IntegratedDynamics._instance.getRegistryManager().getRegistry(IInfoBookRegistry.class)
                .registerSection(this,
                        OnTheDynamicsOfIntegrationBook.getInstance(), "info_book.integrateddynamics.manual",
                        "/data/" + Reference.MOD_ID + "/info/rest_info.xml");
    }

    /**
     * Register the things that are related to server starting.
     * @param event The Forge event required for this.
     */
    public void onApiServerStarted(ServerStartedEvent event) {
        if (GeneralConfig.startApi) {
            server.initialize();
        }
    }

    /**
     * Register the things that are related to server stopping, like persistent storage.
     * @param event The Forge event required for this.
     */
    public void onApiServerStopping(ServerStoppingEvent event) {
        if (GeneralConfig.startApi) {
            server.deinitialize();
        }
    }

    @Override
    public CreativeModeTab constructDefaultCreativeModeTab() {
        return new ItemGroupMod(this, () -> RegistryEntries.ITEM_BLOCK_HTTP);
    }

    @Override
    protected void onConfigsRegister(ConfigHandler configHandler) {
        super.onConfigsRegister(configHandler);

        configHandler.addConfigurable(new GeneralConfig());

        configHandler.addConfigurable(new BlockHttpConfig());

        configHandler.addConfigurable(new BlockEntityHttpConfig());

        configHandler.addConfigurable(new ContainerHttpConfig());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    protected IClientProxy constructClientProxy() {
        return new ClientProxy();
    }

    @Override
    protected ICommonProxy constructCommonProxy() {
        return new CommonProxy();
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
