package org.cyclops.integratedrest;

import org.cyclops.cyclopscore.config.ConfigurablePropertyCommon;
import org.cyclops.cyclopscore.config.extendedconfig.DummyConfigCommon;
import org.cyclops.cyclopscore.init.IModBase;

/**
 * A config with general options for this mod.
 * @author rubensworks
 *
 */
public class GeneralConfig extends DummyConfigCommon<IModBase> {

    @ConfigurablePropertyCommon(category = "core", comment = "If an anonymous mod startup analytics request may be sent to our analytics service.")
    public static boolean analytics = true;

    @ConfigurablePropertyCommon(category = "core", comment = "If the version checker should be enabled.")
    public static boolean versionChecker = true;

    @ConfigurablePropertyCommon(category = "general", comment = "If the API should be enabled.", requiresMcRestart = true)
    public static boolean startApi = true;
    @ConfigurablePropertyCommon(category = "general", comment = "The port the API should be exposed on.")
    public static int apiPort = 3000;
    @ConfigurablePropertyCommon(category = "general", comment = "The base URL the API is exposed on, used for constructing URLs.")
    public static String apiBaseUrl = "http://localhost:3000/";

    public GeneralConfig() {
        super(IntegratedRest._instance, "general");
    }
}
