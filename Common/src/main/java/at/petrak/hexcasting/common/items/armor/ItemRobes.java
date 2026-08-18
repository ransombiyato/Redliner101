package at.petrak.hexcasting.common.items.armor;

import at.petrak.hexcasting.api.HexAPI;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;

/**
 * Robe armor item backed by the 1.21.11 equipment components.
 */
public class ItemRobes extends Item {
    public final ArmorType type;

    public ItemRobes(ArmorType type, Properties properties) {
        super(properties.humanoidArmor(HexAPI.instance().robesMaterial(), type));
        this.type = type;
    }
}
