from pathlib import Path
import json, re, zipfile, sys

root = Path('/home/ubuntu/Redliner101')
resources = root / 'src/main/resources'
errors = []

blocks = re.findall(r'BLOCKS\.register\("([a-z0-9_]+)"', (root/'src/main/java/com/ransombiyato/createkineticfrontier/registry/ModBlocks.java').read_text())
items = re.findall(r'ITEMS\.register\("([a-z0-9_]+)"', (root/'src/main/java/com/ransombiyato/createkineticfrontier/registry/ModItems.java').read_text())
standalone_items = ['momentum_hook', 'momentum_meter', 'cargo_capsule', 'impact_capsule', 'signal_capsule', 'utility_capsule']
be_types = re.findall(r'BLOCK_ENTITIES\.register\("([a-z0-9_]+)"', (root/'src/main/java/com/ransombiyato/createkineticfrontier/registry/ModBlockEntities.java').read_text())

for name in blocks:
    for rel in [f'assets/createkineticfrontier/blockstates/{name}.json', f'assets/createkineticfrontier/models/block/{name}.json', f'assets/createkineticfrontier/models/item/{name}.json', f'assets/createkineticfrontier/textures/block/{name}.png']:
        if not (resources/rel).exists(): errors.append(f'missing {rel}')
for name in items:
    for rel in [f'assets/createkineticfrontier/models/item/{name}.json']:
        if not (resources/rel).exists(): errors.append(f'missing {rel}')
for name in standalone_items:
    for rel in [f'assets/createkineticfrontier/models/item/{name}.json', f'assets/createkineticfrontier/textures/item/{name}.png']:
        if not (resources/rel).exists(): errors.append(f'missing {rel}')

for path in resources.rglob('*.json'):
    try: json.loads(path.read_text())
    except Exception as exc: errors.append(f'invalid JSON {path}: {exc}')

for recipe in (resources/'data/createkineticfrontier/recipes').glob('*.json'):
    data = json.loads(recipe.read_text())
    result = data.get('result', {}).get('id')
    if result and result.split(':')[-1] not in items: errors.append(f'recipe result not registered: {recipe} -> {result}')

jar = root/'build/libs/createkineticfrontier-1.0.0.jar'
expected = [
    'META-INF/neoforge.mods.toml',
    'com/ransombiyato/createkineticfrontier/KineticFrontier.class',
    'com/ransombiyato/createkineticfrontier/client/ClientKineticFrontier.class',
    'assets/createkineticfrontier/lang/en_us.json',
    'data/createkineticfrontier/recipes/kinetic_core.json',
    'data/createkineticfrontier/advancements/root.json',
]
if not jar.exists(): errors.append('missing build/libs/createkineticfrontier-1.0.0.jar')
else:
    with zipfile.ZipFile(jar) as z:
        names = set(z.namelist())
        for rel in expected:
            if rel not in names: errors.append(f'JAR missing {rel}')

source_text = '\n'.join(p.read_text(errors='ignore') for p in (root/'src/main/java').rglob('*.java'))
for term in ['TODO', 'FIXME', 'placeholder', 'dummy']:
    if term.lower() in source_text.lower(): errors.append(f'source contains forbidden audit term: {term}')

print(f'blocks={len(blocks)} items={len(items)} block_entities={len(be_types)}')
print(f'json_files={len(list(resources.rglob("*.json")))} textures={len(list(resources.rglob("*.png")))}')
print(f'jar={jar.stat().st_size if jar.exists() else 0} bytes')
if errors:
    print('FAIL')
    print('\n'.join(errors))
    sys.exit(1)
print('PASS: registrations, JSON, assets, and JAR checks completed')
