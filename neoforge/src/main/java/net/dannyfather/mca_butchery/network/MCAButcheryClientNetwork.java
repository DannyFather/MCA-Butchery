package net.dannyfather.mca_butchery.network;

import com.mojang.blaze3d.platform.NativeImage;
import net.conczin.mca.client.resources.SkinExporter;
import net.conczin.mca.entity.VillagerEntityMCA;
import net.conczin.mca.entity.VillagerLike;
import net.conczin.mca.entity.ai.Genetics;
import net.dannyfather.mca_butchery.MCAButchery;
import net.dannyfather.mca_butchery.client.ClientSkinCache;
import net.dannyfather.mca_butchery.client.CorpseTexture;
import net.dannyfather.mca_butchery.config.MCAButcheryCommonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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

        registrar.playToClient(
                DownloadVillagerSkinPayload.TYPE,
                DownloadVillagerSkinPayload.STREAM_CODEC,
                MCAButcheryClientNetwork::handleSkinDownload
        );
    }

    public static void handleSkinRequest(RequestVillagerSkinPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if(context.player() instanceof ServerPlayer player){
                MCAButcheryNetwork.sendVillagerSkin(player,payload.uuid());
            }

        });
    }

    private static void handleSkinDownload(DownloadVillagerSkinPayload payload, IPayloadContext context) {
        context.enqueueWork(()->{
            ClientLevel level = Minecraft.getInstance().level;
            if(level != null) {
                Entity entity = level.getEntity(payload.entityId());
                if(entity instanceof VillagerEntityMCA villagerEntityMCA) {
                    String clothesVariant = villagerEntityMCA.isBurned() ? "burned" : "normal";
                    if (MCAButcheryCommonConfig.CLOSED_EYES.get()) {
                        villagerEntityMCA.getGenetics().setGene(Genetics.FACE,0.96f);
                    }
                    try (NativeImage image = SkinExporter.createSkin(villagerEntityMCA,clothesVariant)){
                        byte[] data = image.asByteArray();
                        PacketDistributor.sendToServer(new UploadVillagerSkinPayload(data, villagerEntityMCA.getUUID()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else if (entity instanceof AbstractClientPlayer player) {
                    if (payload.model() == 1 || payload.model() == 2) {
                        PlayerSkin playerSkin = player.getSkin();
                        ResourceLocation skinLocation = playerSkin.texture();
                        try (NativeImage skinImage = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(skinLocation).open())) {
                            PacketDistributor.sendToServer(new UploadVillagerSkinPayload(skinImage.asByteArray(), player.getUUID()));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
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

    private static NativeImage blendEyes(NativeImage base,NativeImage overlay) {
        int width = base.getWidth();
        int height = base.getHeight();
        NativeImage result = new NativeImage(width, height, true);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int baseColor = base.getPixelRGBA(x,y);
                int overlayColor = overlay.getPixelRGBA(x,y);

                int baseA = (baseColor >> 24) & 0xFF;
                int baseB = (baseColor >> 16) & 0xFF;
                int baseG = (baseColor >> 8) & 0xFF;
                int baseR = baseColor & 0xFF;

                int overlayA = (overlayColor >> 24) & 0xFF;
                int overlayB = (overlayColor >> 16) & 0xFF;
                int overlayG = (overlayColor >> 8) & 0xFF;
                int overlayR = overlayColor & 0xFF;

                float ratio = 0.7f;

                if(overlayA == 0) {
                    result.setPixelRGBA(x,y,baseA << 24| baseB << 16 | baseG << 8 | baseR );
                } else {
                    result.setPixelRGBA(x, y, baseA << 24 | (int) (baseB * (1 - ratio) + overlayB * ratio) << 16 | (int) (baseG * (1 - ratio) + overlayG * ratio) << 8 | (int) (baseR * (1 - ratio) + overlayR * ratio));
                }

            }
        }
        return result;
    }
}
