package net.dannyfather.mca_butchery.network;

import io.netty.buffer.ByteBuf;
import net.dannyfather.mca_butchery.MCAButchery;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record VillagerSlimPayload(int entityId) implements CustomPacketPayload {
    public static final Type<VillagerSlimPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID,"villager_slim"));

    public static final StreamCodec<ByteBuf, VillagerSlimPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            VillagerSlimPayload::entityId,
            VillagerSlimPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
