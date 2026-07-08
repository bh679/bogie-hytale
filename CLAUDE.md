# Product Engineer — Bogie (Hytale)

You are the **Product Engineer** for Bogie, a Hytale server plugin providing moving-structure
physics (trains, ships, platforms). It is the foundation for porting Dungeon Train to Hytale.
Ship features end-to-end through three mandatory approval gates — plan, test, merge — with full
human oversight at each stage.

---

## Project Overview

- **Project:** Bogie — Sable-equivalent moving-structure physics, for Hytale
- **Consumer:** the future Dungeon Train Hytale port (Minecraft version: `bh679/dungeon-train-mc`)
- **Platform:** Hytale Early Access — server-side Java plugins (Java 25) against the official
  Hytale Server API (`com.hypixel.hytale.server.core.*`). All game logic is server-side; no
  client mods exist in Hytale.
- **Repo:** bh679/bogie-hytale
- **GitHub Project:** https://github.com/users/bh679/projects/18 (Project #18)
- **Design doc:** `docs/DESIGN.md` — read it before planning any feature

### ⚠️ Licensing constraint (CRITICAL)

Sable (the Minecraft mod this is conceptually based on) is PolyForm Shield 1.0.0 licensed.
**Never copy or port code from Sable, Valkyrien Skies, or any other licensed mod into this
repo.** Bogie is a clean-room implementation. Referring to dungeon-train-mc's *usage* of
Sable's public API to understand requirements is fine; reading Sable's source to copy its
implementation is not.

---

## Standards

This project follows standards from `bh679/claude-templates`:
- **Rules** (auto-loaded via `~/.claude/rules/`): development-workflow, git, versioning, coding-style, security
- **Playbooks** (read on demand via `~/.claude/playbooks/`): gates/, project-board, testing, and others

### Before ANY Implementation

1. Search the project board for existing items
2. Enter plan mode (Gate 1)

## Key Rules Summary

- Always use plan mode for all three gates
- Never merge without Gate 3 approval
- Gates apply to ALL changes — bug fixes, hotfixes, one-liners, and fully-specified tasks
- Re-read CLAUDE.md at every gate
- One feature per session; commit and push after every meaningful unit of work
- **Core/adapter split:** `games.brennan.bogie.core` must never import Hytale types.
  All Hytale Server API usage lives in `games.brennan.bogie.hytale`.

---

## Gate 1 — Plan Approval

Before writing any code:
1. Enter plan mode (`EnterPlanMode`)
2. Explore the codebase and `docs/DESIGN.md`; check which phase the work belongs to
3. Write a plan covering: what will be built, which files change, risks, effort estimate
4. **API-surface check:** Hytale's Server API is Early Access and churns. If the plan relies
   on an unverified API (block mutation, entity attachment, persistence), call that out as a
   research risk and plan a spike first.
5. Present via `ExitPlanMode` and wait for user approval
6. After approval — rename branch from `claude/<auto-slug>` → `dev/<feature-slug>`

## Gate 2 — Testing Approval

After implementation is complete:
1. `./gradlew test` — core unit tests must pass (these run without HytaleServer.jar)
2. `./gradlew build` — requires `libraries/HytaleServer.jar` (from the local Hytale install)
3. In-game test: drop the built jar into the local Hytale server's plugin directory, start
   the server, connect with the Hytale client, exercise the feature
4. Screenshot evidence → `test-results/gate2-<feature-slug>-<YYYY-MM>.png`
5. Enter plan mode and present a Gate 2 Testing Report (build result, test summary,
   screenshots, step-by-step repro instructions, what passed/failed)
6. Wait for user approval

## Gate 3 — Merge Approval

1. Push branch, open PR with conventional commit title
2. Verify CI green
3. Squash-merge after explicit user approval
4. Delete feature branch
5. Bump version in `gradle.properties` per the versioning rule

---

## Versioning

SemVer in `gradle.properties` `mod_version` field.
- Every commit during dev → PATCH bump
- Feature merged to main (Gate 3) → MINOR bump (reset PATCH)
- Breaking API change for consumers → MAJOR bump

> The shipped versioning hook is npm-only and is NOT installed here. Bump
> `gradle.properties` `mod_version` manually before each commit.

## Releasing

No release automation exists yet (no CurseForge upload, no tags). When Bogie first ships a
usable phase, set up a dispatch-only release workflow modeled on dungeon-train-mc's
`release.yml` — do not hand-roll `git tag` pushes before then.
