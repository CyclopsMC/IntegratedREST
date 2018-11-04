package org.cyclops.integratedrest.tileentity;

import com.google.common.collect.Sets;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.expression.VariableAdapter;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueType;
import org.cyclops.integrateddynamics.api.evaluate.variable.IVariable;
import org.cyclops.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import org.cyclops.integrateddynamics.api.network.INetworkElement;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.capability.networkelementprovider.NetworkElementProviderConfig;
import org.cyclops.integrateddynamics.capability.networkelementprovider.NetworkElementProviderSingleton;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueHelpers;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integrateddynamics.tileentity.TileProxy;
import org.cyclops.integratedrest.api.item.IHttpVariableFacade;
import org.cyclops.integratedrest.evaluate.HttpVariableFacadeHandler;
import org.cyclops.integratedrest.item.HttpVariableFacade;
import org.cyclops.integratedrest.network.HttpNetworkElement;

/**
 * A tile entity for the http block.
 *
 * @author rubensworks
 */
public class TileHttp extends TileProxy {

    public static final int SLOT_WRITE_IN = 0;
    public static final int SLOT_WRITE_OUT = 1;

    private IValueType valueType = ValueTypes.CATEGORY_ANY;
    private IValue value = ValueTypeBoolean.ValueBoolean.of(false);
    private final IVariable variable;

    @Setter
    private EntityPlayer lastPlayer = null;

    public TileHttp() {
        super(2);

        addSlotsToSide(EnumFacing.UP, Sets.newHashSet(SLOT_WRITE_IN));
        addSlotsToSide(EnumFacing.DOWN, Sets.newHashSet(SLOT_WRITE_OUT));
        addSlotsToSide(EnumFacing.NORTH, Sets.newHashSet(SLOT_WRITE_IN));
        addSlotsToSide(EnumFacing.SOUTH, Sets.newHashSet(SLOT_WRITE_OUT));
        addSlotsToSide(EnumFacing.WEST, Sets.newHashSet(SLOT_WRITE_OUT));
        addSlotsToSide(EnumFacing.EAST, Sets.newHashSet(SLOT_WRITE_IN));

        this.variable = new VariableAdapter() {
            @Override
            public IValueType getType() {
                return TileHttp.this.valueType;
            }

            @Override
            public IValue getValue() throws EvaluationException {
                if (TileHttp.this.value == null) {
                    throw new EvaluationException("HTTP Proxy is not exposing a value");
                }
                return TileHttp.this.value;
            }
        };

        addCapabilityInternal(NetworkElementProviderConfig.CAPABILITY, new NetworkElementProviderSingleton() {
            @Override
            public INetworkElement createNetworkElement(World world, BlockPos blockPos) {
                return new HttpNetworkElement(DimPos.of(world, blockPos));
            }
        });
    }

    @Override
    public ItemStack writeProxyInfo(boolean generateId, ItemStack itemStack, final int proxyId) {
        IVariableFacadeHandlerRegistry registry = IntegratedDynamics._instance.getRegistryManager().getRegistry(IVariableFacadeHandlerRegistry.class);
        return registry.writeVariableFacadeItem(generateId, itemStack, HttpVariableFacadeHandler.getInstance(), new IVariableFacadeHandlerRegistry.IVariableFacadeFactory<IHttpVariableFacade>() {
            @Override
            public IHttpVariableFacade create(boolean generateId) {
                return new HttpVariableFacade(generateId, proxyId);
            }

            @Override
            public IHttpVariableFacade create(int id) {
                return new HttpVariableFacade(id, proxyId);
            }
        }, lastPlayer, getBlock());
    }

    @Override
    public IVariable<?> getVariable(IPartNetwork network) {
        return variable;
    }

    @Override
    public int getSlotRead() {
        return -1;
    }

    @Override
    protected int getSlotWriteIn() {
        return SLOT_WRITE_IN;
    }

    @Override
    protected int getSlotWriteOut() {
        return SLOT_WRITE_OUT;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag = super.writeToNBT(tag);
        tag.setString("valueType", valueType.getUnlocalizedName());
        tag.setTag("value", ValueHelpers.serialize(value));
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.valueType = ValueTypes.REGISTRY.getValueType(tag.getString("valueType"));
        if (tag.hasKey("value", Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound valueTag = (NBTTagCompound) tag.getTag("value");
            this.value = ValueHelpers.deserialize(valueTag);

        }
    }

    public IValueType getValueType() {
        return valueType;
    }

    public void setValueType(IValueType valueType) {
        this.valueType = valueType;
        if (!this.valueType.isCategory()) {
            this.value = this.valueType.getDefault();
        } else {
            this.value = ValueTypeBoolean.ValueBoolean.of(false);
        }
        sendUpdate();
    }

    public void setValue(IValue value) {
        this.value = value;
    }

    @Override
    public boolean hasVariable() {
        return this.value != null;
    }

    @Override
    protected void updateReadVariable(boolean sendVariablesUpdateEvent) {
        // Do nothing
    }
}
