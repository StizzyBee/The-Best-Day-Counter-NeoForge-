# Day Counter — Neoforge for Minecraft 1.21.3

This independent build retains the configurable HUD, pause-menu settings, six languages,
font and text-case options, dragging, resizing, presets, and saved configuration.
The repository root continues to build Minecraft 26.1.2.

## Build

Install JDK 21. From this directory, run `./gradlew build` (Windows: `gradlew.bat build`).
The installable mod is `build/libs/day-counter-neoforge-1.2.1+1.21.3.jar`.
For Fabric, do not install the `-dev` or `-sources` jars.

## Install

Install the Neoforge loader for Minecraft **1.21.3**, then put the mod jar in `mods/`.
Use NeoForge **21.3.97+**.
This is a client-side mod. Use the matching build for your loader and Minecraft version.

## Manual game checks

- Enter a world and confirm the day counter appears; hide the HUD with F1 and confirm it disappears.
- Open the pause menu and Day Counter Settings; try dragging, scrolling, arrow keys and Shift+arrow keys.
- Change language, font, text case, scale and position presets; choose Done, then restart to check persistence.
- Resize the window and check the HUD position scales with the screen.
- Check the pause menu in another language and on a multiplayer server.
