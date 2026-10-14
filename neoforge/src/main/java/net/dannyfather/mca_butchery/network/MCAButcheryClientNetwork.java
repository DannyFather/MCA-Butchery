package net.dannyfather.mca_butchery.network;

import com.mojang.blaze3d.platform.NativeImage;
import net.conczin.mca.client.resources.SkinExporter;
import net.conczin.mca.entity.VillagerEntityMCA;
import net.conczin.mca.entity.ai.Genetics;
import net.dannyfather.mca_butchery.MCAButchery;
import net.dannyfather.mca_butchery.client.ClientSkinCache;
import net.dannyfather.mca_butchery.config.MCAButcheryCommonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

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
                UpdateSlimStatus.TYPE,
                UpdateSlimStatus.STREAM_CODEC,
                MCAButcheryClientNetwork::sendSlimStatus
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

        registrar.playToClient(
                VillagerSlimPayload.TYPE,
                VillagerSlimPayload.STREAM_CODEC,
                MCAButcheryClientNetwork::handleSlim
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
                        Minecraft.getInstance().getSkinManager().getOrLoad(player.getGameProfile())
                                .thenAccept(skin -> {
                                    String url = skin.textureUrl();
                                    if (url == null || url.isBlank()) {
                                        return;
                                    }
                                    CompletableFuture.runAsync(() -> {
                                        try (InputStream in = URI.create(url).toURL().openStream();
                                             NativeImage image = NativeImage.read(in)) {
                                            byte[] png = image.asByteArray();
                                            if(image.getHeight() != 64 || image.getWidth() != 64){
                                                png = to64x64Png(image);
                                            }
                                            byte[] finalPng = png;
                                            Minecraft.getInstance().execute(() -> {
                                                PacketDistributor.sendToServer(new UploadVillagerSkinPayload(finalPng, player.getUUID()));

                                                System.out.println("Uploaded skin PNG: " + finalPng.length + " bytes");
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    });
                                })
                                .exceptionally(error -> {
                                    error.printStackTrace();
                                    return null;
                                });
                    }
                }
            }
        });
    }

    public static byte[] to64x64Png(NativeImage source) throws IOException {
        BufferedImage image = new BufferedImage(
                64, 64, BufferedImage.TYPE_INT_ARGB
        );

        int srcWidth = source.getWidth();
        int srcHeight = source.getHeight();

        for (int y = 0; y < 64; y++) {
            for (int x = 0; x < 64; x++) {
                int srcX = (y < 48 ? x : (y < 52) ? ( (x >= 20 && x < 24) ? (27 - x) : ((x >= 24 && x < 28) ? (35 - x) : ( (x >= 36 && x < 40) ? (83 - x) : ((x >= 40 && x < 44) ? (91 - x) : 16))) )
                        : ( (x >= 16 && x < 28)  ? (27 - x) : ((x >= 28 && x < 32) ? (43 - x) : (x >= 32 && x < 44) ? (83 - x) : ((x >= 44 &  x < 48) ? 99 - x : x))) ) * srcWidth / 64;
                int srcY = y % srcHeight;
                int abgr = source.getPixelRGBA(srcX, srcY);
                int argb = (abgr & 0xFF00FF00) | ((abgr & 0x00FF0000) >> 16) | ((abgr & 0x000000FF) << 16);

                if(y >= srcHeight) {
                    if (y < 48 || (x < 16 || x >= 48)) {
                        argb = 0;
                    }
                }

                image.setRGB(x, y, argb);
            }
        }


        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", out);
        return out.toByteArray();
    }

    private static void handleVillagerSkin(VillagerSkinPayload payload,IPayloadContext context) {
        context.enqueueWork(() -> {
            try {
                NativeImage image = NativeImage.read(new ByteArrayInputStream(payload.data()));
                DynamicTexture texture = new DynamicTexture(image);
                ResourceLocation location = ResourceLocation.fromNamespaceAndPath("mca_butchery","villager_skin/" + payload.uuid());

                Minecraft.getInstance().getTextureManager().register(location, texture);
                ClientSkinCache.put(payload.uuid().toString(), location, image);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void handleSlim(VillagerSlimPayload payload,IPayloadContext context) {
        context.enqueueWork(()-> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                Entity entity = level.getEntity(payload.entityId());
                if (entity instanceof AbstractClientPlayer player) {
                    if (player.getSkin().model() == PlayerSkin.Model.SLIM) {
                        PacketDistributor.sendToServer(new UpdateSlimStatus(true));
                    } else {
                        PacketDistributor.sendToServer(new UpdateSlimStatus(false));
                    }
                }
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

    private static void sendSlimStatus(UpdateSlimStatus payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if(context.player() instanceof ServerPlayer player){
                player.getPersistentData().putBoolean("Slim", payload.slim());
            }

        });
    }
}
