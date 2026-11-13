package dev.novaclient.render;

import dev.novaclient.module.ModuleManager;
import dev.novaclient.modules.effects.EffectModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

import java.util.List;

/** Punkt wejscia do rysowania wszystkich wlaczonych efektow wokol przedmiotu. */
public final class Effects {
    private Effects() {}

    public static boolean active() {
        if (!EffectModule.globallyAllowed()) return false;
        List<EffectModule> list = ModuleManager.effects();
        for (int i = 0; i < list.size(); i++) if (list.get(i).isEnabled()) return true;
        return false;
    }

    public static void render(DrawContext c, int x, int y, ItemStack stack, ItemTarget target) {
        if (stack.isEmpty()) return;
        List<EffectModule> list = ModuleManager.effects();
        for (int i = 0; i < list.size(); i++) list.get(i).render(c, x, y, stack, target);
    }
}
