package com.builtbroken.cardboardboxes;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Material;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.builtbroken.cardboardboxes.box.BoxBlock;
import com.builtbroken.cardboardboxes.box.BoxBlockEntity;
import com.builtbroken.cardboardboxes.box.BoxBlockItem;
import com.builtbroken.cardboardboxes.handler.HandlerManager;
import com.builtbroken.cardboardboxes.mods.ModHandler;
import com.builtbroken.cardboardboxes.mods.VanillaHandler;

import net.minecraft.block.entity.BlockEntityType;

/**
 * Main mod class, handles registering content and triggering loading of interaction
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/25/2015.
 */

public class Cardboardboxes implements ModInitializer {
    public static final String DOMAIN = "cardboardboxes";
    public static final String PREFIX = DOMAIN + ":";
    public static final Logger LOGGER = LogManager.getLogger(DOMAIN);
    public static final BoxBlock BOX_BLOCK = new BoxBlock(FabricBlockSettings.of(Material.WOOD).hardness(2f));
    public static final BoxBlockItem BOX_ITEM = new BoxBlockItem(BOX_BLOCK);
    public static BlockEntityType<BoxBlockEntity> BOX_BLOCK_ENTITY_TYPE;


    @Override
    public void onInitialize() {
        ModHandler.modSupportHandlerMap.put("minecraft", VanillaHandler.class);
        Registry.register(Registry.BLOCK, new Identifier(DOMAIN, "cardboard_box"), BOX_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(DOMAIN, "cardboard_box"), BOX_ITEM);
        BOX_BLOCK_ENTITY_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(DOMAIN, "cardboard_box"),
                FabricBlockEntityTypeBuilder.create(BoxBlockEntity::new, BOX_BLOCK).build());
        HandlerManager.INSTANCE.banBlock(BOX_BLOCK);
        HandlerManager.INSTANCE.banBlockEntity(BOX_BLOCK_ENTITY_TYPE);

        // ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, config = ModHandler.buildHandlerData());
        // LOGGER.info("Finished building the config -> " + config);
        // ModHandler.loadHandlerData(config);
    }
}
