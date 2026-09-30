package net.dannyfather.mca_butchery.network;

import com.mojang.blaze3d.platform.NativeImage;
import net.conczin.mca.client.resources.SkinExporter;
import net.conczin.mca.entity.VillagerLike;
import net.dannyfather.mca_butchery.MCAButchery;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@EventBusSubscriber(modid = MCAButchery.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
public class MCAButcheryNetwork {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MCAButchery.MOD_ID);
        MCAButchery.LOGGER.info("REGISTERING SERVER PAYLOADS");

        registrar.playToServer(
                RequestVillagerSkinPayload.TYPE,
                RequestVillagerSkinPayload.STREAM_CODEC,
                MCAButcheryNetwork::handleSkinRequest
        );

        registrar.playToServer(
                UploadVillagerSkinPayload.TYPE,
                UploadVillagerSkinPayload.STREAM_CODEC,
                (payload, context) -> {
                    ServerPlayer player = (ServerPlayer) context.player();
                    MCAButcheryNetwork.handleSkinUpload(player,payload);
                }
        );

        registrar.playToClient(
                VillagerSkinPayload.TYPE,
                VillagerSkinPayload.STREAM_CODEC,
                MCAButcheryNetwork::handleVillagerSkin
        );
    }

    private static void handleSkinRequest(RequestVillagerSkinPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if(context.player() instanceof ServerPlayer player){
                MCAButcheryNetwork.sendVillagerSkin(player,payload.uuid());
            }

        });
    }

    private static void handleSkinUpload(ServerPlayer player, UploadVillagerSkinPayload payload) {
        MCAButchery.LOGGER.info(
                "RECEIVED SKIN UPLOAD: {} bytes for {} from {}",
                payload.data().length,
                payload.villager(),
                player.getGameProfile().getName()
        );
        saveSkin(payload.data(), payload.villager().toString(), player);
    }

    public static void saveSkin(byte[] baseByte, String customName, ServerPlayer player) {
        Path exportDir = player.server.getWorldPath(LevelResource.ROOT).resolve("data").resolve(MCAButchery.MOD_ID).resolve("skins");
        try {


            if (!Files.exists(exportDir)) {
                Files.createDirectories(exportDir);
            }

            Path skinFile = exportDir.resolve(customName + ".png");
            Files.write(skinFile, baseByte, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            MCAButchery.LOGGER.info(
                    "Saved villager skin to {}",
                    skinFile.toAbsolutePath()
            );
        } catch(Exception e) {
            MCAButchery.LOGGER.error("Failed to export villager skin", e);
        }
    }

    private static void handleVillagerSkin(VillagerSkinPayload payload,IPayloadContext context) {
        context.enqueueWork(() -> {
        });
    }

    public static void sendVillagerSkin(ServerPlayer player, UUID uuid) {
        try {
            Path skinFile = player.server.getWorldPath(LevelResource.ROOT).resolve("data").resolve(MCAButchery.MOD_ID).resolve("skins").resolve(uuid.toString() + ".png");
            byte[] data = Files.readAllBytes(skinFile);

            PacketDistributor.sendToPlayer(player, new VillagerSkinPayload(uuid,data));

        } catch (IOException e) {
            MCAButchery.LOGGER.error( "Failed to read villager skin {}", uuid, e );
        }
    }

    public static void requestVillagerSkin(UUID uuid) {
        PacketDistributor.sendToServer(new RequestVillagerSkinPayload(uuid));
    }
}
