package net.dannyfather.mca_butchery.event;

import com.mojang.blaze3d.platform.NativeImage;
import net.conczin.mca.client.resources.SkinExporter;
import net.conczin.mca.client.resources.SkinLocations;
import net.conczin.mca.entity.VillagerEntityMCA;
import net.conczin.mca.entity.ZombieVillagerEntityMCA;
import net.conczin.mca.entity.ai.Traits;
import net.dannyfather.mca_butchery.MCAButchery;
import net.dannyfather.mca_butchery.block.MCAButcheryBlocks;
import net.dannyfather.mca_butchery.block.MCAVillagerCorpseBlock;
import net.dannyfather.mca_butchery.block.MCAVillagerHeadBlock;
import net.dannyfather.mca_butchery.block.entity.MCAVillagerCorpseBlockEntity;
import net.dannyfather.mca_butchery.client.ClientSkinCache;
import net.dannyfather.mca_butchery.client.CorpseTexture;
import net.dannyfather.mca_butchery.item.MCAButcheryItems;
import net.dannyfather.mca_butchery.network.DownloadVillagerSkinPayload;
import net.dannyfather.mca_butchery.network.MCAButcheryNetwork;
import net.dannyfather.mca_butchery.network.RequestVillagerSkinPayload;
import net.dannyfather.mca_butchery.network.UploadVillagerSkinPayload;
import net.mcreator.butchery.configuration.ButcheryconfigConfiguration;
import net.mcreator.butchery.init.ButcheryModConfigs;
import net.mcreator.butchery.init.ButcheryModItems;
import net.mcreator.butchery.init.ButcheryModTabs;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Containers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@EventBusSubscriber(modid = MCAButchery.MOD_ID)
public class MCAButcheryEvents {
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event){
        LivingEntity entity = event.getEntity();
        DamageSource damageSource = event.getSource();
        if (damageSource.getWeaponItem() != null) {
            ItemStack weapon = damageSource.getWeaponItem();
            if (entity.level() instanceof ServerLevel serverLevel) {
                if (weapon.is(ItemTags.create(ResourceLocation.parse("c:cleaver")))) {
                    if(!entity.isBaby()) {
                        if (entity instanceof VillagerEntityMCA villagerEntityMCA) {
                            int entityId = villagerEntityMCA.getId();
                            ServerPlayer player = serverLevel.getRandomPlayer();
                            if (player != null) {
                                PacketDistributor.sendToPlayer(player, new DownloadVillagerSkinPayload(entityId));
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
                            Containers.dropItemStack(serverLevel, pos.getX(), pos.getY(), pos.getZ(), item);
                        } else if (entity instanceof Villager villager) {
                            BlockPos pos = villager.blockPosition();
                            ItemStack item = ButcheryModItems.VILLAGER_CORPSE.toStack();
                            Containers.dropItemStack(serverLevel, pos.getX(), pos.getY(), pos.getZ(), item);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == ButcheryModTabs.BUTCHERYCARCASSES.getKey()) {
            event.accept(MCAButcheryItems.MCAVILLAGERCORPSE);
            event.accept(MCAButcheryItems.DRAINEDMCAVILLAGERCORPSE);
        }
    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        ButcheryconfigConfiguration.VILLAGER_CORPSE.set(false);
    }

}
