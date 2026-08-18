package at.petrak.hexcasting.client.gui;

import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.misc.PatternTooltip;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jetbrains.annotations.Nullable;

import static at.petrak.hexcasting.api.HexAPI.modLoc;

/** Client-side rendering data for the pattern tooltip. */
public class PatternTooltipComponent implements ClientTooltipComponent {
    public static final Identifier PRISTINE_BG = modLoc("textures/gui/scroll.png");
    public static final Identifier ANCIENT_BG = modLoc("textures/gui/scroll_ancient.png");
    public static final Identifier SLATE_BG = modLoc("textures/gui/slate.png");

    private static final int RENDER_SIZE = 128;
    private static final int TEXTURE_SIZE = 48;

    private final HexPattern pattern;
    private final Identifier background;

    public PatternTooltipComponent(PatternTooltip tooltip) {
        this.pattern = tooltip.pattern();
        this.background = tooltip.background();
    }

    @Nullable
    public static ClientTooltipComponent tryConvert(TooltipComponent component) {
        return component instanceof PatternTooltip tooltip ? new PatternTooltipComponent(tooltip) : null;
    }

    @Override
    public void renderImage(Font font, int x, int y, int width, int height, GuiGraphics graphics) {
        var pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(x, y);
        graphics.blit(RenderPipelines.GUI_TEXTURED, this.background, 0, 0, 0f, 0f,
            RENDER_SIZE, RENDER_SIZE, TEXTURE_SIZE, TEXTURE_SIZE);
        pose.popMatrix();
    }

    @Override
    public int getWidth(Font font) {
        return RENDER_SIZE;
    }

    @Override
    public int getHeight(Font font) {
        return RENDER_SIZE;
    }
}
