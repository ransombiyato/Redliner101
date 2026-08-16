import type { Ability, AiRule } from "@/lib/guyvs/types";

export const abilityTypes: Ability["type"][] = ["dash", "projectile", "shockwave", "shield", "force"];
export const aiConditions: AiRule["condition"][] = ["enemyNear", "lowHealth", "timer", "random"];
export const aiActions: AiRule["action"][] = ["chase", "attack", "retreat", "ability", "wait"];

export function nextValue<T>(values: readonly T[], current: T): T {
  const index = values.indexOf(current);
  return values[(index + 1) % values.length] ?? values[0];
}
