package at.petrak.hexcasting.api.casting.mishaps.circle

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import net.minecraft.core.component.DataComponents
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantments

class MishapNoSpellCircle : Mishap() {
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment =
        dyeColor(DyeColor.LIGHT_BLUE)

    // FIXME: make me work with any entity and not just players
    private inline fun dropAll(player: Player, start: Int, end: Int, filter: (ItemStack) -> Boolean = { true }) {
        val inventory = player.inventory
        for (index in start until end) {
            val item = inventory.getItem(index)
            if (!item.isEmpty && filter(item)) {
                player.drop(item, true, false)
                inventory.setItem(index, ItemStack.EMPTY)
            }
        }
    }

    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
        val caster = env.castingEntity as? ServerPlayer
        if (caster != null) {
            // FIXME: handle null caster case
            dropAll(caster, 0, 36)
            dropAll(caster, 40, 41)
            dropAll(caster, 36, 40) {
                it.get(DataComponents.ENCHANTMENTS)?.keySet()?.any { e -> e.`is`(Enchantments.BINDING_CURSE) } != true
            }
        }
    }

    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context) =
        error("no_spell_circle")
}
