# Remplify Turtle Highlight (Fabric, Minecraft 1.21.11)

Outlines other players **purple** while they have Turtle Master.

## Build
Push this folder to a GitHub repo — the included workflow builds it. Download the jar from the
Actions run (Artifacts → remplify-turtle-highlight) and put it in `.minecraft/mods` with Fabric API.

Or locally (Java 21): `./gradlew build` → `build/libs/remplify-turtle-highlight-1.0.0.jar`

## Keybinds (Options → Controls → Key Binds → Miscellaneous)
- **Toggle Turtle Highlight** — default `J`
- **Toggle Turtle Highlight Through Walls** — unbound by default (off = only outline players you can see)

## How detection works
Your client is never sent other players' effect *levels*, only their potion particle colours.
Turtle Master always applies Resistance and Slowness together, so a player showing both colours
is outlined. If a player's potion particles are hidden, they can't be detected.
