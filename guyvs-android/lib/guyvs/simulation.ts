import type { DvdPhysics } from "@/lib/guyvs/types";

export type SimActor = { id: string; x: number; y: number; vx: number; vy: number; radius: number; health: number; impact: number };
export type SimBounds = { width: number; height: number; floor: number };

export function stepBattle(actors: SimActor[], profiles: Record<string, DvdPhysics>, bounds: SimBounds, deltaSeconds: number): SimActor[] {
  const next = actors.map((actor) => {
    const profile = profiles[actor.id];
    const maxSpeed = profile?.maxSpeed ?? 440;
    let vx = Math.max(-maxSpeed, Math.min(maxSpeed, actor.vx));
    let vy = actor.vy + (profile?.gravity ?? 860) * deltaSeconds;
    let x = actor.x + vx * deltaSeconds;
    let y = actor.y + vy * deltaSeconds;
    if (x < actor.radius || x > bounds.width - actor.radius) {
      x = Math.max(actor.radius, Math.min(bounds.width - actor.radius, x));
      vx *= -(profile?.wallBounce ?? 0.94);
    }
    if (y < actor.radius) { y = actor.radius; vy = Math.abs(vy) * 0.72; }
    if (y > bounds.floor - actor.radius) { y = bounds.floor - actor.radius; vy *= -(profile?.floorBounce ?? 0.82); }
    return { ...actor, x, y, vx, vy, health: Math.max(0, actor.health), impact: Math.max(0, actor.impact - deltaSeconds) };
  });

  for (let left = 0; left < next.length; left += 1) {
    for (let right = left + 1; right < next.length; right += 1) {
      const a = next[left]; const b = next[right];
      const dx = b.x - a.x; const dy = b.y - a.y; const distance = Math.hypot(dx, dy) || 0.001; const minimum = a.radius + b.radius;
      if (distance < minimum) {
        const nx = dx / distance; const ny = dy / distance; const overlap = (minimum - distance) / 2;
        a.x -= nx * overlap; a.y -= ny * overlap; b.x += nx * overlap; b.y += ny * overlap;
        const relativeSpeed = Math.hypot(a.vx - b.vx, a.vy - b.vy);
        const damage = Math.min(18, Math.max(1, Math.round(relativeSpeed * 0.018)));
        const swapX = a.vx; const swapY = a.vy;
        a.vx = b.vx * 0.78 - nx * 80; a.vy = b.vy * 0.78 - ny * 80;
        b.vx = swapX * 0.78 + nx * 80; b.vy = swapY * 0.78 + ny * 80;
        a.health = Math.max(0, a.health - damage); b.health = Math.max(0, b.health - damage);
        a.impact = 0.22; b.impact = 0.22;
      }
    }
  }
  return next;
}
