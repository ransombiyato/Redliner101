import { ScrollView, StyleSheet, Text, View } from "react-native";
import { router } from "expo-router";

import { ActionButton, AppScreen, palette, Section } from "@/components/guyvs/ui";
import { makeArena, makeGuy } from "@/lib/guyvs/defaults";
import { useProject } from "@/lib/guyvs/project-store";

const tools = [
  { title: "Guy Creator", detail: "Body parts, appearance, DVD physics, hitboxes", tone: "cyan" as const, route: "/guys" },
  { title: "Arena Creator", detail: "Rings, geometry, physics zones, objects", tone: "magenta" as const, route: "/arenas" },
  { title: "Combat Studio", detail: "Attack timelines, modular abilities, AI, animation, effects", tone: "magenta" as const, route: "/combat" },
  { title: "Battle Lab", detail: "Spawn, simulate, pause, inspect, live edit", tone: "lime" as const, route: "/battles" },
];

export default function CreateScreen() {
  const { addGuy, addArena } = useProject();
  return (
    <AppScreen>
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        <Text style={styles.title}>Create</Text>
        <Text style={styles.subtitle}>Start with a reusable system, then tune every value in the inspectors.</Text>
        <Section title="Fast start" subtitle="These add working editable assets to the active project.">
          <View style={styles.row}>
            <ActionButton label="+ Guy" onPress={() => { addGuy(makeGuy(`Guy ${Date.now().toString().slice(-3)}`)); router.push("/guys" as never); }} />
            <ActionButton label="+ Arena" tone="magenta" onPress={() => { addArena(makeArena(`Arena ${Date.now().toString().slice(-3)}`)); router.push("/arenas" as never); }} />
          </View>
        </Section>
        {tools.map((tool) => (
          <Section key={tool.title} title={tool.title} subtitle={tool.detail}>
            <ActionButton label={`Open ${tool.title}`} tone={tool.tone} onPress={() => router.push(tool.route as never)} />
          </Section>
        ))}
        <Section title="Portable projects" subtitle="Every asset stays data-backed, so it can be duplicated, exported, and safely imported into another project.">
          <ActionButton label="Open Workshop" tone="ghost" onPress={() => router.push("/workshop" as never)} />
        </Section>
      </ScrollView>
    </AppScreen>
  );
}

const styles = StyleSheet.create({
  content: { padding: 18, gap: 16, paddingBottom: 32 },
  title: { color: palette.text, fontSize: 30, fontWeight: "900", letterSpacing: -0.8 },
  subtitle: { color: palette.muted, marginTop: -8, fontSize: 14, lineHeight: 20 },
  row: { flexDirection: "row", gap: 10 },
  note: { color: palette.muted, lineHeight: 19, fontSize: 13 },
});
