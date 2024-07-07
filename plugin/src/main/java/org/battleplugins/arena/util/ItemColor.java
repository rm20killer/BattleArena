package org.battleplugins.arena.util;

import org.bukkit.DyeColor;
import org.bukkit.Material;

import java.util.Map;
import java.util.function.Function;

/**
 * A utility class to get the different colors of items.
 */
public enum ItemColor {
    WHITE(DyeColor.WHITE, Material.WHITE_BANNER, Material.WHITE_WOOL, Material.WHITE_STAINED_GLASS_PANE, Material.WHITE_DYE, Material.WHITE_CANDLE),
    ORANGE(DyeColor.ORANGE, Material.ORANGE_BANNER, Material.ORANGE_WOOL, Material.ORANGE_STAINED_GLASS_PANE, Material.ORANGE_DYE, Material.ORANGE_CANDLE),
    MAGENTA(DyeColor.MAGENTA, Material.MAGENTA_BANNER, Material.MAGENTA_WOOL, Material.MAGENTA_STAINED_GLASS_PANE, Material.MAGENTA_DYE, Material.MAGENTA_CANDLE),
    LIGHT_BLUE(DyeColor.LIGHT_BLUE, Material.LIGHT_BLUE_BANNER, Material.LIGHT_BLUE_WOOL, Material.LIGHT_BLUE_STAINED_GLASS_PANE, Material.LIGHT_BLUE_DYE, Material.LIGHT_BLUE_CANDLE),
    YELLOW(DyeColor.YELLOW, Material.YELLOW_BANNER, Material.YELLOW_WOOL, Material.YELLOW_STAINED_GLASS_PANE, Material.YELLOW_DYE, Material.YELLOW_CANDLE),
    LIME(DyeColor.LIME, Material.LIME_BANNER, Material.LIME_WOOL, Material.LIME_STAINED_GLASS_PANE, Material.LIME_DYE, Material.LIME_CANDLE),
    PINK(DyeColor.PINK, Material.PINK_BANNER, Material.PINK_WOOL, Material.PINK_STAINED_GLASS_PANE, Material.PINK_DYE, Material.PINK_CANDLE),
    GRAY(DyeColor.GRAY, Material.GRAY_BANNER, Material.GRAY_WOOL, Material.GRAY_STAINED_GLASS_PANE, Material.GRAY_DYE, Material.GRAY_CANDLE),
    LIGHT_GRAY(DyeColor.LIGHT_GRAY, Material.LIGHT_GRAY_BANNER, Material.LIGHT_GRAY_WOOL, Material.LIGHT_GRAY_STAINED_GLASS_PANE, Material.LIGHT_GRAY_DYE, Material.LIGHT_GRAY_CANDLE),
    CYAN(DyeColor.CYAN, Material.CYAN_BANNER, Material.CYAN_WOOL, Material.CYAN_STAINED_GLASS_PANE, Material.CYAN_DYE, Material.CYAN_CANDLE),
    PURPLE(DyeColor.PURPLE, Material.PURPLE_BANNER, Material.PURPLE_WOOL, Material.PURPLE_STAINED_GLASS_PANE, Material.PURPLE_DYE, Material.PURPLE_CANDLE),
    BLUE(DyeColor.BLUE, Material.BLUE_BANNER, Material.BLUE_WOOL, Material.BLUE_STAINED_GLASS_PANE, Material.BLUE_DYE, Material.BLUE_CANDLE),
    BROWN(DyeColor.BROWN, Material.BROWN_BANNER, Material.BROWN_WOOL, Material.BROWN_STAINED_GLASS_PANE, Material.BROWN_DYE, Material.BROWN_CANDLE),
    GREEN(DyeColor.GREEN, Material.GREEN_BANNER, Material.GREEN_WOOL, Material.GREEN_STAINED_GLASS_PANE, Material.GREEN_DYE, Material.GREEN_CANDLE),
    RED(DyeColor.RED, Material.RED_BANNER, Material.RED_WOOL, Material.RED_STAINED_GLASS_PANE, Material.RED_DYE, Material.RED_CANDLE),
    BLACK(DyeColor.BLACK, Material.BLACK_BANNER, Material.BLACK_WOOL, Material.BLACK_STAINED_GLASS_PANE, Material.BLACK_DYE, Material.BLACK_CANDLE);

    private static final ItemColor[] VALUES = values();
    private static final Map<Category, Function<ItemColor, Material>> CATEGORIES = Map.of(
            Category.BANNER, c -> c.banner,
            Category.WOOL, c -> c.wool,
            Category.GLASS_PANE, c -> c.glassPane,
            Category.DYE, c -> c.dye,
            Category.CANDLE, c -> c.candle
    );
    
    private final DyeColor color;
    private final Material banner;
    private final Material wool;
    private final Material glassPane;
    private final Material dye;
    private final Material candle;

    ItemColor(DyeColor color, Material banner, Material wool, Material glassPane, Material dye, Material candle) {
        this.color = color;
        this.banner = banner;
        this.wool = wool;
        this.glassPane = glassPane;
        this.dye = dye;
        this.candle = candle;
    }

    /**
     * Gets the material of the given color and category.
     *
     * @param color the color of the material
     * @param category the category of the material
     * @return the material of the given color and category
     */
    public static Material get(DyeColor color, Category category) {
        for (ItemColor itemColor : VALUES) {
            if (itemColor.color == color) {
                return CATEGORIES.get(category).apply(itemColor);
            }
        }

        return null;
    }

    /**
     * Gets the color of the given material and category.
     *
     * @param material the material of the color
     * @param category the category of the color
     * @return the color of the given material and category
     */
    public static DyeColor get(Material material, Category category) {
        for (ItemColor itemColor : VALUES) {
            if (CATEGORIES.get(category).apply(itemColor) == material) {
                return itemColor.color;
            }
        }

        return null;
    }

    /**
     * The different categories of colors.
     */
    public enum Category {
        BANNER,
        WOOL,
        GLASS_PANE,
        DYE,
        CANDLE
    }
}