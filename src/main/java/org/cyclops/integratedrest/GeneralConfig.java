package org.cyclops.integratedrest;

import org.cyclops.cyclopscore.config.ConfigurableProperty;
import org.cyclops.cyclopscore.config.extendedconfig.DummyConfig;
import org.cyclops.cyclopscore.tracking.Analytics;
import org.cyclops.cyclopscore.tracking.Versions;

/**
 * A config with general options for this mod.
 * @author rubensworks
 *
 */
public class GeneralConfig extends DummyConfig {

    @ConfigurableProperty(category = "core", comment = "If an anonymous mod startup analytics request may be sent to our analytics service.")
    public static boolean analytics = true;

    @ConfigurableProperty(category = "core", comment = "If the version checker should be enabled.")
    public static boolean versionChecker = true;

    @ConfigurableProperty(category = "general", comment = "If the API should be enabled.", requiresMcRestart = true)
    public static boolean startApi = true;
    @ConfigurableProperty(category = "general", comment = "The port the API should be exposed on.")
    public static int apiPort = 3000;
    @ConfigurableProperty(category = "general", comment = "The base URL the API is exposed on, used for constructing URLs.")
    public static String apiBaseUrl = "http://localhost:3000/";
    public GeneralConfig() {
        super(IntegratedRest._instance, "general");
    }

    @Override
    public void onRegistered() {
        if(analytics) {
            Analytics.registerMod(getMod(), Reference.GA_TRACKING_ID);
        }
        if(versionChecker) {
            Versions.registerMod(getMod(), IntegratedRest._instance, Reference.VERSION_URL);
        }
    }
}
