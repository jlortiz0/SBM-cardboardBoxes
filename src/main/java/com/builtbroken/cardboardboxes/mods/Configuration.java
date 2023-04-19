package com.builtbroken.cardboardboxes.mods;

import static com.builtbroken.cardboardboxes.Cardboardboxes.LOGGER;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.builtbroken.cardboardboxes.handler.HandlerManager;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.include.com.google.gson.Gson;
import org.spongepowered.include.com.google.gson.stream.JsonWriter;

/**
 * Prefab for handling interaction for a mod or content package
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/28/2015.
 */
public class Configuration {

    public HashMap<String, Boolean> blockEntityBan;
    public boolean allowSpawners;

    public Configuration() {
        allowSpawners = false;
        blockEntityBan = new HashMap<>();
    }

    /**
     * Called to build config
     */
    public static Configuration load() {
        Path saveDir = FabricLoader.getInstance().getConfigDir();
        Configuration obj;
        if (Files.exists(saveDir.resolve("sbm-cardboardboxes.json"))) {
            try (FileReader cfg = new FileReader(saveDir.resolve("sbm-cardboardboxes.json").toFile())) {
                obj = new Gson().fromJson(cfg, Configuration.class);
            } catch (IOException e) {
                return null;
            }
        } else {
            obj = new Configuration();
            obj.saveBanList();
        }
        obj.loadBanList();
        return obj;
    }
    public boolean save() {
        saveBanList();
        Path saveDir = FabricLoader.getInstance().getConfigDir();
        try (FileWriter cfg = new FileWriter(saveDir.resolve("sbm-cardboardboxes.json").toFile());
             JsonWriter w = new JsonWriter(cfg)) {
            new Gson().getAdapter(Configuration.class).write(w, this);;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private void saveBanList() {
        for (Identifier name : Registry.BLOCK_ENTITY_TYPE.getIds()) {
            BlockEntityType<?> type = Registry.BLOCK_ENTITY_TYPE.get(name);
            if (name != null && type != null) {
                try {
                    String typeString = type.toString();
                    boolean shouldBan = HandlerManager.blockEntityBanList.contains(type) || typeString.contains("cable") || typeString.contains("wire") || typeString.contains("pipe") || typeString.contains("tube") || typeString.contains("conduit") || typeString.contains("channel");
                    blockEntityBan.put(typeString, shouldBan);
                } catch (Exception e) {
                    LOGGER.error("ModHandler#buildConfig() -> Failed to add entry to config [" + name + " > " + type + "]", e);
                }
            }
        }
    }

    private void loadBanList() {
        for (Identifier name : Registry.BLOCK_ENTITY_TYPE.getIds()) {
            BlockEntityType<?> type = Registry.BLOCK_ENTITY_TYPE.get(name);
            if (name != null && type != null) {
                try {
                    String typeString = type.toString();
                    boolean shouldBan = HandlerManager.blockEntityBanList.contains(type) || typeString.contains("cable") || typeString.contains("wire") || typeString.contains("pipe") || typeString.contains("tube") || typeString.contains("conduit") || typeString.contains("channel");
                    if (blockEntityBan.containsKey(typeString) ? blockEntityBan.get(typeString) : false) {
                        HandlerManager.INSTANCE.banBlockEntity(type);
                    } else if (shouldBan) {
                        //If original was banned but someone unbanned it in the config
                        HandlerManager.blockEntityBanList.remove(type);
                    }
                } catch (Exception e) {
                    LOGGER.error("ModHandler#loadHandlerData() -> Failed to add entry to config [" + name + " > " + type + "]", e);
                }
            }
        }
    }
}
