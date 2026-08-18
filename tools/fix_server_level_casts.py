from pathlib import Path
import re

root = Path(__file__).resolve().parents[1]
pattern = re.compile(r"net\.minecraft\.world\.entity\.ai\.goal\.Goal\.getServerLevel\(([^()]+)\)")
for base in (root / "Common/src/main/java", root / "Fabric/src/main/java"):
    for path in base.rglob("*"):
        if path.suffix not in {".java", ".kt"}:
            continue
        text = path.read_text()
        if path.suffix == ".kt":
            updated = pattern.sub(r"(\1.level() as net.minecraft.server.level.ServerLevel)", text)
        else:
            updated = pattern.sub(r"((net.minecraft.server.level.ServerLevel) \1.level())", text)
        if updated != text:
            path.write_text(updated)
