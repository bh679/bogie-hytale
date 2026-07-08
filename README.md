# Bogie — moving-structure physics for Hytale

Trains, ships, and platforms that carry blocks, entities, and players across a Hytale world.
Bogie fills the role [Sable](https://modrinth.com/mod/sable) plays for the Minecraft version of
[Dungeon Train](https://brennanhatton.itch.io/dungeontrain): it is the physics/structure layer the
Hytale port of Dungeon Train will be built on.

> **Clean-room notice:** Sable is licensed under PolyForm Shield 1.0.0 and its code cannot be
> reused here. Bogie is an independent implementation of the same *concept* (moving sub-worlds with
> a ship-space ↔ world-space transform), written from scratch against the Hytale Server API. Do not
> copy code from Sable, Valkyrien Skies, or other licensed physics mods into this repo.

## Why Hytale makes this easier

Hytale's architecture is server-authoritative: the C# client renders, the Java server owns *all*
game logic — even in singleplayer. That means:

- **No client mod needed.** Any vanilla Hytale client can ride a Bogie train.
- **One codebase.** No client/server split, no mixins into client rendering.
- **Java 25** plugins against the official Hytale Server API, distributed via CurseForge.

## Project layout

| Path | What it is |
|---|---|
| `src/main/java/games/brennan/bogie/core/` | Engine-agnostic kinematics (`Vec3`, `StructureFrame`). No Hytale imports — unit-testable anywhere. |
| `src/main/java/games/brennan/bogie/hytale/` | Adapter layer — everything that touches the Hytale Server API. |
| `src/main/java/games/brennan/bogie/BogiePlugin.java` | Plugin entry point (`JavaPlugin`). |
| `src/main/resources/manifest.json` | Hytale plugin manifest. |
| `docs/DESIGN.md` | Architecture and phased roadmap. |

## Setup

1. Install Hytale (Early Access, hytale.com) and locate `HytaleServer.jar` in your install.
2. Copy it into `libraries/` (gitignored).
3. Build:
   ```bash
   ./gradlew build      # requires the jar from step 2
   ./gradlew test       # core tests run without HytaleServer.jar
   ```
4. Drop `build/libs/bogie-hytale-<version>.jar` into your Hytale server's plugin directory.

## Status

**Phase 0 — scaffold.** The core transform math exists and is tested; the Hytale adapter is a
skeleton. See [docs/DESIGN.md](docs/DESIGN.md) for the phase plan.

## Related repos

- [dungeon-train-mc](https://github.com/bh679/dungeon-train-mc) — the Minecraft version (NeoForge 1.21.1 + Sable)
