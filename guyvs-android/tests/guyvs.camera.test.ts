import { describe, expect, it } from "vitest";
import { getBattleCamera } from "../lib/guyvs/battle-camera";

describe("GuyVs battle camera", () => {
  it("gently follows one live Guy without excessive zoom", () => {
    const camera = getBattleCamera([{ id: "solo", x: 80, y: 140, vx: 0, vy: 0, radius: 18, health: 100, impact: 0 }], 332, 380);
    expect(camera).toMatchObject({ focusX: 80, focusY: 140, label: "Following one Guy" });
    expect(camera.zoom).toBeGreaterThanOrEqual(0.9);
    expect(camera.zoom).toBeLessThanOrEqual(1.08);
  });

  it("centers the midpoint of two live Guys and keeps zoom capped", () => {
    const camera = getBattleCamera([
      { id: "left", x: 70, y: 120, vx: 0, vy: 0, radius: 18, health: 100, impact: 0 },
      { id: "right", x: 270, y: 200, vx: 0, vy: 0, radius: 18, health: 100, impact: 0 },
    ], 332, 380);
    expect(camera).toMatchObject({ focusX: 170, focusY: 160, label: "Framing two Guys" });
    expect(camera.zoom).toBeGreaterThanOrEqual(0.9);
    expect(camera.zoom).toBeLessThanOrEqual(1.08);
  });
});
