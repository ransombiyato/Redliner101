from pathlib import Path
import re

path = Path("Common/src/main/java/at/petrak/hexcasting/datagen/recipe/HexplatRecipes.java")
text = path.read_text()
pattern = re.compile(r"\.save\(recipes, (modLoc\([^;\n]+?\)|Identifier\.fromNamespaceAndPath\([^;\n]+?\))\);")
updated, count = pattern.subn(r".save(recipes, recipeKey(\1));", text)
if count == 0:
    raise SystemExit("No recipe save calls were changed")
path.write_text(updated)
print(f"updated_recipe_save_calls={count}")
