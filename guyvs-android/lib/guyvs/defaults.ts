import type { Arena, DvdPhysics, Guy, GuyVsProject } from "@/lib/guyvs/types";

export const uid = (prefix: string) => `${prefix}-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`;

export type BuiltInGuyTemplate = {
  key: string;
  name: string;
  portraitKey: NonNullable<Guy["portraitKey"]>;
  accent: string;
  health: number;
  dvd: Partial<DvdPhysics>;
  attack: { name: string; damage: number; knockback: number };
  ability: { name: string; type: Guy["abilities"][number]["type"]; strength: number };
};

export const BUILT_IN_GUY_ROSTER: BuiltInGuyTemplate[] = [
  { key: "sprinter", name: "Sprint Frame", portraitKey: "sprinter", accent: "#22D3EE", health: 94, dvd: { acceleration: 1100, maxSpeed: 570, gravity: 780, floorBounce: 0.9 }, attack: { name: "Vector Rush", damage: 7, knockback: 390 }, ability: { name: "Burst Step", type: "dash", strength: 760 } },
  { key: "anchor", name: "Anchor Frame", portraitKey: "stoic", accent: "#FBBF24", health: 132, dvd: { acceleration: 580, maxSpeed: 330, gravity: 980, wallBounce: 0.78, floorBounce: 0.62 }, attack: { name: "Ground Check", damage: 13, knockback: 280 }, ability: { name: "Mass Shield", type: "shield", strength: 920 } },
  { key: "arc", name: "Arc Frame", portraitKey: "arc", accent: "#A3E635", health: 100, dvd: { acceleration: 780, maxSpeed: 440, gravity: 700, wallBounce: 1.02, floorBounce: 1.02 }, attack: { name: "Arc Pop", damage: 9, knockback: 350 }, ability: { name: "Field Push", type: "force", strength: 690 } },
  { key: "dash", name: "Dash Frame", portraitKey: "dash", accent: "#F472B6", health: 88, dvd: { acceleration: 1250, maxSpeed: 620, gravity: 840, momentumPreservation: 0.96 }, attack: { name: "Afterimage Jab", damage: 6, knockback: 420 }, ability: { name: "Double Dash", type: "dash", strength: 840 } },
  { key: "comet", name: "Comet Frame", portraitKey: "sprinter", accent: "#FB7185", health: 98, dvd: { acceleration: 900, maxSpeed: 520, gravity: 620, floorBounce: 1.06 }, attack: { name: "Comet Kick", damage: 10, knockback: 430 }, ability: { name: "Sky Shock", type: "shockwave", strength: 640 } },
  { key: "prism", name: "Prism Frame", portraitKey: "dash", accent: "#C084FC", health: 104, dvd: { acceleration: 760, maxSpeed: 460, gravity: 790, turningStrength: 0.96 }, attack: { name: "Prism Strike", damage: 9, knockback: 360 }, ability: { name: "Pulse Shot", type: "projectile", strength: 590 } },
  { key: "stone", name: "Stone Frame", portraitKey: "stoic", accent: "#94A3B8", health: 142, dvd: { acceleration: 520, maxSpeed: 300, gravity: 1080, wallBounce: 0.7, floorBounce: 0.58 }, attack: { name: "Stone Swing", damage: 15, knockback: 300 }, ability: { name: "Brace", type: "shield", strength: 980 } },
  { key: "echo", name: "Echo Frame", portraitKey: "arc", accent: "#38BDF8", health: 92, dvd: { acceleration: 980, maxSpeed: 500, gravity: 760, airControl: 0.94 }, attack: { name: "Echo Tap", damage: 8, knockback: 380 }, ability: { name: "Echo Wave", type: "shockwave", strength: 670 } },
];

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

export function makeBuiltInGuy(template: BuiltInGuyTemplate): Guy {
  const guy = makeGuy(template.name);
  return {
    ...guy,
    portraitKey: template.portraitKey,
    accent: template.accent,
    health: template.health,
    dvd: { ...guy.dvd, ...template.dvd },
    bodyParts: guy.bodyParts.map((part, index) => ({ ...part, color: index === 0 ? template.accent : part.color })),
    attacks: [{ ...guy.attacks[0], name: template.attack.name, damage: template.attack.damage, knockback: template.attack.knockback, color: template.accent }],
    abilities: [{ ...guy.abilities[0], name: template.ability.name, type: template.ability.type, strength: template.ability.strength, color: template.accent }],
    effects: [{ ...guy.effects[0], name: `${template.name} impact`, color: template.accent }],
  };
}

export function makeBuiltInRoster(): Guy[] {
  return BUILT_IN_GUY_ROSTER.map(makeBuiltInGuy);
}

export function makeArena(name = "Neon Ring"): Arena {
  const now = Date.now();
  return {
    id: uid("arena"),
    name,
    background: "#111827",
    gravity: 860,
    updatedAt: now,
    shapes: [{ id: uid("shape"), type: "ring", points: [], position: { x: 180, y: 230 }, size: { x: 300, y: 360 }, rotation: 0, bounce: 0.92, friction: 0.25, color: "#22D3EE" }],
    zones: [{ id: uid("zone"), type: "wind", position: { x: 180, y: 130 }, size: { x: 110, y: 72 }, strength: 180, color: "#A3E635" }],
  };
}

export function makeStarterProject(): GuyVsProject {
  const now = Date.now();
  const guys = makeBuiltInRoster();
  const arena = makeArena();
  return {
    id: uid("project"),
    name: "GuyVs Starter Lab",
    description: "A remixable physics-fighter project with an editable starter roster.",
    createdAt: now,
    updatedAt: now,
    guys,
    arenas: [arena],
    battles: [{ id: uid("battle"), name: "Starter Duel", arenaId: arena.id, guyIds: guys.slice(0, 2).map((guy) => guy.id), lives: 3, roundTime: 90 }],
  };
}
