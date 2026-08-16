import { describe, expect, it } from "vitest";
import { stepBattle } from "../lib/guyvs/simulation";

const profile = { acceleration: 820, maxSpeed: 440, gravity: 860, airControl: 0.7, momentumPreservation: 0.8, wallBounce: 0.9, floorBounce: 0.8, angularDamping: 0.8, turningStrength: 0.7 };

describe("GuyVs battle step", () => {
  it("keeps an actor inside the arena and bounces it from the floor", () => {
    const [actor] = stepBattle([{ id: "a", x: 40, y: 90, vx: 0, vy: 200, radius: 16, health: 100, impact: 0 }], { a: profile }, { width: 120, height: 120, floor: 120 }, 0.2);
    expect(actor.y).toBeLessThanOrEqual(104);
    expect(actor.vy).toBeLessThan(0);
  });

  it("resolves overlapping fighter bodies as a collision", () => {
    const actors = stepBattle([{ id: "a", x: 50, y: 50, vx: 80, vy: 0, radius: 16, health: 100, impact: 0 }, { id: "b", x: 60, y: 50, vx: -80, vy: 0, radius: 16, health: 100, impact: 0 }], { a: profile, b: profile }, { width: 140, height: 120, floor: 120 }, 0.016);
    expect(actors[0].impact).toBeGreaterThan(0);
    expect(Math.hypot(actors[1].x - actors[0].x, actors[1].y - actors[0].y)).toBeGreaterThanOrEqual(31.9);
  });

  it("keeps a dense 48-actor simulation bounded over repeated steps", () => {
    let actors = Array.from({ length: 48 }, (_, index) => ({ id: `actor-${index}`, x: 18 + (index % 12) * 20, y: 18 + Math.floor(index / 12) * 22, vx: index % 2 ? 180 : -180, vy: 0, radius: 8, health: 100, impact: 0 }));
    const profiles = Object.fromEntries(actors.map((actor) => [actor.id, profile]));
    for (let frame = 0; frame < 90; frame += 1) actors = stepBattle(actors, profiles, { width: 280, height: 220, floor: 220 }, 0.016);
    expect(actors).toHaveLength(48);
    expect(actors.every((actor) => actor.x >= actor.radius && actor.x <= 280 - actor.radius && actor.y >= actor.radius && actor.y <= 220 - actor.radius)).toBe(true);
  });
});
