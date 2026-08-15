from pathlib import Path
import json

ROOT = Path('/home/ubuntu/Redliner101/src/main/resources')
ASSET = ROOT / 'assets/createkineticfrontier'
DATA = ROOT / 'data/createkineticfrontier'
blocks = ['kinetic_core','momentum_launcher','kinetic_rail','grapple_winch','kinetic_brake','flywheel_array','kinetic_cannon','kinetic_sensor']
items = ['momentum_hook','momentum_meter','cargo_capsule','impact_capsule','signal_capsule','utility_capsule']

def write(path, obj):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(obj, indent=2) + '\n')

for name in blocks:
    write(ASSET / f'models/block/{name}.json', {'parent':'minecraft:block/cube_all','textures':{'all':f'createkineticfrontier:block/{name}'}})
    variants = {}
    for facing in ['north','south','east','west']:
        variants[f'facing={facing}'] = {'model':f'createkineticfrontier:block/{name}'}
    if name == 'kinetic_sensor':
        variants = {}
        for facing in ['north','south','east','west']:
            variants[f'facing={facing},powered=false'] = {'model':f'createkineticfrontier:block/{name}'}
            variants[f'facing={facing},powered=true'] = {'model':f'createkineticfrontier:block/{name}'}
    write(ASSET / f'blockstates/{name}.json', {'variants':variants})
    write(ASSET / f'models/item/{name}.json', {'parent':f'createkineticfrontier:block/{name}'})

for name in items:
    write(ASSET / f'models/item/{name}.json', {'parent':'minecraft:item/generated','textures':{'layer0':f'createkineticfrontier:item/{name}'}})

recipes = {
    'momentum_hook': {'type':'minecraft:crafting_shaped','pattern':['  I',' CS','C  '],'key':{'I':{'item':'minecraft:iron_ingot'},'C':{'item':'minecraft:copper_ingot'},'S':{'item':'minecraft:string'}},'result':{'id':'createkineticfrontier:momentum_hook','count':1}},
    'momentum_meter': {'type':'minecraft:crafting_shaped','pattern':[' R ','IEI',' C '],'key':{'R':{'item':'minecraft:redstone'},'I':{'item':'minecraft:iron_ingot'},'E':{'item':'create:electron_tube'},'C':{'item':'minecraft:copper_ingot'}},'result':{'id':'createkineticfrontier:momentum_meter','count':1}},
    'kinetic_sensor': {'type':'minecraft:crafting_shaped','pattern':['CRC','AEA','CRC'],'key':{'C':{'item':'minecraft:copper_ingot'},'R':{'item':'minecraft:redstone'},'A':{'item':'create:andesite_alloy'},'E':{'item':'create:electron_tube'}},'result':{'id':'createkineticfrontier:kinetic_sensor','count':1}},
    'kinetic_brake': {'type':'minecraft:crafting_shaped','pattern':['ICI','CBC','ICI'],'key':{'I':{'item':'minecraft:iron_ingot'},'C':{'item':'minecraft:copper_ingot'},'B':{'item':'create:brass_ingot'}},'result':{'id':'createkineticfrontier:kinetic_brake','count':1}},
    'kinetic_core': {'type':'minecraft:crafting_shaped','pattern':['BGB','PFP','BGB'],'key':{'B':{'item':'create:brass_casing'},'G':{'item':'create:cogwheel'},'P':{'item':'create:precision_mechanism'},'F':{'item':'minecraft:iron_block'}},'result':{'id':'createkineticfrontier:kinetic_core','count':1}},
    'momentum_launcher': {'type':'minecraft:crafting_shaped','pattern':['SBS','CPC','IRI'],'key':{'S':{'item':'create:shaft'},'B':{'item':'create:brass_casing'},'C':{'item':'minecraft:copper_ingot'},'P':{'item':'create:precision_mechanism'},'I':{'item':'minecraft:iron_block'},'R':{'item':'minecraft:redstone'}},'result':{'id':'createkineticfrontier:momentum_launcher','count':1}},
    'kinetic_rail': {'type':'minecraft:crafting_shaped','pattern':['III','CRC','III'],'key':{'I':{'item':'minecraft:iron_ingot'},'C':{'item':'create:cogwheel'},'R':{'item':'minecraft:redstone'}},'result':{'id':'createkineticfrontier:kinetic_rail','count':8}},
    'flywheel_array': {'type':'minecraft:crafting_shaped','pattern':['SBS','CFC','SBS'],'key':{'S':{'item':'create:shaft'},'B':{'item':'create:brass_casing'},'C':{'item':'create:cogwheel'},'F':{'item':'minecraft:iron_block'}},'result':{'id':'createkineticfrontier:flywheel_array','count':1}},
    'grapple_winch': {'type':'minecraft:crafting_shaped','pattern':['CEC','BSB','ICI'],'key':{'C':{'item':'minecraft:chain'},'E':{'item':'minecraft:ender_pearl'},'B':{'item':'create:brass_casing'},'S':{'item':'create:shaft'},'I':{'item':'minecraft:iron_block'}},'result':{'id':'createkineticfrontier:grapple_winch','count':1}},
    'cargo_capsule': {'type':'minecraft:crafting_shaped','pattern':[' I ','ICI',' I '],'key':{'I':{'item':'minecraft:iron_ingot'},'C':{'item':'minecraft:chest'}},'result':{'id':'createkineticfrontier:cargo_capsule','count':4}},
    'impact_capsule': {'type':'minecraft:crafting_shaped','pattern':[' I ','IRI',' I '],'key':{'I':{'item':'minecraft:iron_ingot'},'R':{'item':'minecraft:anvil'}},'result':{'id':'createkineticfrontier:impact_capsule','count':4}},
    'signal_capsule': {'type':'minecraft:crafting_shaped','pattern':[' R ','ICI',' R '],'key':{'R':{'item':'minecraft:redstone'},'I':{'item':'minecraft:iron_ingot'},'C':{'item':'minecraft:copper_ingot'}},'result':{'id':'createkineticfrontier:signal_capsule','count':4}},
    'utility_capsule': {'type':'minecraft:crafting_shaped','pattern':[' I ','ICI',' I '],'key':{'I':{'item':'minecraft:iron_ingot'},'C':{'item':'minecraft:slime_ball'}},'result':{'id':'createkineticfrontier:utility_capsule','count':4}},
    'kinetic_cannon': {'type':'minecraft:crafting_shaped','pattern':['SFS','BEB','IRI'],'key':{'S':{'item':'create:sturdy_sheet'},'F':{'item':'create:precision_mechanism'},'B':{'item':'create:brass_casing'},'E':{'item':'create:electron_tube'},'I':{'item':'minecraft:iron_block'},'R':{'item':'minecraft:redstone_block'}},'result':{'id':'createkineticfrontier:kinetic_cannon','count':1}}
}
for name, recipe in recipes.items(): write(DATA / f'recipes/{name}.json', recipe)

