package net.dannyfather.mca_butchery.event;

import com.mojang.blaze3d.platform.NativeImage;
import net.conczin.mca.MCA;
import net.conczin.mca.client.resources.SkinExporter;
import net.conczin.mca.entity.VillagerEntityMCA;
import net.dannyfather.mca_butchery.MCAButchery;
import net.dannyfather.mca_butchery.client.ClientSkinCache;
import net.dannyfather.mca_butchery.client.CorpseTexture;
import net.dannyfather.mca_butchery.item.MCAButcheryItems;
import net.dannyfather.mca_butchery.network.MCAButcheryClientNetwork;
import net.dannyfather.mca_butchery.network.MCAButcheryNetwork;
import net.dannyfather.mca_butchery.network.RequestVillagerSkinPayload;
import net.dannyfather.mca_butchery.network.UploadVillagerSkinPayload;
import net.mcreator.butchery.configuration.ButcheryconfigConfiguration;
import net.mcreator.butchery.init.ButcheryModItems;
import net.mcreator.butchery.init.ButcheryModTabs;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.io.IOException;

@EventBusSubscriber(modid = MCAButchery.MOD_ID, bus = EventBusSubscriber.Bus.GAME,value = Dist.CLIENT)
public class MCAButcheryClientEvents {
    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.hasSingleplayerServer()) {
            return;
        }

        if (event.getTarget() instanceof VillagerEntityMCA villagerEntityMCA) {
            MCAButcheryClientEvents.saveVillagerSkin(villagerEntityMCA);
        }

    }

    @SubscribeEvent
    public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.hasSingleplayerServer()) {
            return;
        }

        if (event.getTarget() instanceof VillagerEntityMCA villagerEntityMCA) {
            MCAButcheryClientEvents.saveVillagerSkin(villagerEntityMCA);
        }

    }

    public static void saveVillagerSkin(VillagerEntityMCA villagerEntityMCA) {
        if (!villagerEntityMCA.isBaby()) {
            String clothesVariant = villagerEntityMCA.isBurned() ? "burnt" : "normal";
            try (NativeImage image = SkinExporter.createSkin(villagerEntityMCA, clothesVariant)) {
                byte[] data = image.asByteArray();
                PacketDistributor.sendToServer(new UploadVillagerSkinPayload(data, villagerEntityMCA.getUUID()));
            } catch (Exception e) {
                MCAButchery.LOGGER.error("Failed to export villager skin", e);
            }
        }
    }


    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event){
        LivingEntity entity = event.getEntity();
        DamageSource damageSource = event.getSource();
        if (damageSource.getEntity() instanceof LivingEntity livingEntity) {
            ItemStack weapon = livingEntity.getWeaponItem();
            if (weapon.is(ItemTags.create(ResourceLocation.parse("c:cleaver")))) {
                if (entity instanceof VillagerEntityMCA villagerEntityMCA) {
                    MCAButcheryClientEvents.saveVillagerSkin(villagerEntityMCA);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientSkinCache.clear();
    }

}
