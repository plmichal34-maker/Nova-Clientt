package dev.novaclient.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandledScreen.class)
public interface HandledScreenAccessor {
    @Accessor("x")
    int novaGetX();

    @Accessor("y")
    int novaGetY();

    @Accessor("backgroundWidth")
    int novaGetWidth();

    @Accessor("backgroundHeight")
    int novaGetHeight();
}
