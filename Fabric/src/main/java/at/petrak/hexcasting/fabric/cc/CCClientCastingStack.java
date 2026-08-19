package at.petrak.hexcasting.fabric.cc;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import at.petrak.hexcasting.api.client.ClientCastingStack;
import net.minecraft.world.entity.player.Player;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;

public class CCClientCastingStack implements Component, ClientTickingComponent {

    public CCClientCastingStack(Player owner) {
    }

    private final ClientCastingStack clientCastingStack = new ClientCastingStack();

    public ClientCastingStack getClientCastingStack() {
        return clientCastingStack;
    }

    @Override
    public void clientTick() {
        clientCastingStack.tick();
    }

    @Override
    public void readData(ValueInput input) { }

    @Override
    public void writeData(ValueOutput output) { }
}
