package dev.remplify.turtlehighlight;

import com.mojang.blaze3d.platform.InputConstants;
import dev.remplify.turtlehighlight.mixin.LivingEntityAccessor;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * Outlines other players purple while they have Turtle Master.
 *
 * The server never tells your client what level of Resistance another player has.
 * What it does send is the list of potion particle colours on every player.
 * Turtle Master always gives Resistance AND Slowness together, so seeing both
 * colours on the same player is the Turtle Master signature.
 */
public class TurtleHighlight implements ClientModInitializer {

    /** Outline colour (RGB). */
    public static final int PURPLE = 0xA020F0;

    /** Entity ids to outline this tick. Read by the mixins every frame. */
    public static final IntSet HIGHLIGHTED = new IntOpenHashSet();

    public static boolean enabled = true;
    /** When false, players are only outlined while you can actually see them. */
    public static boolean throughWalls = false;

    private static KeyMapping toggleKey;
    private static KeyMapping wallsKey;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.remplify_turtle_highlight.toggle",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_J,
                KeyMapping.Category.MISC));
        wallsKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.remplify_turtle_highlight.walls",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,
                KeyMapping.Category.MISC));

        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
    }

    private void tick(Minecraft mc) {
        while (toggleKey.consumeClick()) {
            enabled = !enabled;
            say(mc, "Turtle highlight: ", enabled);
        }
        while (wallsKey.consumeClick()) {
            throughWalls = !throughWalls;
            say(mc, "Turtle highlight through walls: ", throughWalls);
        }

        HIGHLIGHTED.clear();
        if (!enabled || mc.level == null || mc.player == null) return;

        int resistance = MobEffects.RESISTANCE.value().getColor();
        int slowness = MobEffects.SLOWNESS.value().getColor();

        for (Player p : mc.level.players()) {
            if (p == mc.player || p.isSpectator()) continue;

            List<ParticleOptions> particles =
                    p.getEntityData().get(LivingEntityAccessor.getEffectParticlesKey());
            if (particles.isEmpty()) continue;

            if (hasColour(particles, resistance) && hasColour(particles, slowness)
                    && (throughWalls || mc.player.hasLineOfSight(p))) {
                HIGHLIGHTED.add(p.getId());
            }
        }
    }

    private static boolean hasColour(List<ParticleOptions> particles, int rgb) {
        int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
        for (ParticleOptions opt : particles) {
            if (opt instanceof ColorParticleOption c
                    && close(c.getRed(), r) && close(c.getGreen(), g) && close(c.getBlue(), b)) {
                return true;
            }
        }
        return false;
    }

    private static boolean close(float channel, int expected) {
        return Math.abs(Math.round(channel * 255f) - expected) <= 2;
    }

    private static void say(Minecraft mc, String label, boolean on) {
        if (mc.player == null) return;
        mc.player.displayClientMessage(Component.literal(label)
                .append(Component.literal(on ? "ON" : "OFF")
                        .withStyle(on ? ChatFormatting.GREEN : ChatFormatting.RED)), true);
    }
}
