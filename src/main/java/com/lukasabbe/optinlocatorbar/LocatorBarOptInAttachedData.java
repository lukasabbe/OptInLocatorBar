package com.lukasabbe.optinlocatorbar;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record LocatorBarOptInAttachedData(Map<String, Boolean> playerOptInData) {

    static Codec<Map<String, Boolean>> saveCodec = Codec.unboundedMap(Codec.STRING, Codec.BOOL);

    public static Codec<LocatorBarOptInAttachedData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    saveCodec
                            .fieldOf("optinmap")
                            .forGetter(LocatorBarOptInAttachedData::playerOptInData))
                    .apply(instance, LocatorBarOptInAttachedData::new)
    );
    public static LocatorBarOptInAttachedData DEFAULT = new LocatorBarOptInAttachedData(new HashMap<>());

    public LocatorBarOptInAttachedData setValue(UUID player, boolean value){
        Map<String, Boolean> newMap = new HashMap<>(playerOptInData);
        newMap.put(player.toString(), value);
        return new LocatorBarOptInAttachedData(Map.copyOf(newMap));
    }

    public boolean getValue(UUID player){
        return playerOptInData.getOrDefault(player.toString(), true);
    }

    public LocatorBarOptInAttachedData clear(){
        return DEFAULT;
    }
}