advancements = {
    'root': {'display':{'icon':{'item':'createkineticfrontier:kinetic_core'},'title':{'translate':'advancements.createkineticfrontier.root.title'},'description':{'translate':'advancements.createkineticfrontier.root.description'},'background':'minecraft:textures/block/brass_block.png','frame':'task','show_toast':True,'announce_to_chat':False,'hidden':False},'criteria':{'core':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':['createkineticfrontier:kinetic_core']}]}}},'requirements':[['core']]},
    'meter': {'parent':'createkineticfrontier:root','display':{'icon':{'item':'createkineticfrontier:momentum_meter'},'title':{'translate':'advancements.createkineticfrontier.meter.title'},'description':{'translate':'advancements.createkineticfrontier.meter.description'},'frame':'task','show_toast':True,'announce_to_chat':True},'criteria':{'meter':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':['createkineticfrontier:momentum_meter']}]}}}},
    'hook': {'parent':'createkineticfrontier:meter','display':{'icon':{'item':'createkineticfrontier:momentum_hook'},'title':{'translate':'advancements.createkineticfrontier.hook.title'},'description':{'translate':'advancements.createkineticfrontier.hook.description'},'frame':'task','show_toast':True,'announce_to_chat':True},'criteria':{'hook':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':['createkineticfrontier:momentum_hook']}]}}}},
    'rail': {'parent':'createkineticfrontier:hook','display':{'icon':{'item':'createkineticfrontier:kinetic_rail'},'title':{'translate':'advancements.createkineticfrontier.rail.title'},'description':{'translate':'advancements.createkineticfrontier.rail.description'},'frame':'task','show_toast':True,'announce_to_chat':True},'criteria':{'rail':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':['createkineticfrontier:kinetic_rail']}]}}}},
    'launch': {'parent':'createkineticfrontier:rail','display':{'icon':{'item':'createkineticfrontier:momentum_launcher'},'title':{'translate':'advancements.createkineticfrontier.launch.title'},'description':{'translate':'advancements.createkineticfrontier.launch.description'},'frame':'goal','show_toast':True,'announce_to_chat':True},'criteria':{'launcher':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':['createkineticfrontier:momentum_launcher']}]}}}},
    'cannon': {'parent':'createkineticfrontier:launch','display':{'icon':{'item':'createkineticfrontier:kinetic_cannon'},'title':{'translate':'advancements.createkineticfrontier.cannon.title'},'description':{'translate':'advancements.createkineticfrontier.cannon.description'},'frame':'challenge','show_toast':True,'announce_to_chat':True},'criteria':{'cannon':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':['createkineticfrontier:kinetic_cannon']}]}}}}
}
for name, advancement in advancements.items(): write(DATA / f'advancements/{name}.json', advancement)

write(DATA / 'tags/blocks/mineable/pickaxe.json', {'replace':False,'values':[f'createkineticfrontier:{name}' for name in blocks]})
