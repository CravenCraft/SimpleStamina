package com.cravencraft.stamina.events;

import com.cravencraft.stamina.SimpleStamina;
import com.cravencraft.stamina.registries.DatapackRegistry;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

import java.io.IOException;
import java.io.InputStreamReader;

@Mod(SimpleStamina.MODID)
@EventBusSubscriber(modid = SimpleStamina.MODID)
public class ServerEvents {
    // TODO: Everything is setup to add armors and tools for stamina. Just need to implement it now.
    public static final String ARMOR_PATH = "stamina_cost/armor";
    public static final String MELEE_WEAPON_PATH = "stamina_cost/melee_weapons";
    public static final String RANGED_WEAPON_PATH = "stamina_cost/ranged_weapons";
    public static final String SHIELD_PATH = "stamina_cost/shields";
    public static final String TOOL_PATH = "stamina_cost/tools";

    /**
     * Reads datapacks & saves stamina overrides for items in a Map object, which will be used to override
     * the stamina cost of a weapon when the player attacks in the CalculateStaminaUtils class.
     */
    @SubscribeEvent
    public static void loadStaminaDataPackValues(ServerStartedEvent event) throws IOException {
        SimpleStamina.LOGGER.info("attempting to load server datapacks.");
        ResourceManager resourceManager = event.getServer().getResourceManager();

        loadCertainDatapackStaminaValue(MELEE_WEAPON_PATH, resourceManager);
        loadCertainDatapackStaminaValue(RANGED_WEAPON_PATH, resourceManager);
        loadCertainDatapackStaminaValue(SHIELD_PATH, resourceManager);
//
//        for (var listEntry: resourceManager.listResourceStacks("stamina_cost/melee_weapons",
//                (fileName) -> fileName.getPath().endsWith(".json")).entrySet()) {
//            String nameSpace = listEntry.getKey().getNamespace();
//
//            SimpleStamina.LOGGER.info("server datapack namespace: {} | path: {}", nameSpace, listEntry.getKey().getPath());
//
//
//        }
//
//        for (Map.Entry<ResourceLocation, List<Resource>> resourceLocationListEntry : resourceManager.listResourceStacks("stamina_cost",
//                (fileName) -> fileName.getPath().endsWith(".json")).entrySet()) {
//            String nameSpace = resourceLocationListEntry.getKey().getNamespace();
//
//            for (Resource resource : resourceLocationListEntry.getValue()) {
//                JsonReader staminaReader = new JsonReader(new InputStreamReader(resource.open()));
//                try {
//                    JsonArray staminaItems = JsonParser.parseReader(staminaReader).getAsJsonArray();
//
//                    for (JsonElement staminaItem : staminaItems) {
//                        if (staminaItem.isJsonObject()) {
//                            JsonObject itemAttributes = staminaItem.getAsJsonObject();
//                            if (itemAttributes.has("stamina_cost")) {
//                                String type = (itemAttributes.has("type")) ? itemAttributes.get("type").getAsString() : "placeholder";
//                                String itemId = (itemAttributes.has("name")) ? itemAttributes.get("name").getAsString() : "placeholder";
//                                double staminaCost = (itemAttributes.has("stamina_cost")) ? itemAttributes.get("stamina_cost").getAsDouble() : 0;
//
//                                itemId = nameSpace.concat(".").concat(itemId);
//
//                                DatapackRegistry.addDataPackStaminaValues(type, itemId, staminaCost);
//                            }
//                        }
//                    }
//                } catch (IllegalStateException e) {
//                    SimpleStamina.LOGGER.error("ERROR: {}. The JSON object {} isn't properly configured.", e, resourceLocationListEntry.getKey());
//                }
//
//                staminaReader.close();
//            }
//        }
    }

    private static void loadCertainDatapackStaminaValue(String type, ResourceManager resourceManager) throws IOException {
        for (var listEntry: resourceManager.listResourceStacks(type, (fileName) -> fileName.getPath().endsWith(".json")).entrySet()) {
            String nameSpace = listEntry.getKey().getNamespace();

            SimpleStamina.LOGGER.info("server datapack namespace: {} | path: {}", nameSpace, listEntry.getKey().getPath());

            for (Resource resource : listEntry.getValue()) {
                JsonReader staminaReader = new JsonReader(new InputStreamReader(resource.open()));
                try {
                    JsonArray staminaItems = JsonParser.parseReader(staminaReader).getAsJsonArray();

                    for (JsonElement staminaItem : staminaItems) {
                        if (staminaItem.isJsonObject()) {
                            JsonObject itemAttributes = staminaItem.getAsJsonObject();
                            if (itemAttributes.has("stamina_cost")) {
                                String itemId = (itemAttributes.has("name")) ? itemAttributes.get("name").getAsString() : "placeholder";
                                double staminaCost = (itemAttributes.has("stamina_cost")) ? itemAttributes.get("stamina_cost").getAsDouble() : 0;

                                itemId = nameSpace.concat(".").concat(itemId);

                                DatapackRegistry.addDataPackStaminaValues(type, itemId, staminaCost);
                            }
                        }
                    }
                } catch (IllegalStateException e) {
                    SimpleStamina.LOGGER.error("ERROR: {}. The JSON object {} isn't properly configured.", e, listEntry.getKey());
                }

                staminaReader.close();
            }
        }
    }

}