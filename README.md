# Mindustry Engine & Expanded Core

An expanded, unified monorepo distribution of **Mindustry** — the open-source automation tower defense RTS.

---

## Overview & Architecture

This repository combines the **Mindustry** game and the core **Arc** game engine into a single unified monorepo with an integrated build pipeline, pre-emptive rendering safeguards, and expanded content integrations.

```
Mindustry/
├── Arc/                   # Core Arc graphics & game engine subproject
├── Mindustry/              # Game source, assets, and platform launchers
│   ├── core/              # Game logic, world generation, content, & UI
│   ├── desktop/           # Desktop launcher & packaging
│   ├── android/           # Android launcher & app sources
│   ├── server/            # Dedicated server target
│   └── tests/             # Comprehensive JUnit test suite
├── DOCUMENTATION/         # Detailed repository documentation & guides
├── output/                # Consolidated output directory for builds
└── build.sh               # Unified command-line build interface
```

---

## Quick Start & Building (`./build.sh`)

Building and testing is managed through the top-level `./build.sh` script.

### Requirements
- **JDK 21+** (e.g. OpenJDK 21 / Temurin 21 or above)

### Common Commands

| Command | Description | Staged Output |
| :--- | :--- | :--- |
| `./build.sh desktop` | Compiles & packages the runnable Desktop JAR | `output/desktop/Mindustry.jar` |
| `./build.sh server` | Compiles the headless dedicated server | `output/server/server.jar` |
| `./build.sh android` | Assembles Android APKs (requires `ANDROID_HOME`) | `output/android/` |
| `./build.sh test` | Runs the full JUnit test suite across all modules | — |
| `./build.sh clean` | Cleans Gradle build caches and the `output/` directory | — |

---

## Running the Game

After running `./build.sh desktop`, launch the desktop JAR:

```bash
java -jar output/desktop/Mindustry.jar
```

---

## Documentation

Detailed documentation and contribution guides are located in the [`DOCUMENTATION/`](DOCUMENTATION/) directory:

- [Contributing Guidelines](DOCUMENTATION/CONTRIBUTING.md) — Code style and pull request guidelines.
- [Mod Attribution & Credits](DOCUMENTATION/MOD_ATTRIBUTION.md) — Attributions and source links for integrated community content.
- [Issue Guidelines](DOCUMENTATION/ISSUES.md) — Bug reporting procedures.
- [Translating Guide](DOCUMENTATION/TRANSLATING.md) — Localization instructions.
- [Server List](DOCUMENTATION/SERVERLIST.md) — Official server directory details.

---

## License

Mindustry and Arc are licensed under the GNU General Public License v3.0 (GPL-3.0). See [Mindustry/LICENSE](LICENSE) for the full text.
