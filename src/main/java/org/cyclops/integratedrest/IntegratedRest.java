package org.cyclops.integratedrest;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.apache.logging.log4j.Level;
import org.cyclops.cyclopscore.config.ConfigHandler;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.cyclopscore.infobook.IInfoBookRegistry;
import org.cyclops.cyclopscore.init.ModBaseVersionable;
import org.cyclops.cyclopscore.proxy.IClientProxy;
import org.cyclops.cyclopscore.proxy.ICommonProxy;
import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import org.cyclops.integrateddynamics.core.event.IntegratedDynamicsSetupEvent;
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

    public IntegratedRest(IEventBus modEventBus) {
        super(Reference.MOD_ID, (instance) -> _instance = instance, modEventBus);
        server = new HttpServer();

        // Registries
        getRegistryManager().addRegistry(IRequestHandlerRegistry.class, RequestHandlerRegistry.getInstance());
        getRegistryManager().addRegistry(IValueTypeJsonHandlerRegistry.class, ValueTypeJsonHandlerRegistry.getInstance());

        NeoForge.EVENT_BUS.addListener(this::onApiServerStarted);
        NeoForge.EVENT_BUS.addListener(this::onApiServerStopping);
        modEventBus.addListener(this::onSetup);
    }

    @Override
    protected void setup(FMLCommonSetupEvent event) {
        super.setup(event);

        if (MinecraftHelpers.isClientSide()) {
            HttpVariableModelProviders.load();
        }
    }

    protected void onSetup(IntegratedDynamicsSetupEvent event) {
        IntegratedDynamics._instance.getRegistryManager().getRegistry(IVariableFacadeHandlerRegistry.class).registerHandler(HttpVariableFacadeHandler.getInstance());

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
    protected CreativeModeTab.Builder constructDefaultCreativeModeTab(CreativeModeTab.Builder builder) {
        return super.constructDefaultCreativeModeTab(builder)
                .icon(() -> new ItemStack(RegistryEntries.ITEM_BLOCK_HTTP));
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
