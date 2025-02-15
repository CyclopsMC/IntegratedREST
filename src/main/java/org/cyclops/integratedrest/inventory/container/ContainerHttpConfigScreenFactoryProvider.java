package org.cyclops.integratedrest.inventory.container;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import org.cyclops.cyclopscore.client.gui.ScreenFactorySafe;
import org.cyclops.cyclopscore.config.extendedconfig.GuiConfigScreenFactoryProvider;
import org.cyclops.integratedrest.client.gui.ContainerScreenHttp;

/**
 * @author rubensworks
 */
public class ContainerHttpConfigScreenFactoryProvider extends GuiConfigScreenFactoryProvider<ContainerHttp> {
    @Override
    public <U extends Screen & MenuAccess<ContainerHttp>> MenuScreens.ScreenConstructor<ContainerHttp, U> getScreenFactory() {
        return new ScreenFactorySafe<>(ContainerScreenHttp::new);
    }
}
