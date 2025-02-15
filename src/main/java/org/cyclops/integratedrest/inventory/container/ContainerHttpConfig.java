package org.cyclops.integratedrest.inventory.container;

import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import org.cyclops.cyclopscore.config.extendedconfig.GuiConfigCommon;
import org.cyclops.cyclopscore.config.extendedconfig.GuiConfigScreenFactoryProvider;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.integratedrest.IntegratedRest;

/**
 * Config for {@link ContainerHttp}.
 * @author rubensworks
 */
public class ContainerHttpConfig extends GuiConfigCommon<ContainerHttp, IModBase> {

    public ContainerHttpConfig() {
        super(IntegratedRest._instance,
                "http",
                eConfig -> new MenuType<>(ContainerHttp::new, FeatureFlags.VANILLA_SET));
    }

    @Override
    public GuiConfigScreenFactoryProvider<ContainerHttp> getScreenFactoryProvider() {
        return new ContainerHttpConfigScreenFactoryProvider();
    }
}
