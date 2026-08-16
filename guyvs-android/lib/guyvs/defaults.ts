import type { Arena, Guy, GuyVsProject } from "@/lib/guyvs/types";

export const uid = (prefix: string) => `${prefix}-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`;

export function makeGuy(name = "Neon Runner"): Guy {
  const now = Date.now();
  return {
    id: uid("guy"),
    name,
    health: 100,
    accent: "#22D3EE",
    updatedAt: now,
    bodyParts: [
      { id: uid("part"), name: "Core", shape: "capsule", position: { x: 0, y: 0 }, size: { x: 34, y: 64 }, rotation: 0, mass: 2, density: 1, friction: 0.3, bounce: 0.75, color: "#22D3EE" },
      { id: uid("part"), name: "Head", shape: "circle", position: { x: 0, y: -48 }, size: { x: 34, y: 34 }, rotation: 0, mass: 1, density: 1, friction: 0.3, bounce: 0.8, color: "#F8FAFC" },
    ],
    dvd: { acceleration: 820, maxSpeed: 440, gravity: 860, airControl: 0.72, momentumPreservation: 0.88, wallBounce: 0.94, floorBounce: 0.82, angularDamping: 0.86, turningStrength: 0.74 },
    hitboxes: [{ id: uid("hit"), name: "Core hit", offset: { x: 26, y: 0 }, size: { x: 34, y: 24 }, damageMultiplier: 1, knockbackMultiplier: 1, hitstun: 180, color: "#F472B6" }],
    attacks: [{ id: uid("attack"), name: "Vector Jab", damage: 8, knockback: 330, cooldown: 0.55, windup: 0.12, active: 0.1, recovery: 0.3, color: "#F472B6" }],
    abilities: [{ id: uid("ability"), name: "Momentum Dash", type: "dash", cooldown: 2.5, strength: 680, duration: 0.2, color: "#A3E635" }],
    ai: [{ id: uid("ai"), condition: "enemyNear", action: "attack", value: 160 }, { id: uid("ai"), condition: "lowHealth", action: "retreat", value: 25 }],
    animations: [{ id: uid("animation"), name: "Glide Idle", duration: 0.8, loop: true, keyframes: [{ time: 0, rotation: -3, scale: 1 }, { time: 0.4, rotation: 3, scale: 1.05 }, { time: 0.8, rotation: -3, scale: 1 }] }],
    effects: [{ id: uid("effect"), name: "Impact Sparks", particleCount: 18, lifetime: 0.42, speed: 240, color: "#F472B6" }],
  };
}

export function makeArena(name = "Neon Ring"): Arena {
  const now = Date.now();
  return {
    id: uid("arena"),
    name,
    background: "#111827",
    gravity: 860,
    updatedAt: now,
    shapes: [
      { id: uid("shape"), type: "ring", points: [], position: { x: 180, y: 230 }, size: { x: 300, y: 360 }, rotation: 0, bounce: 0.92, friction: 0.25, color: "#22D3EE" },
    ],
    zones: [{ id: uid("zone"), type: "wind", position: { x: 180, y: 130 }, size: { x: 110, y: 72 }, strength: 180, color: "#A3E635" }],
  };
}

export function makeStarterProject(): GuyVsProject {
  const now = Date.now();
  const guy = makeGuy();
  const arena = makeArena();
  return {
    id: uid("project"),
    name: "Untitled Lab",
    description: "A remixable physics-fighter project.",
    createdAt: now,
    updatedAt: now,
    guys: [guy],
    arenas: [arena],
    battles: [{ id: uid("battle"), name: "Quick Test", arenaId: arena.id, guyIds: [guy.id], lives: 3, roundTime: 90 }],
  };
}
