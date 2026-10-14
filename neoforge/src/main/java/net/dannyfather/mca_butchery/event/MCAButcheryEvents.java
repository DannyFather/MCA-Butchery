package net.dannyfather.mca_butchery.event;

import de.bene2212.holdmyitemsnf.config.HoldMyItemsClientConfig;
import net.conczin.mca.entity.VillagerEntityMCA;
import net.conczin.mca.entity.VillagerLike;
import net.conczin.mca.entity.ai.Genetics;
import net.conczin.mca.entity.ai.Traits;
import net.conczin.mca.registry.EntitiesMCA;
import net.conczin.mca.server.world.data.FamilyTree;
import net.conczin.mca.server.world.data.PlayerSaveData;
import net.dannyfather.mca_butchery.MCAButchery;
import net.dannyfather.mca_butchery.block.MCAButcheryBlocks;
import net.dannyfather.mca_butchery.config.MCAButcheryCommonConfig;
import net.dannyfather.mca_butchery.item.MCAButcheryItems;
import net.dannyfather.mca_butchery.network.DownloadVillagerSkinPayload;
import net.dannyfather.mca_butchery.network.MCAButcheryClientNetwork;
import net.dannyfather.mca_butchery.network.MCAButcheryNetwork;
import net.dannyfather.mca_butchery.network.VillagerSlimPayload;
import net.mcreator.butchery.configuration.ButcheryconfigConfiguration;
import net.mcreator.butchery.init.ButcheryModItems;
import net.mcreator.butchery.init.ButcheryModTabs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static net.dannyfather.mca_butchery.MCAButchery.LOGGER;


