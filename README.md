# Create: Kinetic Frontier

**Create: Kinetic Frontier** is a NeoForge 1.21.1 addon for Create that turns rotational power into a coherent engineering branch focused on momentum, storage, launching, braking, grappling, and high-speed transport.

## Installation

Install Minecraft Java Edition **1.21.1**, NeoForge **21.1.248 or a compatible 21.1.x release**, and **Create 6.0.10 or newer in the 6.0.x line**. Copy the built `createkineticfrontier-1.0.0.jar` from `build/libs/` into the Minecraft `mods/` folder alongside Create.

## Progression

The early-game route begins with the Momentum Hook, Momentum Meter, Kinetic Sensor, and Kinetic Brake. Mid-game engineering adds the Kinetic Core, Momentum Launcher, Kinetic Rail, and Flywheel Array. Late-game projects include the Grapple Winch and staged Kinetic Cannon. Recipes deliberately reuse Create components such as brass casings, shafts, cogwheels, precision mechanisms, electron tubes, sturdy sheets, and andesite alloys instead of adding a redundant ore tier.

## Machines and tools

| Content | Purpose | Controls or activation |
|---|---|---|
| Kinetic Core | Stores up to 5,000 kinetic energy units and transfers charge to the machine in its facing direction. | Apply a redstone signal to charge; remove the signal to release charge forward. |
| Momentum Launcher | Charges over time, then launches nearby dropped items and pushes entities. | Apply a redstone signal; use the Momentum Meter for charge telemetry. |
| Kinetic Rail | Accelerates nearby minecarts with a capped impulse. | Place beneath or beside a rail path and apply a redstone signal. |
| Grapple Winch | Maintains one efficient anchor target and pulls nearby living entities under tension. | Apply a redstone signal; the cable state is persisted as one anchor position. |
| Kinetic Brake | Applies controlled resistance to minecarts and nearby item entities. | Apply a redstone signal. |
| Flywheel Array | Stores energy with capacity based on adjacent flywheel blocks. | Connect adjacent arrays and apply a redstone signal. |
| Kinetic Cannon | Runs an 80-tick preparation and firing cycle with recoil and cooldown. | Apply a redstone signal and place a capsule or item entity in front. |
| Kinetic Sensor | Emits a powered state when it detects nearby moving entities. | Place near a motion path and connect its redstone output. |
| Momentum Hook | Pulls the player toward a valid block anchor. | Right-click a surface; durability and a 24-tick cooldown prevent infinite flight. |
| Momentum Meter | Reports machine state, stored energy, activity, charge, and linked flywheels. | Use on any Kinetic Frontier machine. |
| Kinetic Capsules | Recoverable throwable payload items for cargo, impact, signal, and utility use. | Right-click to throw. |

## Create compatibility

The project depends on Create through the official Create Maven coordinates for the NeoForge 1.21.1 line and reuses Create components in its progression recipes. The current implementation uses Create as the technology foundation and exposes kinetic machines as server-safe block entities. The behavior layer intentionally avoids depending on unstable internal Create contraption classes; this keeps the addon buildable against Create 6.0.10 while leaving clear extension points for future direct stress-network capabilities.

## Build

Use Java 21 and run:

```bash
./gradlew clean build
```

The build uses NeoForge ModDevGradle with the documented no-recompilation pipeline for local reliability. The resulting JAR is created under `build/libs/`. The project includes source code, metadata, registrations, recipes, advancements, English localization, block/item models, and pixel-art textures.

## Development notes

The addon reuses vanilla mechanical sounds rather than shipping broken custom sound references. Persistent machine data is serialized through block entities, cable state is represented by one anchor position rather than thousands of segment entities, and all motion changes are made server-side. The Kinetic Rail is a compact kinetic accelerator block intended to sit in a Create-compatible rail layout; its movement behavior is implemented against vanilla minecart physics so it remains functional even when a specific Create train API is unavailable.
