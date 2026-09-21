package net.dannyfather.mca_butchery.network;

import io.netty.buffer.ByteBuf;
import net.dannyfather.mca_butchery.MCAButchery;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record VillagerSkinPayload(UUID uuid, byte[] data) implements CustomPacketPayload {
    public static final Type<VillagerSkinPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID,"villager_skin"));

    public static final StreamCodec<ByteBuf, VillagerSkinPayload> STREAM_CODEC = StreamCodec.composite(UUIDUtil.STREAM_CODEC, VillagerSkinPayload::uuid, ByteBufCodecs.BYTE_ARRAY,VillagerSkinPayload::data,VillagerSkinPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
