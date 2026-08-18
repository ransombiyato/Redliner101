from pathlib import Path

root = Path(__file__).resolve().parents[1]
for base in (root / "Common/src/main/java", root / "Fabric/src/main/java"):
    for path in base.rglob("*"):
        if path.suffix not in {".java", ".kt"}:
            continue
        text = path.read_text()
        updated = text.replace("ResourceLocation", "Identifier")
        if updated != text:
            path.write_text(updated)
