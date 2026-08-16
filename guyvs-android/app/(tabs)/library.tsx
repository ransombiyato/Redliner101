import { ScrollView, StyleSheet, Text, View } from "react-native";
import { router } from "expo-router";

import { ActionButton, AppScreen, palette, Section } from "@/components/guyvs/ui";
import { makeArena, makeGuy } from "@/lib/guyvs/defaults";
import { useProject } from "@/lib/guyvs/project-store";

export default function LibraryScreen() {
  const { state, addGuy, addArena, deleteGuy, deleteArena } = useProject();
  const { project } = state;
  return (
    <AppScreen>
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        <Text style={styles.title}>Assets</Text>
        <Text style={styles.subtitle}>Everything here is part of your local project and can be remixed safely.</Text>
        <Section title="Guys" subtitle={`${project.guys.length} saved character assets`}>
          {project.guys.map((guy) => <View key={guy.id} style={styles.asset}><View style={[styles.swatch, { backgroundColor: guy.accent }]} /><View style={styles.assetCopy}><Text style={styles.assetTitle}>{guy.name}</Text><Text style={styles.assetMeta}>{guy.bodyParts.length} parts · {guy.attacks.length} attacks · {guy.abilities.length} abilities</Text></View><ActionButton label="Clone" tone="ghost" small onPress={() => addGuy({ ...guy, id: `${guy.id}-copy-${Date.now()}`, name: `${guy.name} Copy`, updatedAt: Date.now() })} /><ActionButton label="×" tone="danger" small onPress={() => deleteGuy(guy.id)} /></View>)}
          <ActionButton label="Add editable Guy" onPress={() => addGuy(makeGuy())} />
        </Section>
        <Section title="Arenas" subtitle={`${project.arenas.length} saved battle spaces`}>
          {project.arenas.map((arena) => <View key={arena.id} style={styles.asset}><View style={[styles.swatch, { backgroundColor: arena.background }]} /><View style={styles.assetCopy}><Text style={styles.assetTitle}>{arena.name}</Text><Text style={styles.assetMeta}>{arena.shapes.length} shapes · {arena.zones.length} physics zones</Text></View><ActionButton label="Clone" tone="ghost" small onPress={() => addArena({ ...arena, id: `${arena.id}-copy-${Date.now()}`, name: `${arena.name} Copy`, updatedAt: Date.now() })} /><ActionButton label="×" tone="danger" small onPress={() => deleteArena(arena.id)} /></View>)}
          <ActionButton label="Add editable Arena" tone="magenta" onPress={() => addArena(makeArena())} />
        </Section>
        <Section title="Workshop" subtitle="The project is stored as portable JSON and validated on import."><ActionButton label="Open import & export" tone="lime" onPress={() => router.push("/workshop" as never)} /></Section>
      </ScrollView>
    </AppScreen>
  );
}

const styles = StyleSheet.create({
  content: { padding: 18, gap: 16, paddingBottom: 32 },
  title: { color: palette.text, fontSize: 30, fontWeight: "900", letterSpacing: -0.8 },
  subtitle: { color: palette.muted, marginTop: -8, fontSize: 14, lineHeight: 20 },
  asset: { flexDirection: "row", alignItems: "center", gap: 8, paddingVertical: 6 },
  swatch: { width: 30, height: 30, borderRadius: 9, borderWidth: 1, borderColor: palette.border },
  assetCopy: { flex: 1, gap: 2 },
  assetTitle: { color: palette.text, fontWeight: "800", fontSize: 14 },
  assetMeta: { color: palette.muted, fontSize: 11 },
});
