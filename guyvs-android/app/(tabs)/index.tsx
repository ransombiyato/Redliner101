import { ScrollView, StyleSheet, Text, View } from "react-native";
import { router } from "expo-router";

import { AppScreen, ActionButton, palette, Section, Stat } from "@/components/guyvs/ui";
import { useProject } from "@/lib/guyvs/project-store";

export default function HomeScreen() {
  const { state, undo, redo } = useProject();
  const { project } = state;
  return (
    <AppScreen>
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        <View style={styles.hero}>
          <Text style={styles.kicker}>CREATION SANDBOX</Text>
          <Text style={styles.title}>GuyVs Maker</Text>
          <Text style={styles.subtitle}>Build the fighter. Bend the physics. Run the battle.</Text>
          <View style={styles.projectPill}><View style={styles.liveDot} /><Text style={styles.projectText}>{project.name}</Text></View>
        </View>
        <View style={styles.stats}>
          <Stat label="Guys" value={project.guys.length} />
          <Stat label="Arenas" value={project.arenas.length} color={palette.magenta} />
          <Stat label="Battles" value={project.battles.length} color={palette.lime} />
        </View>
        <Section title="Start building" subtitle="Every template is editable and stored locally on this device.">
          <View style={styles.actionGrid}>
            <ActionButton label="Create Guy" onPress={() => router.push("/create" as never)} />
            <ActionButton label="Build Arena" tone="magenta" onPress={() => router.push("/create" as never)} />
          </View>
          <ActionButton label="Open Battle Lab" tone="lime" onPress={() => router.push("/battles" as never)} />
        </Section>
        <Section title="Edit history" subtitle="Changes are autosaved locally and can be safely reversed.">
          <View style={styles.actionGrid}>
            <ActionButton label="Undo" tone="ghost" disabled={state.past.length === 0} onPress={undo} />
            <ActionButton label="Redo" tone="ghost" disabled={state.future.length === 0} onPress={redo} />
          </View>
        </Section>
        <Section title="Current project" subtitle={project.description}>
          <Text style={styles.updated}>Last changed {new Date(project.updatedAt).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}</Text>
        </Section>
      </ScrollView>
    </AppScreen>
  );
}

const styles = StyleSheet.create({
  content: { padding: 18, gap: 16, paddingBottom: 32 },
  hero: { gap: 7, paddingTop: 8 },
  kicker: { color: palette.cyan, fontSize: 11, letterSpacing: 1.7, fontWeight: "900" },
  title: { color: palette.text, fontSize: 34, lineHeight: 39, fontWeight: "900", letterSpacing: -1.1 },
  subtitle: { color: palette.muted, fontSize: 15, lineHeight: 21, maxWidth: 310 },
  projectPill: { alignSelf: "flex-start", flexDirection: "row", alignItems: "center", gap: 7, marginTop: 5, backgroundColor: palette.panelRaised, borderRadius: 999, paddingHorizontal: 10, paddingVertical: 7 },
  liveDot: { height: 7, width: 7, borderRadius: 4, backgroundColor: palette.lime },
  projectText: { color: palette.text, fontSize: 12, fontWeight: "700" },
  stats: { flexDirection: "row", gap: 10 },
  actionGrid: { flexDirection: "row", gap: 10 },
  updated: { color: palette.muted, fontSize: 12 },
});
