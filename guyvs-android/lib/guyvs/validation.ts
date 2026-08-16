import type { GuyVsProject } from "@/lib/guyvs/types";

export function isGuyVsProject(value: unknown): value is GuyVsProject {
  if (!value || typeof value !== "object") return false;
  const candidate = value as Partial<GuyVsProject>;
  return Boolean(
    typeof candidate.id === "string" &&
    typeof candidate.name === "string" &&
    Array.isArray(candidate.guys) &&
    Array.isArray(candidate.arenas) &&
    Array.isArray(candidate.battles),
  );
}
