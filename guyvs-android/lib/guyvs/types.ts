export type Vec2 = { x: number; y: number };

export type BodyShape = "circle" | "rectangle" | "capsule" | "polygon";

export type BodyPart = {
  id: string;
  name: string;
  shape: BodyShape;
  position: Vec2;
  size: Vec2;
  rotation: number;
  mass: number;
  density: number;
  friction: number;
  bounce: number;
  color: string;
  parentId?: string;
};

export type DvdPhysics = {
  acceleration: number;
  maxSpeed: number;
  gravity: number;
  airControl: number;
  momentumPreservation: number;
  wallBounce: number;
  floorBounce: number;
  angularDamping: number;
  turningStrength: number;
};

export type Hitbox = {
  id: string;
  name: string;
  offset: Vec2;
  size: Vec2;
  damageMultiplier: number;
  knockbackMultiplier: number;
  hitstun: number;
  color: string;
};

export type Attack = {
  id: string;
  name: string;
  damage: number;
  knockback: number;
  cooldown: number;
  windup: number;
  active: number;
  recovery: number;
  color: string;
};

export type Ability = {
  id: string;
  name: string;
  type: "dash" | "projectile" | "shockwave" | "shield" | "force";
  cooldown: number;
  strength: number;
  duration: number;
  color: string;
};

export type AiRule = {
  id: string;
  condition: "enemyNear" | "lowHealth" | "timer" | "random";
  action: "chase" | "attack" | "retreat" | "ability" | "wait";
  value: number;
};

export type AnimationTrack = {
  id: string;
  name: string;
  duration: number;
  loop: boolean;
  keyframes: { time: number; rotation: number; scale: number }[];
};

export type VisualEffect = {
  id: string;
  name: string;
  particleCount: number;
  lifetime: number;
  speed: number;
  color: string;
};

export type Guy = {
  id: string;
  name: string;
  health: number;
  bodyParts: BodyPart[];
  dvd: DvdPhysics;
  hitboxes: Hitbox[];
  attacks: Attack[];
  abilities: Ability[];
  ai: AiRule[];
  animations: AnimationTrack[];
  effects: VisualEffect[];
  accent: string;
  updatedAt: number;
};

export type ArenaShape = {
  id: string;
  type: "rectangle" | "circle" | "triangle" | "polygon" | "ring";
  points: Vec2[];
  position: Vec2;
  size: Vec2;
  rotation: number;
  bounce: number;
  friction: number;
  color: string;
};

export type PhysicsZone = {
  id: string;
  type: "lowGravity" | "highGravity" | "wind" | "bounce" | "slow";
  position: Vec2;
  size: Vec2;
  strength: number;
  color: string;
};

export type Arena = {
  id: string;
  name: string;
  background: string;
  gravity: number;
  shapes: ArenaShape[];
  zones: PhysicsZone[];
  updatedAt: number;
};

export type BattleSetup = {
  id: string;
  name: string;
  arenaId: string;
  guyIds: string[];
  lives: number;
  roundTime: number;
};

export type GuyVsProject = {
  id: string;
  name: string;
  description: string;
  createdAt: number;
  updatedAt: number;
  guys: Guy[];
  arenas: Arena[];
  battles: BattleSetup[];
};
