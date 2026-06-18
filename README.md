# Redliner Mod

A momentum-based katana mod for NeoForge 1.21.1, inspired by the Roblox game Redliner.

## Features

- 🗡️ **Redliner Katana**: A high-speed momentum-based sword
- 🚀 **Source-style Bunny Hopping**: Hold jump to maintain momentum and speed
- 💨 **Dash System**: Three rechargeable dash charges for forward momentum
- 🎯 **Speed-Scaled Combat**: Damage scales with movement speed
- ✨ **Visual Effects**: Red glowing, particles, and speed trails
- 📊 **HUD Display**: Shows current dash charges and configurable key

## Building

### With Gradle:
```bash
./gradlew build
```

### With Maven:
```bash
mvn clean package
```

## Installation

1. Install NeoForge 1.21.1
2. Place the mod JAR in your `mods` folder
3. Launch Minecraft

## Controls

- **Dash Key**: Configurable (default key TBD) - Use a dash charge to launch forward
- **Jump**: Hold to build momentum and maintain speed
- **Attack**: Speed-scaled slash damage
- **Bash**: Dash into enemies for knockback

## Configuration

Edit the config file in `config/redliner-common.toml` to customize:

- Dash speed multiplier
- Number of dash charges
- Dash recharge cooldown
- Particle effects intensity
- HUD position and appearance

## TODO

- [ ] Implement katana item and registry
- [ ] Add bunny hop momentum system
- [ ] Implement dash mechanic with charges
- [ ] Add speed-scaled damage calculation
- [ ] Create HUD for dash charges
- [ ] Add particle effects
- [ ] Implement sword glow effect
- [ ] Add sound effects
- [ ] Create textures and models
- [ ] Implement keybind configuration
- [ ] Add config file support

## License

MIT License

## Credits

Inspired by the Roblox game Redliner.
