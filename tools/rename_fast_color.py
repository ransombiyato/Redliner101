from pathlib import Path

root = Path(__file__).resolve().parents[1]
for base in (root / "Common/src/main/java", root / "Fabric/src/main/java"):
    for path in base.rglob("*"):
        if path.suffix not in {".java", ".kt"}:
            continue
        text = path.read_text()
        updated = text.replace("import net.minecraft.util.FastColor.ARGB32", "import net.minecraft.util.ARGB")
        updated = updated.replace("import net.minecraft.util.FastColor", "import net.minecraft.util.ARGB")
        updated = updated.replace("FastColor.ARGB32", "ARGB")
        if updated != text:
            path.write_text(updated)
