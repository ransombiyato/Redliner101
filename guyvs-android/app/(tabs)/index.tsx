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
          <Text style={styles.subtitle}>Make a fighter, draw a ring, then watch your creations collide.</Text>
          <View style={styles.projectPill}><View style={styles.liveDot} /><Text style={styles.projectText}>{project.name}</Text></View>
        </View>

        <View style={styles.stats}>
          <Stat label="Guys" value={project.guys.length} />
          <Stat label="Rings" value={project.arenas.length} color={palette.magenta} />
          <Stat label="Battles" value={project.battles.length} color={palette.lime} />
        </View>

        <Section title="New here? Start with these three steps" subtitle="You can change anything later. Your work saves automatically on this device.">
          <View style={styles.step}><View style={[styles.stepNumber, { backgroundColor: palette.cyan }]}><Text style={styles.stepNumberText}>1</Text></View><View style={styles.stepCopy}><Text style={styles.stepTitle}>Make a Guy</Text><Text style={styles.stepHint}>Choose a name, color, body, and physics.</Text></View><ActionButton label="Open" onPress={() => router.push("/guys" as never)} small /></View>
          <View style={styles.step}><View style={[styles.stepNumber, { backgroundColor: palette.magenta }]}><Text style={styles.stepNumberText}>2</Text></View><View style={styles.stepCopy}><Text style={styles.stepTitle}>Build a Ring</Text><Text style={styles.stepHint}>Pick a preset or press and drag a custom boundary.</Text></View><ActionButton label="Open" tone="magenta" onPress={() => router.push("/arenas" as never)} small /></View>
          <View style={styles.step}><View style={[styles.stepNumber, { backgroundColor: palette.lime }]}><Text style={styles.stepNumberText}>3</Text></View><View style={styles.stepCopy}><Text style={styles.stepTitle}>Run the Battle</Text><Text style={styles.stepHint}>Pause, slow down, kick the action, or step frame-by-frame.</Text></View><ActionButton label="Play" tone="lime" onPress={() => router.push("/battles" as never)} small /></View>
        </Section>

        <Section title="Quick actions" subtitle="Jump straight into the part you want to edit.">
          <View style={styles.actionGrid}>
            <ActionButton label="Create Guy" onPress={() => router.push("/guys" as never)} />
            <ActionButton label="Build Ring" tone="magenta" onPress={() => router.push("/arenas" as never)} />
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
  content: { padding: 18, gap: 16, paddingBottom: 36 },
  hero: { gap: 7, paddingTop: 8 },
  kicker: { color: palette.cyan, fontSize: 11, letterSpacing: 1.7, fontWeight: "900" },
  title: { color: palette.text, fontSize: 34, lineHeight: 39, fontWeight: "900", letterSpacing: -1.1 },
  subtitle: { color: palette.muted, fontSize: 15, lineHeight: 21, maxWidth: 330 },
  projectPill: { alignSelf: "flex-start", flexDirection: "row", alignItems: "center", gap: 7, marginTop: 5, backgroundColor: palette.panelRaised, borderRadius: 999, paddingHorizontal: 10, paddingVertical: 7 },
  liveDot: { height: 7, width: 7, borderRadius: 4, backgroundColor: palette.lime },
  projectText: { color: palette.text, fontSize: 12, fontWeight: "700" },
  stats: { flexDirection: "row", gap: 10 },
  actionGrid: { flexDirection: "row", gap: 10 },
  step: { flexDirection: "row", alignItems: "center", gap: 10, backgroundColor: palette.panelRaised, borderRadius: 14, padding: 10 },
  stepNumber: { width: 31, height: 31, borderRadius: 16, alignItems: "center", justifyContent: "center" },
  stepNumberText: { color: palette.ink, fontSize: 16, fontWeight: "900" },
  stepCopy: { flex: 1, gap: 2 },
  stepTitle: { color: palette.text, fontSize: 14, fontWeight: "900" },
  stepHint: { color: palette.muted, fontSize: 11, lineHeight: 15 },
  updated: { color: palette.muted, fontSize: 12 },
});
