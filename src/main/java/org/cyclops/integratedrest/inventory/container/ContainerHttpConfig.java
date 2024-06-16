package org.cyclops.integratedrest.inventory.container;

import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.cyclops.cyclopscore.client.gui.ScreenFactorySafe;
import org.cyclops.cyclopscore.config.extendedconfig.GuiConfig;
import org.cyclops.integratedrest.IntegratedRest;
import org.cyclops.integratedrest.client.gui.ContainerScreenHttp;

/**
 * Config for {@link ContainerHttp}.
 * @author rubensworks
 */
public class ContainerHttpConfig extends GuiConfig<ContainerHttp> {

    public ContainerHttpConfig() {
        super(IntegratedRest._instance,
                "http",
                eConfig -> new MenuType<>(ContainerHttp::new, FeatureFlags.VANILLA_SET));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public <U extends Screen & MenuAccess<ContainerHttp>> MenuScreens.ScreenConstructor<ContainerHttp, U> getScreenFactory() {
        return new ScreenFactorySafe<>(ContainerScreenHttp::new);
    }

}
