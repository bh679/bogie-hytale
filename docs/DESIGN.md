# Bogie — Design

## Goal

Provide what Dungeon Train needs from its physics layer, on Hytale:

1. **Moving structures** — a set of blocks (a carriage) authored in structure-local space,
   placed and continuously moved through the world.
2. **Riders** — players and entities standing on a structure move with it, and can walk
   around on it while it moves.
3. **Space transforms** — `shipToWorld` / `worldToShip` equivalents. Dungeon Train's MC
   codebase leans on this split everywhere (spawning, cameras, HUD, on-deck checks), so the
   Hytale port should find the same primitives here.
4. **Structure lifecycle** — create from a template/prefab, persist across server restarts,
   despawn cleanly.

Explicitly **not** goals (Sable/VS features Dungeon Train doesn't use):
free rigid-body physics, collisions between structures, buoyancy. Train motion is
kinematic (scripted paths), which is dramatically simpler than a physics engine.

## What we know about the Hytale platform (verified, Jan 2026 Early Access)

- All game logic is server-side Java; client is render-only. Plugins are jars against the
  Hytale Server API (`com.hypixel.hytale.server.core.*`), Java 25.
- Plugins get: `JavaPlugin` lifecycle (`setup()`), EventBus + ECS dual event system,
  command registry, config codecs, `manifest.json` metadata.
- Server source release was slated for ~March 2026 — worth re-checking, as it will reveal
  the block/chunk mutation and entity APIs this design depends on.
- Distribution: CurseForge (official partner).

## Phase 1 spike findings (headless-verified; in-game pending)

- **ECS integration works as hoped.** `getEntityStoreRegistry().registerComponent(Class, Supplier)`
  + `.registerSystem(ISystem)` from `JavaPlugin.setup()`; per-tick logic via
  `EntityTickingSystem<EntityStore>` (`getQuery()` returns a `ComponentType`, which is itself a
  `Query`; `tick(dt, index, chunk, store, buffer)` per entity). Verified: plugin loads and enables
  on server 2026.03.26-89796e57b with two systems + three commands registered.
- **Movement primitive candidate:** spawn via
  `BlockEntity.assembleDefaultBlockEntity(TimeResource, blockTypeKey, pos)` →
  `store.addEntity(holder, AddReason.SPAWN)`; move by writing
  `TransformComponent.setPosition(Vector3d)` each tick (`teleportPosition` is the snap variant —
  the naming implies setPosition interpolates client-side; confirm in-game).
- **Rider carry candidate:** no built-in platform/vehicle attachment surfaced in the API scan
  (built-in `mounts` is NPC riding); `Player.addLocationChange(ref, dx, dy, dz, accessor)` is a
  server-side *relative* player move — exactly the Sable-style carry primitive. Confirm smoothness
  in-game.
- **Plugin/manifest gotchas:** `ServerVersion` must be a real version (warning → future hard
  error); server scans `./mods` by default (passing `--mods mods` double-loads and aborts boot);
  `--bare --boot-command stop` gives a fast headless load check. See docs/DEV-SERVER.md.

## Open questions (Phase 1 research)

- **Block movement primitive:** does the API expose fast per-tick block region rewrites, or
  entity-attached visual blocks (Hytale has rich prefab/prop support)? A prop/prefab-entity
  approach would give smooth sub-block motion for free — closer to how Sable renders ships —
  and is the preferred candidate.
- **Rider attachment:** is there a platform/vehicle attachment in the ECS, or do we
  re-apply per-tick velocity to entities standing on the structure (the Sable approach)?
- **Persistence:** what per-plugin world storage does the API provide?

## Architecture

```
games.brennan.bogie.core     — pure math + kinematics. No Hytale imports. Unit-tested.
  Vec3                       — immutable vector
  StructureFrame             — origin + yaw + velocity; local↔world transforms; step(dt)
  (next) TrackPath           — waypoint path a frame follows, mirroring DT's rail cycle

games.brennan.bogie.hytale   — adapter layer. All Hytale API usage lives here.
  StructureRegistry          — live structures by UUID
  (next) StructureRenderer   — publishes a frame's blocks into the world each tick
  (next) RiderTracker        — finds entities on deck, applies frame delta

games.brennan.bogie          — BogiePlugin entry point, wiring, commands.
```

The core/adapter split is deliberate: Hytale's API is young and will churn through Early
Access; keeping kinematics engine-agnostic means API churn only touches the adapter.

## Phases

| Phase | Deliverable | Proves |
|---|---|---|
| 0 | This scaffold: transforms + registry + tests | repo, CI, versioning |
| 1 | API research spike: move ONE block smoothly; ride ONE moving platform | the two open questions above |
| 2 | Carriage from prefab template, moving on a `TrackPath` loop | structure lifecycle |
| 3 | Riders: walk on a moving carriage, on-deck detection | the hard part |
| 4 | Persistence + multi-carriage trains + coupling | Dungeon Train's actual needs |
| 5 | Dungeon Train Hytale port begins (separate repo), Bogie as dependency | — |

## Lessons carried over from the MC/Sable version

Hard-won gotchas from dungeon-train-mc worth designing around from day one:

- **Culling/reload:** structures far from players being unloaded caused a long tail of bugs
  (train vanish, pause/resume separation, join bursts). Bogie should make "hold near players"
  a first-class registry concept, not an afterthought.
- **Tick vs render coords:** riders' reported positions differing between tick and render
  time broke cameras and telemetry. Document which space every API returns early.
- **On-deck ≠ inside AABB:** deck-support checks beat coarse bounding boxes for "is the
  player on the train".
- **World-space entities:** mobs standing on a carriage live in world space; provide
  `worldAABB()`-style queries from the registry from the start.
