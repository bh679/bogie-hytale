# Local dev server & in-game testing

Two ways to test Bogie in-game. Paths below assume the default macOS Hytale install.

## Option A — Singleplayer (easiest for manual Gate 2 testing)

Hytale runs the full server locally for singleplayer, so plugins work with zero setup:

1. Build: `./gradlew build`
2. Copy the jar into the launcher's mods directory:
   ```bash
   cp build/libs/bogie-hytale-<version>.jar \
     "$HOME/Library/Application Support/Hytale/UserData/Mods/"
   ```
3. Launch Hytale normally, open/create a world.
4. In chat: `/bogie_platform <block>` (tab-completes block keys), `/bogie_ride`, `/bogie_stop`.

Remove the jar from `UserData/Mods/` when done.

## Option B — Dedicated server (headless verification / multiplayer)

`run-server/` (gitignored) holds a copy of `HytaleServer.jar` from the install plus a
`mods/` directory containing our built jar.

```bash
cd run-server
"$HOME/.gradle/jdks/eclipse_adoptium-25-aarch64-os_x.2/jdk-25.0.3+9/Contents/Home/bin/java" \
  -jar HytaleServer.jar \
  --auth-mode offline \
  --assets "$HOME/Library/Application Support/Hytale/install/release/package/game/latest/Assets.zip" \
  --universe universe
```

Notes discovered while setting this up:

- **Java 25 required** (server manifest targets it; a Gradle-provisioned Temurin 25 works —
  path above; `ls ~/.gradle/jdks` to find yours).
- The server scans `./mods` by default — do **not** also pass `--mods mods` or every plugin
  loads twice and the server shuts down with `pluginDuplicate`.
- `--bare` boots plugin loading without worlds/ports — good for fast "does it load" checks.
  `--boot-command "stop"` makes it exit after boot.
- `manifest.json` `ServerVersion` must be a real version (e.g. `2026.03.26-89796e57b`, shown
  at boot); `"*"` triggers a compatibility warning that will become a hard error. The current
  install's version is what we pin.
- `--auth-mode offline` skips account auth for local testing; default is `AUTHENTICATED`.
- Default bind is `0.0.0.0:5520` (QUIC/UDP).
