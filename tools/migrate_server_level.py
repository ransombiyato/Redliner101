from pathlib import Path
import re

root = Path(__file__).resolve().parents[1]
pattern = re.compile(r"\b([A-Za-z_][A-Za-z0-9_]*)\.serverLevel\(\)")
for base in (root / "Common/src/main/java", root / "Fabric/src/main/java"):
    for path in base.rglob("*"):
        if path.suffix not in {".java", ".kt"}:
            continue
        text = path.read_text()
        updated = pattern.sub(r"net.minecraft.world.entity.ai.goal.Goal.getServerLevel(\1)", text)
        if updated != text:
            path.write_text(updated)
