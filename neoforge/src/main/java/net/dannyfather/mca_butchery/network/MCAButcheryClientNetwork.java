package net.dannyfather.mca_butchery.network;

import com.mojang.blaze3d.platform.NativeImage;
import net.conczin.mca.client.resources.SkinExporter;
import net.conczin.mca.entity.VillagerEntityMCA;
import net.conczin.mca.entity.VillagerLike;
import net.dannyfather.mca_butchery.MCAButchery;
import net.dannyfather.mca_butchery.client.ClientSkinCache;
import net.dannyfather.mca_butchery.client.CorpseTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
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
import java.util.UUID;

@EventBusSubscriber(modid = MCAButchery.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MCAButcheryClientNetwork {
    //this code might look retarded, and it is, but copying functions verbatum from the other class seems to be the only thing that works

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MCAButchery.MOD_ID);

        registrar.playToServer(
                RequestVillagerSkinPayload.TYPE,
                RequestVillagerSkinPayload.STREAM_CODEC,
                MCAButcheryClientNetwork::handleSkinRequest
        );

        registrar.playToServer(
                UploadVillagerSkinPayload.TYPE,
                UploadVillagerSkinPayload.STREAM_CODEC,
                (payload, context) -> {
                    ServerPlayer player = (ServerPlayer) context.player();
                    MCAButcheryClientNetwork.handleSkinUpload(player,payload);
                }
        );

        registrar.playToClient(
                VillagerSkinPayload.TYPE,
                VillagerSkinPayload.STREAM_CODEC,
                MCAButcheryClientNetwork::handleVillagerSkin
        );
    }

    public static void handleSkinRequest(RequestVillagerSkinPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if(context.player() instanceof ServerPlayer player){
                MCAButcheryNetwork.sendVillagerSkin(player,payload.uuid());
            }

        });
    }
    private static void handleVillagerSkin(VillagerSkinPayload payload,IPayloadContext context) {
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

    private static void handleSkinUpload(ServerPlayer player, UploadVillagerSkinPayload payload) {
        MCAButchery.LOGGER.info(
                "RECEIVED SKIN UPLOAD: {} bytes for {} from {}",
                payload.data().length,
                payload.villager(),
                player.getGameProfile().getName()
        );
        MCAButcheryNetwork.saveSkin(payload.data(), payload.villager().toString(), player);
    }

}
