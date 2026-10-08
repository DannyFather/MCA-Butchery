package net.dannyfather.mca_butchery.network;

import io.netty.buffer.ByteBuf;
import net.dannyfather.mca_butchery.MCAButchery;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record DownloadVillagerSkinPayload(int entityId, int model) implements CustomPacketPayload {
    public static final Type<DownloadVillagerSkinPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID,"villager_skin_download"));

    public static final StreamCodec<ByteBuf, DownloadVillagerSkinPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT,
                    DownloadVillagerSkinPayload::entityId,
                    ByteBufCodecs.INT,
                    DownloadVillagerSkinPayload::model,
                    DownloadVillagerSkinPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
