package com.cravencraft.stamina.registries;

import com.cravencraft.stamina.SimpleStamina;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = SimpleStamina.MODID)
public class AttributeRegistry {

    public static DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, SimpleStamina.MODID);

    public static DeferredHolder<Attribute, Attribute> MAX_STAMINA = ATTRIBUTES.register("max_stamina", () -> (
            new RangedAttribute("attribute.simple_stamina.max_stamina", 100.0D, 0.0D, 10000.0D).setSyncable(true)
    ));

    public static void register(IEventBus eventBus) { ATTRIBUTES.register(eventBus); }

    @SubscribeEvent
    public static void modifyEntityAttributes(EntityAttributeModificationEvent e) {
        e.getTypes().forEach(entity -> ATTRIBUTES.getEntries().forEach(attribute -> e.add(entity, attribute)));
    }
}
