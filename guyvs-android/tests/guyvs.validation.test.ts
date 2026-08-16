import { describe, expect, it } from "vitest";

import { makeArena, makeGuy, makeStarterProject } from "../lib/guyvs/defaults";
import { isGuyVsProject } from "../lib/guyvs/validation";

describe("GuyVs project schema", () => {
  it("creates editable default Guy and arena assets", () => {
    const guy = makeGuy();
    const arena = makeArena();
    expect(guy.bodyParts).toHaveLength(2);
    expect(guy.attacks[0].damage).toBeGreaterThan(0);
    expect(arena.shapes[0].type).toBe("ring");
    expect(arena.zones[0].type).toBe("wind");
  });

  it("accepts a valid portable project and rejects malformed input", () => {
    const project = makeStarterProject();
    expect(isGuyVsProject(project)).toBe(true);
    expect(isGuyVsProject({ id: "missing-lists", name: "Broken" })).toBe(false);
    expect(isGuyVsProject(null)).toBe(false);
  });
});
