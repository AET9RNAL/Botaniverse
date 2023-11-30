package com.aeternal.botaniverse.common.entity.sparks;

import baubles.api.BaublesApi;
import com.aeternal.botaniverse.Config;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import com.aeternal.botaniverse.api.mana.spark.AdditionalSparkHelper;
import com.aeternal.botaniverse.common.item.ModVItems;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;
import vazkii.botania.api.mana.spark.SparkUpgradeType;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.network.PacketBotaniaEffect;
import vazkii.botania.common.network.PacketHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import static com.aeternal.botaniverse.common.item.ModVItems.spark_alfheim;

public class EntitySparkAlfheim extends Entity implements ISparkEntity {

    private static final int TRANSFER_RATE_ALFHEIM = Config.SparkAlfheimConductivity;
    private static final String TAG_UPGRADE = "upgrade";
    private static final String TAG_INVIS = "invis";
    private static final DataParameter<Integer> UPGRADE = EntityDataManager.createKey(EntitySparkAlfheim.class, DataSerializers.VARINT);

    private final Set<ISparkEntity> transfers = Collections.newSetFromMap(new WeakHashMap<>());

    private int removeTransferants = 2;

    public EntitySparkAlfheim(World world) {
        super(world);
        isImmuneToFire = true;
    }

    @Override
    protected void entityInit() {
        setSize(0.1F, 0.5F);
        dataManager.register(UPGRADE, 0);
    }

    @Nonnull
    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return new ItemStack(spark_alfheim);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if(world.isRemote)
            return;

        ISparkAttachable tile = getAttachedTile();
        if(tile == null) {
            dropAndKill();
            return;
        }

        SparkUpgradeType upgrade = getUpgrade();
        List<ISparkEntity> allSparks = null;
        if(upgrade == SparkUpgradeType.DOMINANT || upgrade == SparkUpgradeType.RECESSIVE)
            allSparks = AdditionalSparkHelper.getSparksAroundAlfheim(world, posX, posY + (height / 2.0), posZ);

        Collection<ISparkEntity> transfers = getTransfers();

        switch(upgrade) {
            case DISPERSIVE : {
                List<EntityPlayer> players = AdditionalSparkHelper.getEntitiesAroundAlfheim(EntityPlayer.class, world, posX, posY + (height / 2.0), posZ);

                Map<EntityPlayer, TObjectIntHashMap<ItemStack>> receivingPlayers = new HashMap<>();

                ItemStack input = new ItemStack(spark_alfheim);
                for(EntityPlayer player : players) {
                    List<ItemStack> stacks = new ArrayList<>();
                    stacks.addAll(player.inventory.mainInventory);
                    stacks.addAll(player.inventory.armorInventory);

                    IItemHandler baubles = BaublesApi.getBaublesHandler(player);
                    for (int i = 0; i < baubles.getSlots(); i++)
                        stacks.add(baubles.getStackInSlot(i));

                    for(ItemStack stack : stacks) {
                        if(stack.isEmpty() || !(stack.getItem() instanceof IManaItem))
                            continue;

                        IManaItem manaItem = (IManaItem) stack.getItem();
                        if(manaItem.canReceiveManaFromItem(stack, input)) {
                            TObjectIntHashMap<ItemStack> receivingStacks;
                            boolean add = false;
                            if(!receivingPlayers.containsKey(player)) {
                                add = true;
                                receivingStacks = new TObjectIntHashMap<>();
                            } else receivingStacks = receivingPlayers.get(player);

                            int recv = Math.min(getAttachedTile().getCurrentMana(), Math.min(TRANSFER_RATE_ALFHEIM, manaItem.getMaxMana(stack) - manaItem.getMana(stack)));
                            if(recv > 0) {
                                receivingStacks.put(stack, recv);
                                if(add)
                                    receivingPlayers.put(player, receivingStacks);
                            }
                        }
                    }
                }

                if(!receivingPlayers.isEmpty()) {
                    List<EntityPlayer> keys = new ArrayList<>(receivingPlayers.keySet());
                    Collections.shuffle(keys);
                    EntityPlayer player = keys.iterator().next();

                    TObjectIntHashMap<ItemStack> items = receivingPlayers.get(player);
                    ItemStack stack = items.keySet().iterator().next();
                    int cost = items.get(stack);
                    int manaToPut = Math.min(getAttachedTile().getCurrentMana(), cost);
                    ((IManaItem) stack.getItem()).addMana(stack, manaToPut);
                    getAttachedTile().recieveMana(-manaToPut);
                    particlesTowards(player);
                }

                break;
            }
            case DOMINANT : {
                List<ISparkEntity> validSparks = new ArrayList<>();
                for(ISparkEntity spark_alfheim : allSparks) {
                    if(spark_alfheim == this)
                        continue;

                    SparkUpgradeType upgrade_ = spark_alfheim.getUpgrade();
                    if(upgrade_ == SparkUpgradeType.NONE && spark_alfheim.getAttachedTile() instanceof IManaPool)
                        validSparks.add(spark_alfheim);
                }
                if(validSparks.size() > 0)
                    validSparks.get(world.rand.nextInt(validSparks.size())).registerTransfer(this);

                break;
            }
            case RECESSIVE : {
                for(ISparkEntity spark_alfheim : allSparks) {
                    if(spark_alfheim == this)
                        continue;

                    SparkUpgradeType upgrade_ = spark_alfheim.getUpgrade();
                    if(upgrade_ != SparkUpgradeType.DOMINANT && upgrade_ != SparkUpgradeType.RECESSIVE && upgrade_ != SparkUpgradeType.ISOLATED)
                        transfers.add(spark_alfheim);
                }
                break;
            }
            case NONE:
            default: break;
        }

