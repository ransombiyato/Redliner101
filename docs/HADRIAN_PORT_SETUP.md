# Using DemiForge with Hadrian’s Android Deltarune Port

## What this build actually does

DemiForge is now a real **user-selected external-workspace mod manager** for Hadrian’s Android port. It imports your mod package, checks dependencies and conflicts, shows the exact payload files it intends to replace, creates a local backup, writes replacement payloads through Android’s directory permission, and can restore the original payloads afterward.

It does not contain Deltarune assets, does not download game files, does not alter the installed APK, and does not access protected app storage without a directory permission that **you** grant in Android’s picker. Those are intentional boundaries, not a demo mode.

## Before you begin

Hadrian’s public release describes an external save-path option and credits an Android `game_change` / `load_wad` extension.[1] Related current Android Deltarune tooling uses Android-ready `game.droid` payloads and chapter package structures, but it is a separate launcher and does not establish Hadrian’s exact app package or folder.[2]

Consequently, DemiForge does not guess a directory. It looks only in the folder you select for these Android-port payload candidates:

| Detected file | What DemiForge can do |
|---|---|
| `game.droid` | Back up and replace it with a compatible Android-ready mod payload. |
| `data.droid` | Back up and replace it with a compatible Android-ready mod payload. |
| Any `.wad` file | Back up and replace that exact WAD when the port exposes it in the selected workspace. |

If the system picker cannot open the port folder, or the selected folder contains none of these files, then that installed version does not expose an accessible game-payload workspace to DemiForge. In that situation **do not force it**: the app cannot safely modify an APK or private directory, and there is no honest file-management solution until the port exposes the data externally.

## Installation workflow

Open **DemiForge** and choose **Select Hadrian port workspace**. In Android’s directory picker, select only the external directory that your installed port uses for its game payloads. Read the discovered paths, file types, sizes, and Workspace Layout ID. If those paths belong to the port, choose **I reviewed these paths — trust workspace**.

Next, create a folder from `examples/hadrian-port-mod-template`. Add your own **Android-ready** replacement payload under `payload/`, then update the template’s `target` to match one discovered Workspace path exactly. Import that folder through the **Mods** tab and enable the package.

If a mod author distributes the same package as a `.zip`, use **Import .zip mod package** instead. DemiForge extracts the archive locally, rejects path traversal, enforces entry and expanded-size limits, and still validates `manifest.json` before the package reaches your mod library.

Choose **Preview enabled mod changes**. DemiForge refuses the operation unless all dependencies resolve, the manifest targets `deltarune-hadrian-android`, every target is an already-discovered payload, every requested payload is writable, no two enabled mods replace the same file, and Safe Mode is disabled. The preview displays the original bytes to back up and the replacement bytes to write. Choose the final apply button only after reviewing that list.

To undo a successful installation, choose **Restore latest backup**. Restoration is bound to the same selected directory and its same visible payload layout, so a backup cannot accidentally be restored into a different folder.

## Mod compatibility

PC Deltarune mods often ship as `data.win`; Android ports require a port-compatible Android payload such as `game.droid` or an appropriate chapter WAD. The conversion and GameMaker-version adaptation are separate porting work, and DemiForge deliberately does **not** pretend that copying an arbitrary `data.win` into Android will work.[2] The mod author or porter must supply a replacement that matches the port/chapter build you are using.

> A successful preview means DemiForge can safely perform the listed file operation. It does not guarantee that an unrelated mod is compatible with your Deltarune chapter, GameMaker runtime, or the particular Hadrian release revision.

## References

[1]: https://gamejolt.com/games/deltarunech1-5androidport/1080568 "Deltarune Chapter 1–5 Android Port — Hadrian Soares, Game Jolt"
[2]: https://github.com/BookerRues9/Deltaquick-porting-tools "DeltaQuick Porting Tools — Android Deltarune payload format and compatibility guide"
