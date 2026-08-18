package at.petrak.hexcasting.common.casting.actions.math

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getNumOrVec
import at.petrak.hexcasting.api.casting.iota.Iota
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3
import kotlin.math.abs
import kotlin.math.sign

object OpCoerceToAxial : ConstMediaAction {
    override val argc: Int
        get() = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val value = args.getNumOrVec(0, argc)
        return value.map({ num ->
            num.sign.asActionResult
        }, { vec ->
            if (vec == Vec3.ZERO)
                vec.asActionResult
            else {
                val direction = when {
                    abs(vec.x) >= abs(vec.y) && abs(vec.x) >= abs(vec.z) -> if (vec.x >= 0) Direction.EAST else Direction.WEST
                    abs(vec.y) >= abs(vec.z) -> if (vec.y >= 0) Direction.UP else Direction.DOWN
                    else -> if (vec.z >= 0) Direction.SOUTH else Direction.NORTH
                }
                Vec3.atLowerCornerOf(direction.getUnitVec3i()).asActionResult
            }
        })
    }
}