        if(!transfers.isEmpty()) {
            int manaTotal = Math.min(TRANSFER_RATE_ALFHEIM * transfers.size(), tile.getCurrentMana());
            int manaForEach = manaTotal / transfers.size();
            int manaSpent = 0;

            if(manaForEach > transfers.size()) {
                for(ISparkEntity spark_alfheim : transfers) {
                    if(spark_alfheim.getAttachedTile() == null || spark_alfheim.getAttachedTile().isFull() || spark_alfheim.areIncomingTransfersDone()) {
                        manaTotal -= manaForEach;
                        continue;
                    }

                    ISparkAttachable attached = spark_alfheim.getAttachedTile();
                    int spend = Math.min(attached.getAvailableSpaceForMana(), manaForEach);
                    attached.recieveMana(spend);
                    manaSpent += spend;

                    particlesTowards((Entity) spark_alfheim);
                }
                tile.recieveMana(-manaSpent);
            }
        }

        if(removeTransferants > 0)
            removeTransferants--;
        filterTransfers();
    }

    private void particlesTowards(Entity e) {
        PacketHandler.sendToNearby(world, this,
                new PacketBotaniaEffect(PacketBotaniaEffect.EffectType.SPARK_MANA_FLOW, posX, posY, posZ,
                        getEntityId(), e.getEntityId()));
    }

    public static void particleBeam(EntityPlayer player, Entity e1, Entity e2) {
        if(e1 != null && e2 != null && !e1.world.isRemote) {
            PacketHandler.sendTo((EntityPlayerMP) player,
                    new PacketBotaniaEffect(PacketBotaniaEffect.EffectType.SPARK_NET_INDICATOR, e1.posX, e1.posY, e1.posZ,
                            e1.getEntityId(), e2.getEntityId()));
        }
    }

    private void dropAndKill() {
        SparkUpgradeType upgrade = getUpgrade();
        entityDropItem(new ItemStack(ModVItems.spark_alfheim), 0F);
        if(upgrade !=  SparkUpgradeType.NONE)
            entityDropItem(new ItemStack(ModItems.sparkUpgrade, 1, upgrade.ordinal() - 1), 0F);
        setDead();
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if(!isDead && !stack.isEmpty()) {
            if(world.isRemote) {
                boolean valid = stack.getItem() == ModItems.twigWand || stack.getItem() == ModItems.sparkUpgrade
                        || stack.getItem() == ModItems.phantomInk;
                if(valid)
                    player.swingArm(hand);
                return valid;
            }

            SparkUpgradeType upgrade = getUpgrade();
            if(stack.getItem() == ModItems.twigWand) {
                if(player.isSneaking()) {
                    if(upgrade != SparkUpgradeType.NONE) {
                        entityDropItem(new ItemStack(ModItems.sparkUpgrade, 1, upgrade.ordinal() - 1), 0F);
                        setUpgrade(SparkUpgradeType.NONE);

                        transfers.clear();
                        removeTransferants = 2;
                    } else dropAndKill();
                    return true;
                } else {
                    for(ISparkEntity spark_alfheim : AdditionalSparkHelper.getSparksAroundAlfheim(world, posX, posY + (height / 2.0), posZ))
                        particleBeam(player, this, (Entity) spark_alfheim);
                    return true;
                }
            } else if(stack.getItem() == ModItems.sparkUpgrade && upgrade == SparkUpgradeType.NONE) {
                int newUpgrade = stack.getItemDamage() + 1;
                setUpgrade(SparkUpgradeType.values()[newUpgrade]);
                stack.shrink(1);
                return true;
            } else if (stack.getItem() == ModItems.phantomInk) {
                setInvisible(true);
                return true;
            }
        }

        return false;
    }

    @Override
    protected void readEntityFromNBT(@Nonnull NBTTagCompound cmp) {
        setUpgrade(SparkUpgradeType.values()[cmp.getInteger(TAG_UPGRADE)]);
        setInvisible(cmp.getInteger(TAG_INVIS) == 1);
    }

    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound cmp) {
        cmp.setInteger(TAG_UPGRADE, getUpgrade().ordinal());
        cmp.setInteger(TAG_INVIS, isInvisible() ? 1 : 0);
    }

    @Override
    public ISparkAttachable getAttachedTile() {
        int x = MathHelper.floor(posX);
        int y = MathHelper.floor(posY) - 1;
        int z = MathHelper.floor(posZ);
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        if(tile != null && tile instanceof ISparkAttachable)
            return (ISparkAttachable) tile;

        return null;
    }

    private void filterTransfers() {
        Iterator<ISparkEntity> iter = transfers.iterator();
        while (iter.hasNext()) {
            ISparkEntity spark_alfheim = iter.next();
            SparkUpgradeType upgr = getUpgrade();
            SparkUpgradeType supgr = spark_alfheim.getUpgrade();
            ISparkAttachable atile = spark_alfheim.getAttachedTile();

            if(!(spark_alfheim != this
                    && !spark_alfheim.areIncomingTransfersDone()
                    && atile != null && !atile.isFull()
                    && (upgr == SparkUpgradeType.NONE && supgr == SparkUpgradeType.DOMINANT || upgr == SparkUpgradeType.RECESSIVE && (supgr == SparkUpgradeType.NONE || supgr == SparkUpgradeType.DISPERSIVE) || !(atile instanceof IManaPool))))
                iter.remove();
        }
    }

    @Override
    public Collection<ISparkEntity> getTransfers() {
        filterTransfers();
        return transfers;
    }

    private boolean hasTransfer(ISparkEntity entity) {
        return transfers.contains(entity);
    }

    @Override
    public void registerTransfer(ISparkEntity entity) {
        if(hasTransfer(entity))
            return;
        transfers.add(entity);
    }

    @Override
    public SparkUpgradeType getUpgrade() {
        return SparkUpgradeType.values()[dataManager.get(UPGRADE)];
    }

    @Override
    public void setUpgrade(SparkUpgradeType upgrade) {
        dataManager.set(UPGRADE, upgrade.ordinal());
    }

    @Override
    public boolean areIncomingTransfersDone() {
        ISparkAttachable tile = getAttachedTile();
        if(tile instanceof IManaPool)
            return removeTransferants > 0;
        return tile != null && tile.areIncomingTranfersDone();
    }

}
