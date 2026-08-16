# Mod Format

Each mod is an ordinary directory with a required `manifest.json` file and optional payload files. The directory can be copied or imported into DemiForge’s mod storage. DemiForge does not execute scripts, native code, or arbitrary archives from a mod.

```text
example-mod/
├── manifest.json
└── payload/
    └── greeting.txt
```

## Required fields

| Field | Meaning |
|---|---|
| `schemaVersion` | Currently `1`. |
| `id` | Lowercase identifier matching `[a-z][a-z0-9_.-]{2,63}`. |
| `name`, `author`, `version` | User-facing metadata. |
| `targetGame` | Adapter ID, for example `dummy-game` or `deltarune`. |
| `supportedGameVersions` | Exact version, `*`, a `major.minor.*` prefix, or `>=version`. |

## Optional fields

| Field | Meaning |
|---|---|
| `description` | User-facing text. |
| `dependencies` | Objects with `id`, optional `minVersion`, and optional `required`. |
| `conflicts` | Objects with `id` and optional `reason`. |
| `loadAfter`, `loadBefore` | IDs that constrain deterministic load order. |
| `patches` | A list of source/target operations. |
| `configuration` | String key-value settings reserved for adapter use. |

Patch `source` and `target` paths must be relative and cannot contain `..` or begin with `/`. `OVERLAY` writes to a `.demiforge-overlay` layer under the adapter’s target root. `COPY` writes to the target path after recording a backup. A failed transaction restores the affected copy targets.

See `examples/mods/` for valid overlay and copy-patch samples and an intentionally invalid manifest for validator testing.
