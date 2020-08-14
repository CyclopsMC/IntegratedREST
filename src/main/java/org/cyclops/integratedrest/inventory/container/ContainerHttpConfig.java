package org.cyclops.integratedrest.inventory.container;

import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
                eConfig -> new ContainerType<>(ContainerHttp::new));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public <U extends Screen & IHasContainer<ContainerHttp>> ScreenManager.IScreenFactory<ContainerHttp, U> getScreenFactory() {
        return new ScreenFactorySafe<>(ContainerScreenHttp::new);
    }

}
