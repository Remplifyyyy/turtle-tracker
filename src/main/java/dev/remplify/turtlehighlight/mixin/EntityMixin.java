package dev.remplify.turtlehighlight.mixin;

import dev.remplify.turtlehighlight.TurtleHighlight;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Turns the outline purple (overrides the team colour for highlighted players). */
@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
    private void remplify$purpleOutline(CallbackInfoReturnable<Integer> cir) {
        if (TurtleHighlight.HIGHLIGHTED.contains(((Entity) (Object) this).getId())) {
            cir.setReturnValue(TurtleHighlight.PURPLE);
        }
    }
}
