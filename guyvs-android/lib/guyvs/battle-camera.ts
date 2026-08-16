import type { SimActor } from "@/lib/guyvs/simulation";

export type BattleCamera = {
  focusX: number;
  focusY: number;
  zoom: number;
  label: "Following one Guy" | "Framing two Guys" | "Wide arena";
};

const clamp = (value: number, min: number, max: number) => Math.max(min, Math.min(max, value));

/**
 * Gives one fighter a gentle follow, or keeps the midpoint of two fighters in
 * view. The zoom range is deliberately narrow to avoid a disorienting close-up.
 */
export function getBattleCamera(actors: SimActor[], stageWidth: number, stageHeight: number): BattleCamera {
  const live = actors.filter((actor) => actor.health > 0);
  if (live.length === 0) return { focusX: stageWidth / 2, focusY: stageHeight / 2, zoom: 0.94, label: "Wide arena" };
  if (live.length === 1) return { focusX: live[0].x, focusY: live[0].y, zoom: 1.04, label: "Following one Guy" };

  const left = Math.min(...live.map((actor) => actor.x));
  const right = Math.max(...live.map((actor) => actor.x));
  const top = Math.min(...live.map((actor) => actor.y));
  const bottom = Math.max(...live.map((actor) => actor.y));
  const spreadX = right - left;
  const spreadY = bottom - top;
  const comfortZoom = 1.08 - Math.max(spreadX / stageWidth, spreadY / stageHeight) * 0.28;
  return {
    focusX: (left + right) / 2,
    focusY: (top + bottom) / 2,
    zoom: clamp(comfortZoom, 0.9, 1.08),
    label: "Framing two Guys",
  };
}
