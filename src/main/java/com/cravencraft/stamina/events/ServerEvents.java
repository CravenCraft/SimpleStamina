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
    public static final String ARMOR_PATH = "stamina_item_values/armor";
    public static final String MELEE_WEAPON_PATH = "stamina_item_values/melee_weapons";
    public static final String RANGED_WEAPON_PATH = "stamina_item_values/ranged_weapons";
    public static final String SHIELD_PATH = "stamina_item_values/shields";
    public static final String TOOL_PATH = "stamina_item_values/tools";

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
                            if (itemAttributes.has("stamina_cost") || itemAttributes.has("stamina_reduction_percentage")) {
                                String itemId = (itemAttributes.has("name")) ? itemAttributes.get("name").getAsString() : "placeholder";
                                double staminaCost = (itemAttributes.has("stamina_cost")) ? itemAttributes.get("stamina_cost").getAsDouble() : itemAttributes.get("stamina_reduction_percentage").getAsDouble();

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