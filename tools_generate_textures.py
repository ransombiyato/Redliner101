from pathlib import Path
from PIL import Image, ImageDraw

ROOT = Path('/home/ubuntu/Redliner101/src/main/resources/assets/createkineticfrontier/textures')
BLOCKS = ROOT / 'block'
ITEMS = ROOT / 'item'
BLOCKS.mkdir(parents=True, exist_ok=True)
ITEMS.mkdir(parents=True, exist_ok=True)

PALETTE = {
    'steel': (48, 53, 56, 255), 'steel2': (84, 91, 92, 255), 'dark': (24, 27, 28, 255),
    'brass': (184, 133, 57, 255), 'brass2': (223, 177, 82, 255), 'copper': (165, 81, 44, 255),
    'wood': (93, 56, 35, 255), 'glow': (235, 211, 94, 255), 'red': (154, 52, 38, 255),
    'white': (220, 216, 190, 255), 'black': (10, 11, 12, 255)
}

def block_texture(name, accent='brass', motif='panel'):
    img = Image.new('RGBA', (16, 16), PALETTE['steel'])
    d = ImageDraw.Draw(img)
    d.rectangle((0, 0, 15, 15), outline=PALETTE['dark'])
    d.rectangle((2, 2, 13, 13), outline=PALETTE['steel2'])
    a = PALETTE[accent]
    if motif == 'core':
        d.rectangle((5, 5, 10, 10), fill=PALETTE['dark'], outline=a)
        d.rectangle((7, 7, 8, 8), fill=PALETTE['glow'])
        d.line((3, 12, 12, 3), fill=PALETTE['steel2'])
    elif motif == 'launcher':
        d.rectangle((3, 6, 12, 9), fill=PALETTE['dark'], outline=a)
        d.polygon([(11, 5), (14, 7), (11, 10)], fill=a)
        d.rectangle((4, 11, 7, 12), fill=PALETTE['copper'])
    elif motif == 'rail':
        d.line((2, 5, 13, 5), fill=a, width=2)
        d.line((2, 11, 13, 11), fill=a, width=2)
        d.rectangle((6, 3, 9, 13), fill=PALETTE['dark'])
        d.rectangle((7, 6, 8, 10), fill=PALETTE['glow'])
    elif motif == 'winch':
        d.ellipse((3, 3, 12, 12), outline=a, width=2)
        d.ellipse((6, 6, 9, 9), fill=PALETTE['dark'], outline=PALETTE['steel2'])
        d.line((10, 10, 14, 14), fill=PALETTE['copper'], width=2)
    elif motif == 'brake':
        d.rectangle((4, 3, 11, 12), fill=PALETTE['dark'], outline=a)
        d.line((5, 5, 10, 10), fill=PALETTE['red'], width=2)
        d.line((10, 5, 5, 10), fill=PALETTE['red'], width=2)
    elif motif == 'flywheel':
        d.ellipse((2, 2, 13, 13), outline=a, width=2)
        d.ellipse((5, 5, 10, 10), fill=PALETTE['dark'], outline=PALETTE['steel2'])
        d.line((8, 3, 8, 12), fill=PALETTE['brass2'])
        d.line((3, 8, 12, 8), fill=PALETTE['brass2'])
    elif motif == 'cannon':
        d.rectangle((2, 6, 10, 10), fill=PALETTE['dark'], outline=a)
        d.rectangle((9, 5, 14, 11), fill=PALETTE['steel2'], outline=PALETTE['dark'])
        d.rectangle((4, 11, 12, 12), fill=PALETTE['copper'])
    elif motif == 'sensor':
        d.rectangle((4, 3, 11, 12), fill=PALETTE['dark'], outline=a)
        d.rectangle((7, 6, 8, 9), fill=PALETTE['glow'])
        d.line((2, 5, 4, 7), fill=PALETTE['copper'])
        d.line((11, 7, 14, 5), fill=PALETTE['copper'])
    else:
        d.rectangle((4, 4, 11, 11), fill=PALETTE['dark'], outline=a)
    img.save(BLOCKS / f'{name}.png')

def item_texture(name, motif):
    img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    if motif == 'hook':
        d.line((4, 3, 8, 7), fill=PALETTE['brass2'], width=2)
        d.line((8, 7, 12, 11), fill=PALETTE['steel2'], width=2)
        d.arc((8, 8, 14, 14), 0, 270, fill=PALETTE['copper'], width=2)
        d.rectangle((2, 2, 5, 5), fill=PALETTE['dark'], outline=PALETTE['brass'])
    elif motif == 'meter':
        d.rectangle((3, 3, 12, 13), fill=PALETTE['steel'], outline=PALETTE['brass'])
        d.rectangle((5, 5, 10, 8), fill=PALETTE['dark'], outline=PALETTE['glow'])
        d.line((8, 7, 10, 6), fill=PALETTE['red'], width=1)
    else:
        d.rectangle((4, 4, 11, 11), fill=PALETTE['steel2'], outline=PALETTE['brass'])
        d.rectangle((6, 6, 9, 9), fill=PALETTE['dark'])
        if motif == 'impact': d.rectangle((7, 3, 8, 12), fill=PALETTE['red'])
        if motif == 'signal': d.rectangle((7, 6, 8, 9), fill=PALETTE['glow'])
        if motif == 'utility': d.rectangle((5, 7, 10, 8), fill=PALETTE['copper'])
    img.save(ITEMS / f'{name}.png')

for args in [
    ('kinetic_core', 'brass', 'core'), ('momentum_launcher', 'brass', 'launcher'), ('kinetic_rail', 'copper', 'rail'),
    ('grapple_winch', 'brass', 'winch'), ('kinetic_brake', 'red', 'brake'), ('flywheel_array', 'brass', 'flywheel'),
    ('kinetic_cannon', 'brass', 'cannon'), ('kinetic_sensor', 'copper', 'sensor')
]: block_texture(*args)
for args in [('momentum_hook', 'hook'), ('momentum_meter', 'meter'), ('cargo_capsule', 'cargo'), ('impact_capsule', 'impact'), ('signal_capsule', 'signal'), ('utility_capsule', 'utility')]: item_texture(*args)
