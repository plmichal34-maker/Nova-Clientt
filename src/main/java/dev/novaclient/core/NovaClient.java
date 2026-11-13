package dev.novaclient.core;

import dev.novaclient.gui.ClickGuiScreen;
import dev.novaclient.module.Module;
import dev.novaclient.module.ModuleManager;
import dev.novaclient.render.HudRenderer;
import dev.novaclient.render.ScreenStyleRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/** Punkt wejscia clienta (Fabric). */
public final class NovaClient implements ClientModInitializer {
    public static KeyBinding OPEN_GUI;

    @Override
    public void onInitializeClient() {
        ModuleManager.init();
        ConfigManager.init();

        OPEN_GUI = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.novaclient.gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "category.novaclient"));

        ClientTickEvents.END_CLIENT_TICK.register(NovaClient::onTick);
        HudRenderCallback.EVENT.register(HudRenderer::render);
        ClientLifecycleEvents.CLIENT_STOPPING.register(c -> ConfigManager.saveNow());

        ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
            ScreenStyleRenderer.onScreenInit();
            ScreenEvents.beforeRender(screen).register((s, ctx, mx, my, delta) -> ScreenStyleRenderer.before(s, ctx));
            ScreenEvents.afterRender(screen).register((s, ctx, mx, my, delta) -> ScreenStyleRenderer.after(s, ctx, mx, my));
        });

        NovaLog.info("Nova Client zaladowany: " + ModuleManager.all().size() + " modulow.");
    }

    private static void onTick(MinecraftClient mc) {
        ConfigManager.tick();
        while (OPEN_GUI.wasPressed()) {
            if (mc.currentScreen == null) mc.setScreen(new ClickGuiScreen());
        }
        long handle = mc.getWindow().getHandle();
        for (Module m : ModuleManager.all()) {
            if (m.isEnabled()) m.onTick();
            if (m.alwaysOn || m.key < 0 || mc.currentScreen != null) {
                m.keyDown = m.key >= 0 && InputUtil.isKeyPressed(handle, m.key);
                continue;
            }
            boolean down = InputUtil.isKeyPressed(handle, m.key);
            if (down && !m.keyDown) m.toggle();
            m.keyDown = down;
        }
    }
}
