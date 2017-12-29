package org.cyclops.integratedrest;

import net.minecraft.client.gui.GuiScreen;
import org.cyclops.cyclopscore.client.gui.config.ExtendedConfigGuiFactoryBase;
import org.cyclops.cyclopscore.client.gui.config.GuiConfigOverviewBase;
import org.cyclops.cyclopscore.init.ModBase;

/**
 * @author rubensworks (aka kroeserr)
 */
public class GuiConfigOverview extends GuiConfigOverviewBase {
    /**
     * Make a new instance.
     * @param parentScreen the parent GuiScreen object
     */
    public GuiConfigOverview(GuiScreen parentScreen) {
        super(IntegratedRest._instance, parentScreen);
    }

    @Override
    public ModBase getMod() {
        return IntegratedRest._instance;
    }

    public static class ExtendedConfigGuiFactory extends ExtendedConfigGuiFactoryBase {

        @Override
        public Class<? extends GuiConfigOverviewBase> mainConfigGuiClass() {
            return GuiConfigOverview.class;
        }
    }
}
