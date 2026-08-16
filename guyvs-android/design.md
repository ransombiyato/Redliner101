# GuyVs Maker Pro Maxx Ultra k41 — Mobile Interface Design

## Product Vision

GuyVs Maker Pro Maxx Ultra k41 is a touch-first creator studio for configurable 2D physics battles. The Android experience balances an editor’s depth with phone-size controls: users build fighters, attacks, abilities, AI behaviors, arenas, rules, and assets, then test them in a live battle laboratory without losing their work.

## Screen List

| Screen | Primary content and functionality |
| --- | --- |
| Home | Recent projects, templates, quick actions, active project summary, and recovery/autosave entry points. |
| Create | A creator hub leading to Guy, Attack, Ability, AI, Arena, Effect, Animation, and Rule editors. |
| Guy Editor | Body-part hierarchy, touch canvas, appearance, physics, DVD movement, hitbox, and inspection controls. |
| Attack & Ability Editors | Timeline/component inspectors for damage, knockback, cooldowns, events, and reusable effect modules. |
| AI Editor | A touch-friendly behavior graph using conditions and actions, with editable variables and preview traces. |
| Arena Editor | Shape/ring canvas, object list, physics zones, collision settings, and background tools. |
| Battle Lab | A live simulation with multiple combatants, pause, slow motion, frame stepping, debug overlays, and live edits. |
| Assets | Searchable local library of Guys, arenas, attacks, abilities, rules, effects, and templates. |
| Workshop | Import, export, remix, duplicate, and project backup tools. |
| Settings | Graphics, audio, controls, accessibility, storage, performance, and developer/debug controls. |

## Mobile Layout

The app is designed for a 9:16 portrait device. A persistent bottom navigation provides Home, Create, Guys, Arenas, and Battles; an overflow sheet exposes Assets, Workshop, and Settings. Creation screens use a compact top bar for undo, redo, play/test, and save. On larger screens or landscape, the canvas expands while an inspector occupies the right edge. Every editor keeps its main action in the reachable lower half of the screen and exposes additional properties through grouped, collapsible sheets.

## Key User Flows

| Flow | Steps |
| --- | --- |
| Create and test a fighter | Home → Create → Guy Editor → adjust body and DVD physics → save asset → Battle Lab → spawn Guy → test and live-edit. |
| Create an arena | Create → Arena Editor → choose or draw ring geometry → configure surfaces/zones/objects → save → Battle Lab. |
| Build combat logic | Create → Attack or Ability Editor → edit events and effects → assign to Guy → test in Battle Lab. |
| Preserve and reuse work | Any editor → Save/Autosave → Assets → duplicate/remix/export → import or reopen later. |

## Color Choices

| Token | Color | Use |
| --- | --- | --- |
| Ink | `#111827` | Dark simulation canvas, structure, and primary text. |
| Electric cyan | `#22D3EE` | Active creation actions, selected vectors, and physics indicators. |
| Hot magenta | `#F472B6` | Attack, hitbox, and warning emphasis. |
| Acid lime | `#A3E635` | Live state, success, and launch-vector emphasis. |
| Fog | `#E5E7EB` | High-legibility light surface and inspector fields. |

## Interaction Standards

Canvases support drag-to-move and pinch-to-zoom when implementation platform support permits. Long-press surfaces open context actions, destructive actions require confirmation, and every editor provides visible undo, redo, reset, duplicate, and save controls. Numeric controls combine slider and direct entry paths for accurate tuning.
