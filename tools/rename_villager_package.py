from pathlib import Path

root = Path(__file__).resolve().parents[1]
replacements = {
    "net.minecraft.world.entity.npc.AbstractVillager": "net.minecraft.world.entity.npc.villager.AbstractVillager",
    "net.minecraft.world.entity.npc.Villager": "net.minecraft.world.entity.npc.villager.Villager",
    "net.minecraft.world.entity.npc.VillagerData": "net.minecraft.world.entity.npc.villager.VillagerData",
    "net.minecraft.world.entity.npc.VillagerProfession": "net.minecraft.world.entity.npc.villager.VillagerProfession",
    "net.minecraft.world.entity.npc.VillagerTrades": "net.minecraft.world.entity.npc.villager.VillagerTrades",
    "net.minecraft.world.entity.npc.VillagerType": "net.minecraft.world.entity.npc.villager.VillagerType",
}
for base in (root / "Common/src/main/java", root / "Fabric/src/main/java"):
    for path in base.rglob("*"):
        if path.suffix not in {".java", ".kt"}:
            continue
        text = path.read_text()
        updated = text
        for old, new in replacements.items():
            updated = updated.replace(old, new)
        if updated != text:
            path.write_text(updated)
