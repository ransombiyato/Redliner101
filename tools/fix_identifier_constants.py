from pathlib import Path
import re

root = Path(__file__).resolve().parents[1]
for base in (root / "Common/src/main/java", root / "Fabric/src/main/java"):
    for path in base.rglob("*"):
        if path.suffix not in {".java", ".kt"}:
            continue
        text = path.read_text()
        updated = re.sub(r"Identifier\.STREAM(?:_CODEC)?(?:_CODEC)?", "Identifier.STREAM_CODEC", text)
        if updated != text:
            path.write_text(updated)
