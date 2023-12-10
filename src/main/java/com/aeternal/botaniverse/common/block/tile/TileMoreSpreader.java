package com.aeternal.botaniverse.common.block.tile;

import com.aeternal.botaniverse.Constants;
import com.aeternal.botaniverse.api.state.BotaniverseStateProps;
import com.aeternal.botaniverse.api.state.enums.MoreSpreaderVariant;
import com.aeternal.botaniverse.common.block.ModVBlocks;
import com.google.common.base.Predicates;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.*;
import vazkii.botania.api.wand.IWandBindable;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.common.block.tile.TileSimpleInventory;
import vazkii.botania.common.core.handler.ConfigHandler;
import vazkii.botania.common.core.handler.ManaNetworkHandler;
import vazkii.botania.common.core.handler.ModSounds;
import vazkii.botania.common.core.helper.Vector3;
import vazkii.botania.common.entity.EntityManaBurst;
import vazkii.botania.common.entity.EntityManaBurst.PositionProperties;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class TileMoreSpreader extends TileSimpleInventory implements IManaCollector, IWandBindable, IKeyLocked, IThrottledPacket, IManaSpreader, IDirectioned, ITickable {

    private static final int NILFHEIM_MAX_MANA = 25000;
    private static final int MUSPELHEIM_MAX_MANA = 62500;
    private static final int ALFHEIM_MAX_MANA = 140625;
    private static final int ASGARD_MAX_MANA = 210937;
    private static final int CREATIVE_MAX_MANA = Integer.MAX_VALUE;
    private static final int TICKS_ALLOWED_WITHOUT_PINGBACK = 20;
    private static final double PINGBACK_EXPIRED_SEARCH_DISTANCE = 0.5;

    private static final String TAG_HAS_IDENTITY = "hasIdentity";
    private static final String TAG_UUID_MOST = "uuidMost";
    private static final String TAG_UUID_LEAST = "uuidLeast";
    private static final String TAG_MANA = "mana";
    private static final String TAG_KNOWN_MANA = "knownMana";
    private static final String TAG_REQUEST_UPDATE = "requestUpdate";
    private static final String TAG_ROTATION_X = "rotationX";
    private static final String TAG_ROTATION_Y = "rotationY";
    private static final String TAG_PADDING_COLOR = "paddingColor";
    private static final String TAG_CAN_SHOOT_BURST = "canShootBurst";
    private static final String TAG_PINGBACK_TICKS = "pingbackTicks";
    private static final String TAG_LAST_PINGBACK_X = "lastPingbackX";
    private static final String TAG_LAST_PINGBACK_Y = "lastPingbackY";
    private static final String TAG_LAST_PINGBACK_Z = "lastPingbackZ";

    private static final String TAG_FORCE_CLIENT_BINDING_X = "forceClientBindingX";
    private static final String TAG_FORCE_CLIENT_BINDING_Y = "forceClientBindingY";
    private static final String TAG_FORCE_CLIENT_BINDING_Z = "forceClientBindingZ";

    // Map Maker Tags

    private static final String TAG_INPUT_KEY = "inputKey";
    private static final String TAG_OUTPUT_KEY = "outputKey";

    private static final String TAG_MAPMAKER_OVERRIDE = "mapmakerOverrideEnabled";
    private static final String TAG_FORCED_COLOR = "mmForcedColor";
    private static final String TAG_FORCED_MANA_PAYLOAD = "mmForcedManaPayload";
    private static final String TAG_FORCED_TICKS_BEFORE_MANA_LOSS = "mmForcedTicksBeforeManaLoss";
    private static final String TAG_FORCED_MANA_LOSS_PER_TICK = "mmForcedManaLossPerTick";
    private static final String TAG_FORCED_GRAVITY = "mmForcedGravity";
    private static final String TAG_FORCED_VELOCITY_MULTIPLIER = "mmForcedVelocityMultiplier";

    boolean mapmakerOverride = false;
    int mmForcedColor = 0x20FF20;
    int mmForcedManaPayload = 160;
    int mmForcedTicksBeforeManaLoss = 60;
    float mmForcedManaLossPerTick = 4F;
    float mmForcedGravity = 0F;
    float mmForcedVelocityMultiplier = 1F;

    String inputKey = "";
    final String outputKey = "";

    // End Map Maker Tags

    public static final boolean staticNilfheim = false;
    public static final boolean staticMuspelheim = false;
    public static final boolean staticAlfheim = false;
    public static final boolean staticAsgard = false;
    public static final boolean staticCreative = false;
    UUID identity;

    int mana;
    int knownMana = -1;
    public float rotationX, rotationY;
    public int paddingColor = -1;

    boolean requestsClientUpdate = false;
    boolean hasReceivedInitialPacket = false;

    IManaReceiver receiver = null;
    IManaReceiver receiverLastTick = null;

    boolean redstoneLastTick = true;
    public boolean canShootBurst = true;
    public int lastBurstDeathTick = -1;
    public int burstParticleTick = 0;

    public int pingbackTicks = 0;
    public double lastPingbackX = 0;
    public double lastPingbackY = -1;
    public double lastPingbackZ = 0;

    List<PositionProperties> lastTentativeBurst;
    boolean invalidTentativeBurst = false;

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, @Nonnull IBlockState oldState, @Nonnull IBlockState newState) {
        if(oldState.getBlock() != newState.getBlock())
            return true;
        if(oldState.getBlock() != ModVBlocks.morespreader || newState.getBlock() != ModVBlocks.morespreader)
            return true;
        return oldState.getValue(BotaniverseStateProps.MORE_SPREADER_VARIANT) != newState.getValue(BotaniverseStateProps.MORE_SPREADER_VARIANT);
    }

    @Override
    public boolean isFull() {
        return mana >= getMaxMana();
    }

    @Override
    public void recieveMana(int mana) {
        this.mana = Math.min(this.mana + mana, getMaxMana());
    }

    @Override
    public void invalidate() {
        super.invalidate();
        ManaNetworkEvent.removeCollector(this);
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        ManaNetworkEvent.removeCollector(this);
    }

    @Override
    public void update() {
        boolean inNetwork = ManaNetworkHandler.instance.isCollectorIn(this);
        boolean wasInNetwork = inNetwork;
        if(!inNetwork && !isInvalid()) {
            ManaNetworkEvent.addCollector(this);
        }

        boolean redstone = false;

        for(EnumFacing dir : EnumFacing.VALUES) {
            TileEntity tileAt = world.getTileEntity(pos.offset(dir));
            if(world.isBlockLoaded(pos.offset(dir), false) && tileAt instanceof IManaPool) {
                IManaPool pool = (IManaPool) tileAt;
                if(wasInNetwork && pool != receiver) {
                    if(pool instanceof IKeyLocked && !((IKeyLocked) pool).getOutputKey().equals(getInputKey()))
                        continue;

                    int manaInPool = pool.getCurrentMana();
                    if(manaInPool > 0 && !isFull()) {
                        int manaMissing = getMaxMana() - mana;
                        int manaToRemove = Math.min(manaInPool, manaMissing);
                        pool.recieveMana(-manaToRemove);
                        recieveMana(manaToRemove);
                    }
                }
            }

            int redstoneSide = world.getRedstonePower(pos.offset(dir), dir);
            if(redstoneSide > 0)
                redstone = true;
        }

        if(needsNewBurstSimulation())
            checkForReceiver();

        if(!canShootBurst)
            if(pingbackTicks <= 0) {
                double x = lastPingbackX;
                double y = lastPingbackY;
                double z = lastPingbackZ;
                AxisAlignedBB aabb = new AxisAlignedBB(x, y, z, x, y, z).grow(PINGBACK_EXPIRED_SEARCH_DISTANCE, PINGBACK_EXPIRED_SEARCH_DISTANCE, PINGBACK_EXPIRED_SEARCH_DISTANCE);
                List bursts = world.getEntitiesWithinAABB(EntityThrowable.class, aabb, Predicates.instanceOf(IManaBurst.class));
                IManaBurst found = null;
                UUID identity = getIdentifier();
                for(IManaBurst burst : (List<IManaBurst>) bursts)
                    if(burst != null && identity.equals(burst.getShooterUUID())) {
                        found = burst;
                        break;
                    }

                if(found != null)
                    found.ping();
                else setCanShoot(true);
            } else pingbackTicks--;

        boolean shouldShoot = !redstone;

        if(shouldShoot && receiver != null && receiver instanceof IKeyLocked)
            shouldShoot = ((IKeyLocked) receiver).getInputKey().equals(getOutputKey());

        ItemStack lens = itemHandler.getStackInSlot(0);
        ILensControl control = getLensController(lens);
        if(control != null) {
            control.onControlledSpreaderTick(lens, this, redstone);

            shouldShoot &= control.allowBurstShooting(lens, this, redstone);
        }

        if(shouldShoot)
            tryShootBurst();

        if(receiverLastTick != receiver && !world.isRemote) {
            requestsClientUpdate = true;
            VanillaPacketDispatcher.dispatchTEToNearbyPlayers(world, pos);
        }

        redstoneLastTick = redstone;
        receiverLastTick = receiver;
    }

    @Override
    public void writePacketNBT(NBTTagCompound cmp) {
        super.writePacketNBT(cmp);

        UUID identity = getIdentifier();
        cmp.setBoolean(TAG_HAS_IDENTITY, true);
        cmp.setLong(TAG_UUID_MOST, identity.getMostSignificantBits());
        cmp.setLong(TAG_UUID_LEAST, identity.getLeastSignificantBits());

        cmp.setInteger(TAG_MANA, mana);
        cmp.setFloat(TAG_ROTATION_X, rotationX);
        cmp.setFloat(TAG_ROTATION_Y, rotationY);
        cmp.setBoolean(TAG_REQUEST_UPDATE, requestsClientUpdate);
        cmp.setInteger(TAG_PADDING_COLOR, paddingColor);
        cmp.setBoolean(TAG_CAN_SHOOT_BURST, canShootBurst);

        cmp.setInteger(TAG_PINGBACK_TICKS, pingbackTicks);
        cmp.setDouble(TAG_LAST_PINGBACK_X, lastPingbackX);
        cmp.setDouble(TAG_LAST_PINGBACK_Y, lastPingbackY);
        cmp.setDouble(TAG_LAST_PINGBACK_Z, lastPingbackZ);

        cmp.setString(TAG_INPUT_KEY, inputKey);
        cmp.setString(TAG_OUTPUT_KEY, outputKey);

        cmp.setInteger(TAG_FORCE_CLIENT_BINDING_X, receiver == null ? 0 : ((TileEntity) receiver).getPos().getX());
        cmp.setInteger(TAG_FORCE_CLIENT_BINDING_Y, receiver == null ? -1 : ((TileEntity) receiver).getPos().getY());
        cmp.setInteger(TAG_FORCE_CLIENT_BINDING_Z, receiver == null ? 0 : ((TileEntity) receiver).getPos().getZ());

        cmp.setBoolean(TAG_MAPMAKER_OVERRIDE, mapmakerOverride);
        cmp.setInteger(TAG_FORCED_COLOR, mmForcedColor);
        cmp.setInteger(TAG_FORCED_MANA_PAYLOAD, mmForcedManaPayload);
        cmp.setInteger(TAG_FORCED_TICKS_BEFORE_MANA_LOSS, mmForcedTicksBeforeManaLoss);
        cmp.setFloat(TAG_FORCED_MANA_LOSS_PER_TICK, mmForcedManaLossPerTick);
        cmp.setFloat(TAG_FORCED_GRAVITY, mmForcedGravity);
        cmp.setFloat(TAG_FORCED_VELOCITY_MULTIPLIER, mmForcedVelocityMultiplier);

        requestsClientUpdate = false;
    }

    @Override
    public void readPacketNBT(NBTTagCompound cmp) {
        super.readPacketNBT(cmp);

        if(cmp.getBoolean(TAG_HAS_IDENTITY)) {
            long most = cmp.getLong(TAG_UUID_MOST);
            long least = cmp.getLong(TAG_UUID_LEAST);
            UUID identity = getIdentifierUnsafe();
            if(identity == null || most != identity.getMostSignificantBits() || least != identity.getLeastSignificantBits())
                this.identity = new UUID(most, least);
        } else getIdentifier();

        mana = cmp.getInteger(TAG_MANA);
        rotationX = cmp.getFloat(TAG_ROTATION_X);
        rotationY = cmp.getFloat(TAG_ROTATION_Y);
        requestsClientUpdate = cmp.getBoolean(TAG_REQUEST_UPDATE);

        if(cmp.hasKey(TAG_INPUT_KEY))
            inputKey = cmp.getString(TAG_INPUT_KEY);
        if(cmp.hasKey(TAG_OUTPUT_KEY))
            inputKey = cmp.getString(TAG_OUTPUT_KEY);

        mapmakerOverride = cmp.getBoolean(TAG_MAPMAKER_OVERRIDE);
        mmForcedColor = cmp.getInteger(TAG_FORCED_COLOR);
        mmForcedManaPayload = cmp.getInteger(TAG_FORCED_MANA_PAYLOAD);
        mmForcedTicksBeforeManaLoss = cmp.getInteger(TAG_FORCED_TICKS_BEFORE_MANA_LOSS);
        mmForcedManaLossPerTick = cmp.getFloat(TAG_FORCED_MANA_LOSS_PER_TICK);
        mmForcedGravity = cmp.getFloat(TAG_FORCED_GRAVITY);
        mmForcedVelocityMultiplier = cmp.getFloat(TAG_FORCED_VELOCITY_MULTIPLIER);

        if(cmp.hasKey(TAG_KNOWN_MANA))
            knownMana = cmp.getInteger(TAG_KNOWN_MANA);
        if(cmp.hasKey(TAG_PADDING_COLOR))
            paddingColor = cmp.getInteger(TAG_PADDING_COLOR);
        if(cmp.hasKey(TAG_CAN_SHOOT_BURST))
            canShootBurst = cmp.getBoolean(TAG_CAN_SHOOT_BURST);

        pingbackTicks = cmp.getInteger(TAG_PINGBACK_TICKS);
        lastPingbackX = cmp.getDouble(TAG_LAST_PINGBACK_X);
        lastPingbackY = cmp.getDouble(TAG_LAST_PINGBACK_Y);
        lastPingbackZ = cmp.getDouble(TAG_LAST_PINGBACK_Z);

        if(requestsClientUpdate && world != null) {
            int x = cmp.getInteger(TAG_FORCE_CLIENT_BINDING_X);
            int y = cmp.getInteger(TAG_FORCE_CLIENT_BINDING_Y);
            int z = cmp.getInteger(TAG_FORCE_CLIENT_BINDING_Z);
            if(y != -1) {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if(tile instanceof IManaReceiver)
                    receiver = (IManaReceiver) tile;
                else receiver = null;
            } else receiver = null;
        }

        if(world != null && world.isRemote)
            hasReceivedInitialPacket = true;
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return true;
    }

    @Override
    public int getCurrentMana() {
        return mana;
    }

    public void onWanded(EntityPlayer player, ItemStack wand) {
        if(player == null)
            return;

        if(!player.isSneaking()) {
            if(!world.isRemote) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                writePacketNBT(nbttagcompound);
                nbttagcompound.setInteger(TAG_KNOWN_MANA, mana);
                if(player instanceof EntityPlayerMP)
                    ((EntityPlayerMP) player).connection.sendPacket(new SPacketUpdateTileEntity(pos, -999, nbttagcompound));
            }
            world.playSound(null, player.posX, player.posY, player.posZ, ModSounds.ding, SoundCategory.PLAYERS, 0.1F, 1);
        } else {
            RayTraceResult pos = raytraceFromEntity(world, player, true);
            if(pos != null && pos.hitVec != null && !world.isRemote) {
                double x = pos.hitVec.x - getPos().getX() - 0.5;
                double y = pos.hitVec.y - getPos().getY() - 0.5;
                double z = pos.hitVec.z - getPos().getZ() - 0.5;

                if(pos.sideHit != EnumFacing.DOWN && pos.sideHit != EnumFacing.UP) {
                    Vector3 clickVector = new Vector3(x, 0, z);
                    Vector3 relative = new Vector3(-0.5, 0, 0);
                    double angle = Math.acos(clickVector.dotProduct(relative) / (relative.mag() * clickVector.mag())) * 180D / Math.PI;

                    rotationX = (float) angle + 180F;
                    if(clickVector.z < 0)
                        rotationX = 360 - rotationX;
                }

                double angle = y * 180;
                rotationY = -(float) angle;

                checkForReceiver();
                requestsClientUpdate = true;
                VanillaPacketDispatcher.dispatchTEToNearbyPlayers(world, getPos());
            }
        }
    }

    private boolean needsNewBurstSimulation() {
        if(world.isRemote && !hasReceivedInitialPacket)
            return false;

        if(lastTentativeBurst == null)
            return true;

        for(PositionProperties props : lastTentativeBurst)
            if(!props.contentsEqual(world)) {
                invalidTentativeBurst = props.invalid;
                return !invalidTentativeBurst;
            }

        return false;
    }

    public void tryShootBurst() {
        if(receiver != null  && !invalidTentativeBurst) {
            if(canShootBurst && (receiver.canRecieveManaFromBursts() && !receiver.isFull())) {
                EntityManaBurst burst = getBurst(false);
                if(burst != null) {
                    if(!world.isRemote) {
                        mana -= burst.getStartingMana();
                        burst.setShooterUUID(getIdentifier());
                        world.spawnEntity(burst);
                        burst.ping();
                        if(!ConfigHandler.silentSpreaders)
                            world.playSound(null, pos, ModSounds.spreaderFire, SoundCategory.BLOCKS, 0.05F * (paddingColor != -1 ? 0.2F : 1F), 0.7F + 0.3F * (float) Math.random());
                    }
                }
            }
        }
    }


    public boolean isNILFHEIM_SPREADER() {
        updateContainingBlockInfo();
        return world == null ? staticNilfheim : getBlockMetadata() == MoreSpreaderVariant.NILFHEIM.ordinal();
    }
    public boolean isMUSPELHEIM_SPREADER() {
        updateContainingBlockInfo();
        return world == null ? staticMuspelheim : getBlockMetadata() == MoreSpreaderVariant.MUSPELHEIM.ordinal();
    }
    public boolean isALFHEIM_SPREADER() {
        updateContainingBlockInfo();
        return world == null ? staticAlfheim : getBlockMetadata() == MoreSpreaderVariant.ALFHEIM.ordinal();
    }
    public boolean isASGARD_SPREADER() {
        updateContainingBlockInfo();
        return world == null ? staticAsgard : getBlockMetadata() == MoreSpreaderVariant.ASGARD.ordinal();
    }
    public boolean isCREATIVE_SPREADER() {
        updateContainingBlockInfo();
        return world == null ? staticCreative : getBlockMetadata() == MoreSpreaderVariant.CREATIVE.ordinal();
    }

    public void checkForReceiver() {
        ItemStack stack = itemHandler.getStackInSlot(0);
        ILensControl control = getLensController(stack);
        if(control != null && !control.allowBurstShooting(stack, this, false))
            return;

        EntityManaBurst fakeBurst = getBurst(true);
        fakeBurst.setScanBeam();
        TileEntity receiver = fakeBurst.getCollidedTile(true);

        if(receiver != null
                && receiver instanceof IManaReceiver
                && receiver.hasWorld()
                && receiver.getWorld().isBlockLoaded(receiver.getPos(), !receiver.getWorld().isRemote))
            this.receiver = (IManaReceiver) receiver;
        else this.receiver = null;
        lastTentativeBurst = fakeBurst.propsList;
    }

    @Override
    public IManaBurst runBurstSimulation() {
        EntityManaBurst fakeBurst = getBurst(true);
        fakeBurst.setScanBeam();
        fakeBurst.getCollidedTile(true);
        return fakeBurst;
    }

    public EntityManaBurst getBurst(boolean fake) {
        EntityManaBurst burst = new EntityManaBurst(this, fake);

        boolean nilfheim = isNILFHEIM_SPREADER();
        boolean muspelheim = isMUSPELHEIM_SPREADER();
        boolean alfheim = isALFHEIM_SPREADER();
        boolean asgard = isASGARD_SPREADER();
        boolean creative = isCREATIVE_SPREADER();
        int maxMana = nilfheim ? 2400 : muspelheim ? 9600 : alfheim ? 38400 : asgard ? 153600 : creative ? 107374182 : 2400 ;
        int color = isNILFHEIM_SPREADER() ? 0xC000FF : muspelheim ? 0xFF2020 : alfheim ? 0x80C0FF : asgard ? 0xFFFF00 : creative ? 0xC000FF : 0xC000FF;
        int ticksBeforeManaLoss = nilfheim ? 20 : muspelheim ? 20 : alfheim ? 20 : asgard ? 20 : creative ? 20 : 19;
        float manaLossPerTick = nilfheim ? 120F : muspelheim ? 480F: alfheim ? 1920F : asgard ? 7680F : creative ? 15F : 15F;
        float motionModifier = nilfheim ? 4F : muspelheim ? 4F : alfheim ? 4F : asgard ? 4F : creative ? 20F : 4F;
        float gravity = 0F;
        BurstProperties props = new BurstProperties(maxMana, ticksBeforeManaLoss, manaLossPerTick, gravity, motionModifier, color);

        ItemStack lens = itemHandler.getStackInSlot(0);
        if(!lens.isEmpty() && lens.getItem() instanceof ILensEffect)
            ((ILensEffect) lens.getItem()).apply(lens, props);

        burst.setSourceLens(lens);
        if(getCurrentMana() >= props.maxMana || fake) {
            if(mapmakerOverride) {
                burst.setColor(mmForcedColor);
                burst.setMana(mmForcedManaPayload);
                burst.setStartingMana(mmForcedManaPayload);
                burst.setMinManaLoss(mmForcedTicksBeforeManaLoss);
                burst.setManaLossPerTick(mmForcedManaLossPerTick);
                burst.setGravity(mmForcedGravity);
                burst.setMotion(burst.motionX * mmForcedVelocityMultiplier, burst.motionY * mmForcedVelocityMultiplier, burst.motionZ * mmForcedVelocityMultiplier);
            } else {
                burst.setColor(props.color);
                burst.setMana(props.maxMana);
                burst.setStartingMana(props.maxMana);
                burst.setMinManaLoss(props.ticksBeforeManaLoss);
                burst.setManaLossPerTick(props.manaLossPerTick);
                burst.setGravity(props.gravity);
                burst.setMotion(burst.motionX * props.motionModifier, burst.motionY * props.motionModifier, burst.motionZ * props.motionModifier);
            }

            return burst;
        }
        return null;
    }

    public ILensControl getLensController(ItemStack stack) {
        if(!stack.isEmpty() && stack.getItem() instanceof ILensControl) {
            ILensControl control = (ILensControl) stack.getItem();
            if(control.isControlLens(stack))
                return control;
        }

        return null;
    }

    // [VanillaCopy] Item.rayTrace
    protected RayTraceResult raytraceFromEntity(World worldIn, EntityPlayer playerIn, boolean useLiquids) {
        float f = playerIn.rotationPitch;
        float f1 = playerIn.rotationYaw;
        double d0 = playerIn.posX;
        double d1 = playerIn.posY + (double) playerIn.getEyeHeight();
        double d2 = playerIn.posZ;
        Vec3d vec3d = new Vec3d(d0, d1, d2);
        float f2 = MathHelper.cos(-f1 * 0.017453292F - (float) Math.PI);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - (float) Math.PI);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d3 = 5.0D;
        if(playerIn instanceof net.minecraft.entity.player.EntityPlayerMP) {
            d3 = ((net.minecraft.entity.player.EntityPlayerMP) playerIn).interactionManager.getBlockReachDistance();
        }
        Vec3d vec3d1 = vec3d.add((double) f6 * d3, (double) f5 * d3, (double) f7 * d3);
        return worldIn.rayTraceBlocks(vec3d, vec3d1, useLiquids, !useLiquids, false);
    }

    @SideOnly(Side.CLIENT)
    public void renderHUD(Minecraft mc, ScaledResolution res) {
        String name = I18n.format(new ItemStack(ModVBlocks.morespreader, 1, world.getBlockState(getPos()).getValue(BotaniverseStateProps.MORE_SPREADER_VARIANT).ordinal()).getTranslationKey().replaceAll("tile.", "tile." + Constants.PREFIX_MOD) + ".name");
        int color = isNILFHEIM_SPREADER() ? 0xC000FF : isMUSPELHEIM_SPREADER() ? 0xFF2020 : isALFHEIM_SPREADER() ? 0x80C0FF : 0xFFFF00;
        HUDHandler.drawSimpleManaHUD(color, knownMana, getMaxMana(), name, res);

        ItemStack lens = itemHandler.getStackInSlot(0);
        if(!lens.isEmpty()) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            String lensName = lens.getDisplayName();
            int width = 16 + mc.fontRenderer.getStringWidth(lensName) / 2;
            int x = res.getScaledWidth() / 2 - width;
            int y = res.getScaledHeight() / 2 + 50;

            mc.fontRenderer.drawStringWithShadow(lensName, x + 20, y + 5, color);
            RenderHelper.enableGUIStandardItemLighting();
            mc.getRenderItem().renderItemAndEffectIntoGUI(lens, x, y);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableBlend();
        }

        if(receiver != null) {
            TileEntity receiverTile = (TileEntity) receiver;
            ItemStack recieverStack = new ItemStack(world.getBlockState(receiverTile.getPos()).getBlock(), 1, receiverTile.getBlockMetadata());
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            if(!recieverStack.isEmpty()) {
                String stackName = recieverStack.getDisplayName();
                int width = 16 + mc.fontRenderer.getStringWidth(stackName) / 2;
                int x = res.getScaledWidth() / 2 - width;
                int y = res.getScaledHeight() / 2 + 30;

                mc.fontRenderer.drawStringWithShadow(stackName, x + 20, y + 5, color);
                RenderHelper.enableGUIStandardItemLighting();
                mc.getRenderItem().renderItemAndEffectIntoGUI(recieverStack, x, y);
                RenderHelper.disableStandardItemLighting();
            }

            GlStateManager.disableLighting();
            GlStateManager.disableBlend();
        }

        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    @Override
    public void onClientDisplayTick() {
        if(world != null) {
            EntityManaBurst burst = getBurst(true);
            burst.getCollidedTile(false);
        }
    }

    @Override
    public float getManaYieldMultiplier(IManaBurst burst) {
        return 1F;
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    protected SimpleItemStackHandler createItemHandler() {
        return new SimpleItemStackHandler(this, true) {
            @Override
            protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
                return 1;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if(!stack.isEmpty() && stack.getItem() instanceof ILens)
                    return super.insertItem(slot, stack, simulate);
                else return stack;
            }
        };
    }

    @Override
    public void markDirty() {
        checkForReceiver();
        VanillaPacketDispatcher.dispatchTEToNearbyPlayers(world, pos);
    }

    @Override
    public BlockPos getBinding() {
        if(receiver == null)
            return null;

        TileEntity tile = (TileEntity) receiver;
        return tile.getPos();
    }

    @Override
    public int getMaxMana() {
        return isNILFHEIM_SPREADER() ? NILFHEIM_MAX_MANA : isMUSPELHEIM_SPREADER() ? MUSPELHEIM_MAX_MANA : isALFHEIM_SPREADER() ? ALFHEIM_MAX_MANA : isASGARD_SPREADER() ? ASGARD_MAX_MANA : isCREATIVE_SPREADER() ? CREATIVE_MAX_MANA : NILFHEIM_MAX_MANA;
    }
    @Override
    public String getInputKey() {
        return inputKey;
    }

    @Override
    public String getOutputKey() {
        return outputKey;
    }

    @Override
    public boolean canSelect(EntityPlayer player, ItemStack wand, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public boolean bindTo(EntityPlayer player, ItemStack wand, BlockPos pos, EnumFacing side) {
        Vector3 thisVec = Vector3.fromTileEntityCenter(this);
        Vector3 blockVec = new Vector3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        AxisAlignedBB axis = player.world.getBlockState(pos).getCollisionBoundingBox(player.world, pos);
        if(axis != null)
            axis = axis.offset(pos);
        else axis = new AxisAlignedBB(pos, pos.add(1, 1, 1));

        if(!blockVec.isInside(axis))
            blockVec = new Vector3(axis.minX + (axis.maxX - axis.minX) / 2, axis.minY + (axis.maxY - axis.minY) / 2, axis.minZ + (axis.maxZ - axis.minZ) / 2);

        Vector3 diffVec =  blockVec.subtract(thisVec);
        Vector3 diffVec2D = new Vector3(diffVec.x, diffVec.z, 0);
        Vector3 rotVec = new Vector3(0, 1, 0);
        double angle = rotVec.angle(diffVec2D) / Math.PI * 180.0;

        if(blockVec.x < thisVec.x)
            angle = -angle;

        rotationX = (float) angle + 90;

        rotVec = new Vector3(diffVec.x, 0, diffVec.z);
        angle = diffVec.angle(rotVec) * 180F / Math.PI;
        if(blockVec.y < thisVec.y)
            angle = -angle;
        rotationY = (float) angle;

        checkForReceiver();
        return true;
    }

    @Override
    public void markDispatchable() {}

    @Override
    public float getRotationX() {
        return rotationX;
    }

    @Override
    public float getRotationY() {
        return rotationY;
    }

    @Override
    public void setRotationX(float rot) {
        rotationX = rot;
    }

    @Override
    public void setRotationY(float rot) {
        rotationY = rot;
    }

    @Override
    public void rotate(Rotation rotationIn) {
        switch (rotationIn)
        {
            case CLOCKWISE_90:
                rotationX += 270F;
                break;
            case CLOCKWISE_180:
                rotationX += 180F;
                break;
            case COUNTERCLOCKWISE_90:
                rotationX += 90F;
                break;
            default: break;
        }

        if(rotationX >= 360F)
            rotationX -= 360F;
    }

    @Override
    public void mirror(Mirror mirrorIn) {
        switch (mirrorIn)
        {
            case LEFT_RIGHT:
                rotationX = 360F - rotationX;
                break;
            case FRONT_BACK:
                rotationX = 180F - rotationX;
                break;
            default: break;
        }

        if(rotationX < 0F)
            rotationX += 360F;
    }

    @Override
    public void commitRedirection() {
        checkForReceiver();
    }

    @Override
    public void setCanShoot(boolean canShoot) {
        canShootBurst = canShoot;
    }

    @Override
    public int getBurstParticleTick() {
        return burstParticleTick;
    }

    @Override
    public void setBurstParticleTick(int i) {
        burstParticleTick = i;
    }

    @Override
    public int getLastBurstDeathTick() {
        return lastBurstDeathTick;
    }

    @Override
    public void setLastBurstDeathTick(int i) {
        lastBurstDeathTick = i;
    }

    @Override
    public void pingback(IManaBurst burst, UUID expectedIdentity) {
        if(getIdentifier().equals(expectedIdentity)) {
            pingbackTicks = TICKS_ALLOWED_WITHOUT_PINGBACK;
            Entity e = (Entity) burst;
            lastPingbackX = e.posX;
            lastPingbackY = e.posY;
            lastPingbackZ = e.posZ;
            setCanShoot(false);
        }
    }

    @Override
    public UUID getIdentifier() {
        if(identity == null)
            identity = UUID.randomUUID();
        return identity;
    }

    private UUID getIdentifierUnsafe() {
        return identity;
    }

}
