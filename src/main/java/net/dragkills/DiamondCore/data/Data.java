package net.dragkills.DiamondCore.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.nukkitx.nbt.NBTInputStream;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.protocol.bedrock.packet.StartGamePacket;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class Data {

    public static NbtMap BIOMES;

    public static NbtMap ENTITY_IDENTIFIERS;

    public static ArrayList<StartGamePacket.ItemEntry> ITEM_ENTRIES = new ArrayList<>();

    public static void loadBiomeDefinitions() {
        InputStream stream = FileManager.getFileResourceAsInputStream("biome.nbt");

        NbtMap biomesTag;

        try {
            assert stream != null;
            try (NBTInputStream biomenbtInputStream = NbtUtils.createNetworkReader(stream)) {
                biomesTag = (NbtMap) biomenbtInputStream.readTag();
                BIOMES = biomesTag;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadCreativeItems() {
        //TODO
    }

    public static void loadServer()
    {
        if(!new File(Objects.requireNonNull(FileManager.getFileResource("Server.json"))).isFile()){
        }
    }

    public static void loadEntityIdentifiers() {
        InputStream stream = FileManager.getFileResourceAsInputStream("EntityIdentifiers.nbt");

        try {
            assert stream != null;
            try (NBTInputStream nbtInputStream = NbtUtils.createNetworkReader(stream)) {
                ENTITY_IDENTIFIERS = (NbtMap) nbtInputStream.readTag();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void loadItemEntries() {
        String data = FileManager.getFileResource("item_entries.json");

        Map<String, Map<String, Object>> itemEntries = JSON.parseObject(data, new TypeReference<>(){});
        assert itemEntries != null;
        itemEntries.forEach((itemName, val) -> {
            String id = "" + val.get("runtime_id");
            ITEM_ENTRIES.add(new StartGamePacket.ItemEntry(itemName, Short.parseShort(id)));
        });
    }
}
