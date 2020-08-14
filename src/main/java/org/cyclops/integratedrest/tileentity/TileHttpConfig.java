package org.cyclops.integratedrest.tileentity;

import com.google.common.collect.Sets;
import net.minecraft.tileentity.TileEntityType;
import org.cyclops.cyclopscore.config.extendedconfig.TileEntityConfig;
import org.cyclops.integratedrest.IntegratedRest;
import org.cyclops.integratedrest.RegistryEntries;

/**
 * Config for the {@link TileHttp}.
 * @author rubensworks
 *
 */
public class TileHttpConfig extends TileEntityConfig<TileHttp> {

    public TileHttpConfig() {
        super(
                IntegratedRest._instance,
                "http",
                (eConfig) -> new TileEntityType<>(TileHttp::new,
                        Sets.newHashSet(RegistryEntries.BLOCK_HTTP), null)
        );
    }

}
