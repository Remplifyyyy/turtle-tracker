package dev.remplify.turtlehighlight.mixin;

import dev.remplify.turtlehighlight.TurtleHighlight;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Makes highlighted players draw the glowing outline on this client only. */
@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true)
    private void remplify$turtleGlow(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (TurtleHighlight.HIGHLIGHTED.contains(entity.getId())) {
            cir.setReturnValue(true);
        }
    }
}
