# Day Counter (NeoForge)

A lightweight, client-side HUD mod that shows how many in-game days have passed. Everything is configurable from a settings screen inside the game — no config files to edit.

This is the **NeoForge** build for Minecraft **26.1.2**. See also the [Fabric](https://github.com/StizzyBee/The-Best-Day-Counter-Fabric) and [Forge](https://github.com/StizzyBee/The-Best-Day-Counter-Forge) versions.

## Features

- On-screen day counter, calculated from the world's total tick time.
- Drag to reposition, scroll or +/− to resize (0.5×–5.0×), arrow keys to nudge.
- Five position presets plus horizontal/vertical centering.
- Six display languages: English, Español, Português, Français, Русский, 中文.
- Default or filtered font, and Normal / ALL CAPS / lowercase text styles.
- Settings open from a **"Day Counter Settings"** button in the pause menu; everything is saved to `config/day-counter.json`.

## Requirements

- Minecraft **26.1.2**
- NeoForge **26.1.2.x** (tested on 26.1.2.75)

## Installing

1. Install NeoForge for 26.1.2.
2. Download the latest `day_counter-x.x.x.jar` from the [Releases](../../releases) page (or CurseForge).
3. Drop the jar into your `mods` folder.

It is a client-side mod — it does nothing on a dedicated server and isn't required by other players.

## Building from source

Requires JDK 25.

```bash
./gradlew build
```

The finished jar is written to `build/libs/`.

## License

[MIT](LICENSE)
