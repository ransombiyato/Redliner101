import { ScrollView, StyleSheet, Text, View } from "react-native";
import { useEffect, useState } from "react";

import { ActionButton, AppScreen, NumericField, palette, Section, TextField } from "@/components/guyvs/ui";
import { makeGuy, uid } from "@/lib/guyvs/defaults";
import { useProject } from "@/lib/guyvs/project-store";
import type { BodyPart } from "@/lib/guyvs/types";

export default function GuysScreen() {
  const { state, addGuy, replaceGuy } = useProject();
  const [selectedId, setSelectedId] = useState(state.project.guys[0]?.id ?? "");
  useEffect(() => { if (!state.project.guys.some((guy) => guy.id === selectedId)) setSelectedId(state.project.guys[0]?.id ?? ""); }, [selectedId, state.project.guys]);
  const guy = state.project.guys.find((item) => item.id === selectedId);
  const update = (patch: Partial<typeof guy>) => guy && replaceGuy({ ...guy, ...patch });
  const addPart = () => guy && update({ bodyParts: [...guy.bodyParts, { id: uid("part"), name: `Part ${guy.bodyParts.length + 1}`, shape: "rectangle", position: { x: 38, y: 20 }, size: { x: 28, y: 48 }, rotation: 0, mass: 1, density: 1, friction: 0.35, bounce: 0.72, color: guy.accent }] });
  const patchPart = (part: BodyPart, patch: Partial<BodyPart>) => guy && update({ bodyParts: guy.bodyParts.map((item) => item.id === part.id ? { ...item, ...patch } : item) });
  return (
    <AppScreen>
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        <Text style={styles.title}>Guy Creator</Text>
        <Text style={styles.subtitle}>Editable bodies, movement, hitboxes, combat assets, and AI belong to every Guy.</Text>
        <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.chips}>
          {state.project.guys.map((item) => <ActionButton key={item.id} label={item.name} tone={item.id === selectedId ? "cyan" : "ghost"} small onPress={() => setSelectedId(item.id)} />)}
          <ActionButton label="+" tone="magenta" small onPress={() => { const created = makeGuy(); addGuy(created); setSelectedId(created.id); }} />
        </ScrollView>
        {guy ? <>
          <Section title="Identity & appearance" subtitle="The accent is reused by newly-added parts and editor previews.">
            <TextField value={guy.name} onChangeText={(name) => update({ name })} placeholder="Guy name" />
            <TextField value={guy.accent} onChangeText={(accent) => update({ accent })} placeholder="#22D3EE" autoCapitalize="characters" />
            <NumericField label="Health" value={guy.health} min={1} max={9999} onChange={(health) => update({ health })} />
          </Section>
          <Section title="DVD Physics" subtitle="Momentum-first tuning: accelerate, glide, bounce, and change direction without stiff platformer movement.">
            <NumericField label="Acceleration" value={guy.dvd.acceleration} step={20} min={10} max={5000} onChange={(acceleration) => update({ dvd: { ...guy.dvd, acceleration } })} />
            <NumericField label="Maximum speed" value={guy.dvd.maxSpeed} step={10} min={10} max={3000} onChange={(maxSpeed) => update({ dvd: { ...guy.dvd, maxSpeed } })} />
            <NumericField label="Gravity" value={guy.dvd.gravity} step={20} min={0} max={5000} onChange={(gravity) => update({ dvd: { ...guy.dvd, gravity } })} />
            <NumericField label="Wall bounce" value={Math.round(guy.dvd.wallBounce * 100)} step={1} min={0} max={150} onChange={(wallBounce) => update({ dvd: { ...guy.dvd, wallBounce: wallBounce / 100 } })} />
          </Section>
          <Section title="Body editor" subtitle="Each part is independently shaped, sized, massed, and positioned.">
            {guy.bodyParts.map((part) => <View key={part.id} style={styles.part}><View style={[styles.partDot, { backgroundColor: part.color }]} /><View style={styles.partInfo}><Text style={styles.partName}>{part.name}</Text><Text style={styles.partMeta}>{part.shape} · {part.size.x}×{part.size.y} · {part.mass} mass</Text></View><ActionButton label="−" tone="danger" small onPress={() => update({ bodyParts: guy.bodyParts.filter((item) => item.id !== part.id) })} /></View>)}
            <ActionButton label="Add body part" tone="magenta" onPress={addPart} />
            {guy.bodyParts[0] ? <View style={styles.inlineEditor}><Text style={styles.editorHint}>Selected first part quick tuning</Text><TextField value={guy.bodyParts[0].color} onChangeText={(color) => patchPart(guy.bodyParts[0], { color })} placeholder="#FFFFFF" autoCapitalize="characters" /><NumericField label="Mass" value={guy.bodyParts[0].mass} step={0.25} min={0.1} max={100} onChange={(mass) => patchPart(guy.bodyParts[0], { mass })} /><NumericField label="Width" value={guy.bodyParts[0].size.x} step={2} min={4} max={500} onChange={(width) => patchPart(guy.bodyParts[0], { size: { ...guy.bodyParts[0].size, x: width } })} /><NumericField label="Bounce" value={Math.round(guy.bodyParts[0].bounce * 100)} min={0} max={150} onChange={(bounce) => patchPart(guy.bodyParts[0], { bounce: bounce / 100 })} /></View> : null}
          </Section>
          <Section title="Hitbox editor" subtitle="Hitbox dimensions and combat multipliers are stored independently from body collision shapes.">
            {guy.hitboxes.map((hitbox) => <View key={hitbox.id} style={styles.part}><View style={[styles.partDot, { backgroundColor: hitbox.color }]} /><View style={styles.partInfo}><Text style={styles.partName}>{hitbox.name}</Text><Text style={styles.partMeta}>{hitbox.size.x}×{hitbox.size.y} · {hitbox.damageMultiplier}× damage · {hitbox.knockbackMultiplier}× launch</Text></View><ActionButton label="−" tone="danger" small onPress={() => update({ hitboxes: guy.hitboxes.filter((item) => item.id !== hitbox.id) })} /></View>)}
            <ActionButton label="Add hitbox" tone="magenta" onPress={() => update({ hitboxes: [...guy.hitboxes, { id: uid("hit"), name: `Hitbox ${guy.hitboxes.length + 1}`, offset: { x: 20, y: 0 }, size: { x: 30, y: 26 }, damageMultiplier: 1, knockbackMultiplier: 1, hitstun: 160, color: "#F472B6" }] })} />
            {guy.hitboxes[0] ? <View style={styles.inlineEditor}><Text style={styles.editorHint}>Selected first hitbox</Text><NumericField label="Damage multiplier" value={guy.hitboxes[0].damageMultiplier} step={0.1} min={0} max={10} onChange={(damageMultiplier) => update({ hitboxes: guy.hitboxes.map((item, index) => index === 0 ? { ...item, damageMultiplier } : item) })} /><NumericField label="Knockback multiplier" value={guy.hitboxes[0].knockbackMultiplier} step={0.1} min={0} max={10} onChange={(knockbackMultiplier) => update({ hitboxes: guy.hitboxes.map((item, index) => index === 0 ? { ...item, knockbackMultiplier } : item) })} /></View> : null}
          </Section>
          <Section title="Combat components" subtitle={`${guy.attacks.length} attack, ${guy.abilities.length} ability, ${guy.ai.length} AI rules. Detailed editors share these data-backed assets in the next build pass.`}>
            <Text style={styles.combatLine}>{guy.attacks[0]?.name ?? "No attack"} · {guy.attacks[0]?.damage ?? 0} damage · {guy.abilities[0]?.name ?? "No ability"}</Text>
          </Section>
        </> : <Section title="No Guys yet" subtitle="Add a Guy to begin building." ><ActionButton label="Create first Guy" onPress={() => addGuy(makeGuy())} /></Section>}
      </ScrollView>
    </AppScreen>
  );
}

const styles = StyleSheet.create({
  content: { padding: 18, gap: 16, paddingBottom: 32 },
  title: { color: palette.text, fontSize: 30, fontWeight: "900", letterSpacing: -0.8 },
  subtitle: { color: palette.muted, marginTop: -8, fontSize: 14, lineHeight: 20 },
  chips: { gap: 8, paddingVertical: 2 },
  part: { flexDirection: "row", alignItems: "center", gap: 10, backgroundColor: palette.panelRaised, borderRadius: 12, padding: 10 },
  partDot: { width: 24, height: 24, borderRadius: 8 },
  partInfo: { flex: 1, gap: 2 },
  partName: { color: palette.text, fontSize: 13, fontWeight: "800" },
  partMeta: { color: palette.muted, fontSize: 11 },
  inlineEditor: { gap: 5, paddingTop: 4 },
  editorHint: { color: palette.cyan, fontSize: 11, fontWeight: "800", textTransform: "uppercase", letterSpacing: 0.7 },
  combatLine: { color: palette.muted, fontSize: 13, lineHeight: 19 },
});
