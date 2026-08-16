import { Alert, ScrollView, StyleSheet, Text, View } from "react-native";
import { useState } from "react";

import { ActionButton, AppScreen, palette, Section, TextField } from "@/components/guyvs/ui";
import { chooseGuyVsProject, exportGuyVsProject } from "@/lib/guyvs/portable-file";
import { useProject } from "@/lib/guyvs/project-store";

export default function WorkshopScreen() {
  const { state, patchProject, exportProject, importProject, reset } = useProject();
  const [busy, setBusy] = useState(false);
  const exportFile = async () => { setBusy(true); try { const uri = await exportGuyVsProject(state.project.name, exportProject()); Alert.alert("Project exported", `A portable GuyVs JSON file was prepared at:\n${uri}`); } catch (error) { Alert.alert("Export unavailable", error instanceof Error ? error.message : "Unable to export this project."); } finally { setBusy(false); } };
  const importFile = async () => { setBusy(true); try { const json = await chooseGuyVsProject(); if (!json) return; const result = importProject(json); if (result.ok) Alert.alert("Project imported", "The project data passed validation and replaced the current local project."); else Alert.alert("Import rejected", result.error); } catch (error) { Alert.alert("Import failed", error instanceof Error ? error.message : "Unable to read that project file."); } finally { setBusy(false); } };
  return <AppScreen><ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
    <Text style={styles.title}>Workshop</Text><Text style={styles.subtitle}>Save, recover, remix, and exchange portable local project files.</Text>
    <Section title="Project details" subtitle="These details are included in your saved JSON."><TextField value={state.project.name} onChangeText={(name) => patchProject({ name })} placeholder="Project name" /><TextField value={state.project.description} onChangeText={(description) => patchProject({ description })} placeholder="Project description" multiline style={styles.description} /></Section>
    <Section title="Portable project file" subtitle="Exports are JSON, validated when imported, and never require an account."><View style={styles.row}><ActionButton label={busy ? "Working…" : "Export JSON"} tone="lime" disabled={busy} onPress={exportFile} /><ActionButton label="Import JSON" tone="cyan" disabled={busy} onPress={importFile} /></View><Text style={styles.note}>Android export opens the system share sheet. Import opens the document picker and safely rejects malformed files.</Text></Section>
    <Section title="Recovery" subtitle="Edits are autosaved locally. Reset replaces the current project with an editable starter template."><ActionButton label="Reset project" tone="danger" onPress={() => Alert.alert("Reset project?", "This replaces the current project in local storage. Export first if you want a backup.", [{ text: "Cancel", style: "cancel" }, { text: "Reset", style: "destructive", onPress: reset }])} /></Section>
  </ScrollView></AppScreen>;
}
const styles = StyleSheet.create({ content: { padding: 18, gap: 16, paddingBottom: 32 }, title: { color: palette.text, fontSize: 30, fontWeight: "900", letterSpacing: -0.8 }, subtitle: { color: palette.muted, marginTop: -8, fontSize: 14, lineHeight: 20 }, row: { flexDirection: "row", gap: 10 }, note: { color: palette.muted, fontSize: 12, lineHeight: 18 }, description: { minHeight: 82, textAlignVertical: "top", paddingTop: 12 } });
