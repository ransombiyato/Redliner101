package at.petrak.hexcasting.fabric.cc.adimpl;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import at.petrak.hexcasting.api.addldata.ADVariantItem;
import at.petrak.hexcasting.api.item.VariantItem;
import net.minecraft.world.item.ItemStack;
import org.ladysnake.cca.api.v3.component.Component;

public abstract class CCVariantItem implements ADVariantItem, Component {
    final ItemStack stack;
    public CCVariantItem(ItemStack stack) {
        this.stack = stack;
    }

    public static class ItemBased extends CCVariantItem {
        private final VariantItem variantItem;

        public ItemBased(ItemStack owner) {
            super(owner);
            var item = owner.getItem();
            if (!(item instanceof VariantItem variantItem)) {
                throw new IllegalStateException("item is not a colorizer: " + owner);
            }
            this.variantItem = variantItem;
        }

        @Override
        public int numVariants() {
            return variantItem.numVariants();
        }

        @Override
        public int getVariant() {
            return variantItem.getVariant(this.stack);
        }

        @Override
        public void setVariant(int variant) {
            variantItem.setVariant(this.stack, variant);
        }

        @Override
        public void readData(ValueInput input) {

        }

        @Override
        public void writeData(ValueOutput output) {

        }
    }
}
