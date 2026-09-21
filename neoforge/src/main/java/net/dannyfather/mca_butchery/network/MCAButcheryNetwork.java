package net.dannyfather.mca_butchery.network;

import com.mojang.blaze3d.platform.NativeImage;
import net.dannyfather.mca_butchery.MCAButchery;
import net.dannyfather.mca_butchery.client.ClientSkinCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
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
import java.util.UUID;

@EventBusSubscriber(modid = MCAButchery.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class MCAButcheryNetwork {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                RequestVillagerSkinPayload.TYPE,
                RequestVillagerSkinPayload.STREAM_CODEC,
                MCAButcheryNetwork::handleSkinRequest
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
    public static void handleVillagerSkin(VillagerSkinPayload payload,IPayloadContext context) {
        context.enqueueWork(() -> {
            try {
                NativeImage image = NativeImage.read(new ByteArrayInputStream(payload.data()));
                DynamicTexture texture = new DynamicTexture(image);
                ResourceLocation location = ResourceLocation.fromNamespaceAndPath("mca_butchery","villager_skin/" + payload.uuid());

                Minecraft.getInstance().getTextureManager().register(location, texture);
                ClientSkinCache.put(payload.uuid(), location, image);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void sendVillagerSkin(ServerPlayer player, UUID uuid) {
        try {
            Path skinFile = player.getServer().getWorldPath(LevelResource.ROOT)
                    .resolve("data").resolve(MCAButchery.MOD_ID).resolve("skins").resolve(uuid.toString() + ".png");
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
