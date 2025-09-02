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

    //TODO: Maybe make the default values here server config values?
    public static DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, SimpleStamina.MODID);

    public static DeferredHolder<Attribute, Attribute> MAX_STAMINA = ATTRIBUTES.register("max_stamina", () -> (
            new RangedAttribute("attribute.simple_stamina.max_stamina", 100.0D, 0.0D, 10000.0D).setSyncable(true)
    ));
    public static DeferredHolder<Attribute, Attribute> STAMINA_REGEN = ATTRIBUTES.register("stamina_regen", () -> (
            new RangedAttribute("attribute.simple_stamina.stamina_regen", .02D, 0.0D, 1.0D).setSyncable(true)
    ));
    public static DeferredHolder<Attribute, Attribute> SPRINT_STAMINA_COST = ATTRIBUTES.register("sprint_stamina_cost", () -> (
            new RangedAttribute("attribute.simple_stamina.sprint_stamina_cost", 0.25D, 0.0D, 10.0D).setSyncable(true)
    ));
    public static DeferredHolder<Attribute, Attribute> SWIM_STAMINA_COST = ATTRIBUTES.register("swim_stamina_cost", () -> (
            new RangedAttribute("attribute.simple_stamina.swim_stamina_cost", 0.5D, 0.0D, 10.0D).setSyncable(true)
    ));
    public static DeferredHolder<Attribute, Attribute> JUMP_STAMINA_COST = ATTRIBUTES.register("jump_stamina_cost", () -> (
            new RangedAttribute("attribute.simple_stamina.jump_stamina_cost", 10.0D, 0.0D, 100.0D).setSyncable(true)
    ));
    public static DeferredHolder<Attribute, Attribute> ATTACK_STAMINA_COST = ATTRIBUTES.register("attack_stamina_cost", () -> (
            new RangedAttribute("attribute.simple_stamina.attack_stamina_cost", 1.0D, 0.0D, 100.0D).setSyncable(true)
    ));
    public static DeferredHolder<Attribute, Attribute> PULL_BOW_STAMINA_COST = ATTRIBUTES.register("pull_bow_stamina_cost", () -> (
            new RangedAttribute("attribute.simple_stamina.pull_bow_stamina_cost", 1.0D, 0.0D, 100.0D).setSyncable(true)
    ));
    public static DeferredHolder<Attribute, Attribute> BLOCK_STAMINA_COST_REDUCTION = ATTRIBUTES.register("block_stamina_cost_reduction", () -> (
            new RangedAttribute("attribute.simple_stamina.block_stamina_cost_reduction", 0.0D, 0.0D, 100.0D).setSyncable(true)
    ));

    public static void register(IEventBus eventBus) { ATTRIBUTES.register(eventBus); }

    @SubscribeEvent
    public static void modifyEntityAttributes(EntityAttributeModificationEvent e) {
        e.getTypes().forEach(entity -> ATTRIBUTES.getEntries().forEach(attribute -> e.add(entity, attribute)));
    }
}