@EventBusSubscriber(modid = MCAButchery.MOD_ID)
public class MCAButcheryEvents {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityDeath(LivingDeathEvent event){
        LivingEntity entity = event.getEntity();
        DamageSource damageSource = event.getSource();
        if (damageSource.getWeaponItem() != null) {
            ItemStack weapon = damageSource.getWeaponItem();
            if (entity.level() instanceof ServerLevel serverLevel) {
                if (weapon.is(ItemTags.create(ResourceLocation.parse("c:cleaver")))) {
                    if(!entity.isBaby()) {
                        switch (entity) {
                            case VillagerEntityMCA villagerEntityMCA -> {
                                int entityId = villagerEntityMCA.getId();
                                ServerPlayer player = serverLevel.getRandomPlayer();
                                if (player != null) {
                                    PacketDistributor.sendToPlayer(player, new DownloadVillagerSkinPayload(entityId,0));
                                }

                                BlockPos pos = villagerEntityMCA.blockPosition();
                                ItemStack item = MCAButcheryItems.MCAVILLAGERCORPSE.toStack();
                                String villagerName = Component.translatable("block.mca_butchery.villager_corpse_named").getString().replace("{VILLAGERNAME}", villagerEntityMCA.getName().getString());
                                item.set(DataComponents.CUSTOM_NAME, Component.literal(villagerName).withStyle(style -> style.withItalic(false)));
                                item.set(MCAButcheryItems.VILLAGER_UUID, villagerEntityMCA.getUUID());
                                item.set(MCAButcheryItems.B_SIZE, villagerEntityMCA.getGenetics().getBreastSize());
                                Traits villagerTraits = villagerEntityMCA.getTraits();
                                float hScale = (villagerTraits.hasTrait(Traits.DWARFISM) ? 0.65f : 1.0f) * villagerEntityMCA.getGenetics().getVerticalScaleFactor();
                                float wScale = (villagerEntityMCA.getTraits().hasTrait(Traits.DWARFISM) ? 0.85f : 1.0f)
                                        * (villagerTraits.hasTrait(Traits.TOUGH) ? 1.2F : 1.0F)
                                        * (villagerTraits.hasTrait(Traits.WEAK) ? 0.85F : 1.0F) * villagerEntityMCA.getGenetics().getHorizontalScaleFactor();
                                item.set(MCAButcheryItems.H_SIZE, hScale);
                                item.set(MCAButcheryItems.W_SIZE, wScale);
                                int face = (int) Math.floor( (double) villagerEntityMCA.getGenetics().getGene(Genetics.FACE) * 12);
                                switch (face) {
                                    case 0,1,2,3,4,5,6 -> {
                                        face = 2;
                                    }
                                    case 7,8,10 -> {
                                        face = 1;
                                    }
                                    default -> {
                                        face = 0;
                                    }

                                }
                                if(MCAButcheryCommonConfig.CLOSED_EYES.get()) {
                                    face = 0;
                                }
                                item.set(MCAButcheryItems.FACE, face);
                                Containers.dropItemStack(serverLevel, pos.getX(), pos.getY(), pos.getZ(), item);
                            }
                            case Villager villager when MCAButcheryCommonConfig.VANILLA_VILLAGER_CORPSE.get() -> {
                                BlockPos pos = villager.blockPosition();
                                ItemStack item = ButcheryModItems.VILLAGER_CORPSE.toStack();
                                Containers.dropItemStack(serverLevel, pos.getX(), pos.getY(), pos.getZ(), item);
                            }
                            case ServerPlayer player -> {
                                if (!ModList.get().isLoaded("mca_descendants")) {
                                    if (MCAButcheryCommonConfig.PLAYER_CORPSE.get()) {
                                        ItemStack item = ButcheryModItems.PLAYERCORPSE.toStack();
                                        BlockPos pos = player.blockPosition();
                                        Containers.dropItemStack(serverLevel, pos.getX(), pos.getY(), pos.getZ(), item);
                                    } else {
                                        int entityId = player.getId();
                                        int model = PlayerSaveData.get(player).getEntityData().getInt("PlayerModel");

                                        BlockPos pos = player.blockPosition();
                                        ItemStack item = MCAButcheryItems.MCAVILLAGERCORPSE.toStack();
                                        if (model == 0) {
                                            CompoundTag pData = new CompoundTag();
                                            VillagerLike.toVillager(player).asEntity().save(pData);
                                            UUID vUUID = UUID.randomUUID();
                                            pData.remove("UUID");
                                            pData.putUUID("UUID", vUUID);
                                            pData.remove("id");
                                            pData.remove("tree_mother_name");
                                            pData.remove("tree_father_name");
                                            pData.remove("tree_spouse_name");
                                            pData.remove("tree_mother_UUID");
                                            pData.remove("tree_father_UUID");
                                            pData.remove("tree_spouse_UUID");
                                            VillagerEntityMCA pVillager = EntitiesMCA.FEMALE_VILLAGER.create(serverLevel);
                                            if (pData.getInt("gender") == 1) {
                                                pVillager = EntitiesMCA.MALE_VILLAGER.create(serverLevel);
                                            }
                                            pVillager.load(pData);
                                            pVillager.setCustomName(Component.literal(FamilyTree.get(serverLevel).getOrCreate(player).getName()));
                                            pVillager.setCustomNameVisible(true);
                                            pVillager.moveTo(player.position());
                                            serverLevel.addFreshEntity(pVillager);
                                            int face = (int) Math.floor((double) pVillager.getGenetics().getGene(Genetics.FACE) * 12);
                                            switch (face) {
                                                case 0, 1, 2, 3, 4, 5, 6 -> {
                                                    face = 2;
                                                }
                                                case 7, 8, 10 -> {
                                                    face = 1;
                                                }
                                                default -> {
                                                    face = 0;
                                                }

                                            }
                                            if (MCAButcheryCommonConfig.CLOSED_EYES.get()) {
                                                face = 0;
                                            }
                                            item.set(MCAButcheryItems.FACE, face);
                                            PacketDistributor.sendToPlayer(player, new DownloadVillagerSkinPayload(pVillager.getId(), 0));
                                            item.set(MCAButcheryItems.VILLAGER_UUID, vUUID);
                                            pVillager.discard();
                                        } else {
                                            PacketDistributor.sendToPlayer(player, new DownloadVillagerSkinPayload(entityId, model));
                                            item.set(MCAButcheryItems.VILLAGER_UUID, player.getUUID());
                                            boolean isSlim = player.getPersistentData().getBoolean("Slim");
                                            item.set(MCAButcheryItems.SLIM, isSlim);
                                        }
                                        FamilyTree tree = FamilyTree.get(serverLevel);
                                        String playerName = player.getScoreboardName();
                                        if (tree.getOrEmpty(player.getUUID()).isPresent()) {
                                            playerName = tree.getOrEmpty(player.getUUID()).get().getName();
                                        }
                                        String itemName = Component.translatable("block.mca_butchery.villager_corpse_named").getString().replace("{VILLAGERNAME}", playerName);
                                        item.set(DataComponents.CUSTOM_NAME, Component.literal(itemName).withStyle(style -> style.withItalic(false)));
                                        if (model != 2) {
                                            Genetics genetics = VillagerLike.toVillager(player).getGenetics();
                                            item.set(MCAButcheryItems.B_SIZE, genetics.getBreastSize());
                                            Traits villagerTraits = VillagerLike.toVillager(player).getTraits();
                                            float hScale = (villagerTraits.hasTrait(Traits.DWARFISM) ? 0.65f : 1.0f) * genetics.getVerticalScaleFactor();
                                            float wScale = (villagerTraits.hasTrait(Traits.DWARFISM) ? 0.85f : 1.0f)
                                                    * (villagerTraits.hasTrait(Traits.TOUGH) ? 1.2F : 1.0F)
                                                    * (villagerTraits.hasTrait(Traits.WEAK) ? 0.85F : 1.0F) * genetics.getHorizontalScaleFactor();
                                            item.set(MCAButcheryItems.H_SIZE, hScale);
                                            item.set(MCAButcheryItems.W_SIZE, wScale);
                                        }
                                        Containers.dropItemStack(serverLevel, pos.getX(), pos.getY(), pos.getZ(), item);
                                    }
                                    if (!serverLevel.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
                                        player.getInventory().dropAll();
                                        int xp = player.getExperienceReward(serverLevel, event.getSource().getEntity());
                                        if (xp > 0) {
                                            ExperienceOrb.award(serverLevel, player.blockPosition().getCenter(), xp);
                                        }
                                    }
                                }
                            }
                            default -> {
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void  fallEvent(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();
        int x = Mth.floor(entity.getX());
        int y = Mth.floor(entity.getY()-0.2D);
        int z = Mth.floor(entity.getZ());
        BlockPos landingPos = new BlockPos(x,y,z);
        BlockState blockState = entity.level().getBlockState(landingPos);
        if(blockState.is(MCAButcheryBlocks.IRON_SPIKE.get())) {
            float dist = event.getDistance();
            event.setDistance(2f * dist);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if(player instanceof ServerPlayer serverPlayer) {
            int entityId = serverPlayer.getId();
            PacketDistributor.sendToPlayer(serverPlayer, new VillagerSlimPayload(entityId));
        }
    }

    @SubscribeEvent
    public static void tickEvent(EntityTickEvent.Post event) {
        if(event.getEntity() instanceof ServerPlayer player) {
            int entityId = player.getId();
            if (player.tickCount % 80 == 0) {
                PacketDistributor.sendToPlayer(player, new VillagerSlimPayload(entityId));
            }
        }
    }

    @SubscribeEvent
    public static void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == ButcheryModTabs.BUTCHERYCARCASSES.getKey()) {
            event.accept(MCAButcheryItems.MCAVILLAGERCORPSE);
            event.accept(MCAButcheryItems.DRAINEDMCAVILLAGERCORPSE);
            event.accept(MCAButcheryItems.MCAVILLAGERHEAD);
        }
        if (event.getTabKey() == ButcheryModTabs.BUTCHERY_BLOCKS.getKey()) {
            event.accept(MCAButcheryItems.IRON_SPIKE);
        }
    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        ButcheryconfigConfiguration.VILLAGER_CORPSE.set(false);
        ButcheryconfigConfiguration.PLAYER_CORPSE.set(false);
    }

}
