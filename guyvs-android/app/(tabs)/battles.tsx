import { ScrollView, StyleSheet, Text, View } from "react-native";
import { useEffect, useMemo, useState } from "react";

import { ActionButton, AppScreen, palette, Section, Stat } from "@/components/guyvs/ui";
import { useProject } from "@/lib/guyvs/project-store";
import { stepBattle, type SimActor } from "@/lib/guyvs/simulation";

 type DisplayActor = SimActor & { accent: string; name: string };
const WIDTH = 332;
const HEIGHT = 360;
const RADIUS = 17;

export default function BattlesScreen() {
  const { state } = useProject();
  const [paused, setPaused] = useState(false);
  const [slow, setSlow] = useState(false);
  const [showDebug, setShowDebug] = useState(false);
  const [tick, setTick] = useState(0);
  const battleGuys = useMemo(() => {
    const source = state.project.guys.slice(0, 8);
    if (source.length !== 1) return source;
    return [source[0], { ...source[0], id: `${source[0].id}-sparring`, name: `${source[0].name} Sparring`, accent: palette.magenta }];
  }, [state.project.guys]);
  const initial = useMemo<DisplayActor[]>(() => battleGuys.map((guy, index) => ({ id: guy.id, name: guy.name, accent: guy.accent, x: 58 + (index % 4) * 72, y: 48 + Math.floor(index / 4) * 78, vx: index % 2 ? -170 - index * 14 : 170 + index * 14, vy: 0, radius: RADIUS, health: guy.health, impact: 0 })), [battleGuys]);
  const profiles = useMemo(() => Object.fromEntries(battleGuys.map((guy) => [guy.id, guy.dvd])), [battleGuys]);
  const [actors, setActors] = useState(initial);
  useEffect(() => { setActors(initial); setTick(0); setPaused(false); }, [initial]);
  useEffect(() => {
    if (paused) return;
    const interval = setInterval(() => {
      setActors((current) => stepBattle(current, profiles, { width: WIDTH, height: HEIGHT, floor: HEIGHT }, slow ? 0.004 : 0.016) as DisplayActor[]);
      setTick((value) => value + 1);
    }, slow ? 32 : 16);
    return () => clearInterval(interval);
  }, [paused, slow, profiles]);
  const restart = () => { setActors(initial); setTick(0); setPaused(false); };
  const impulse = () => setActors((current) => current.map((actor, index) => ({ ...actor, vx: actor.vx + (index % 2 ? -1 : 1) * 220, vy: -320, impact: 0.2 })));
  const alive = actors.filter((actor) => actor.health > 0);
  const impact = actors.find((actor) => actor.impact > 0);
  const status = alive.length <= 1 && actors.length > 1 ? `${alive[0]?.name ?? "Nobody"} wins this round` : impact ? "Impact! Physics collision landed" : paused ? "Round paused" : slow ? "Slow-motion round" : "Round live";

  return <AppScreen><ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
    <Text style={styles.title}>Battle Lab</Text>
    <Text style={styles.subtitle}>Put your Guys in the ring, watch the physics collide, and replay the best moments.</Text>
    <View style={styles.stats}><Stat label="Fighters" value={actors.length} /><Stat label="Live" value={alive.length} color={palette.lime} /><Stat label="Frame" value={tick} color={palette.magenta} /></View>

    <View style={styles.canvas}>
      <View style={styles.canvasHeader}><Text style={styles.roundLabel}>GUYVS ROUND 01</Text><Text style={styles.status}>{status}</Text></View>
      {actors.length ? actors.map((actor) => <View key={actor.id} style={[styles.actor, { left: actor.x - RADIUS, top: actor.y - RADIUS, opacity: actor.health > 0 ? 1 : 0.35 }]}>
        <View style={styles.healthTrack}><View style={[styles.healthFill, { width: `${Math.max(0, actor.health)}%`, backgroundColor: actor.health > 45 ? actor.accent : palette.warning }]} /></View>
        <View style={[styles.actorCore, { borderColor: actor.impact ? palette.warning : actor.accent, backgroundColor: actor.accent }]}><Text style={styles.actorInitial}>{actor.name.slice(0, 1).toUpperCase()}</Text></View>
        <Text numberOfLines={1} style={styles.actorName}>{actor.name.slice(0, 12)}</Text>
        {actor.health <= 0 ? <Text style={styles.ko}>KO</Text> : null}
        {showDebug ? <Text style={styles.velocity}>{Math.round(actor.vx)},{Math.round(actor.vy)}</Text> : null}
      </View>) : <Text style={styles.empty}>Create a Guy first, then return here to battle.</Text>}
      <View style={styles.floor} />
      {showDebug ? <Text style={styles.debug}>gravity · bounce · body collision · damage</Text> : null}
    </View>

    <Section title="Run the round" subtitle="Start with Play. Use Slow when you want to understand why a collision happened.">
      <View style={styles.controls}><ActionButton label={paused ? "Play" : "Pause"} tone="cyan" onPress={() => setPaused((value) => !value)} /><ActionButton label={slow ? "Normal speed" : "Slow motion"} tone="magenta" onPress={() => setSlow((value) => !value)} /></View>
      <View style={styles.controls}><ActionButton label="Launch impact" tone="lime" onPress={impulse} /><ActionButton label="Restart round" tone="ghost" onPress={restart} /></View>
      <View style={styles.controls}><ActionButton label="Step one frame" tone="ghost" onPress={() => { setPaused(true); setActors((current) => stepBattle(current, profiles, { width: WIDTH, height: HEIGHT, floor: HEIGHT }, 0.016) as DisplayActor[]); setTick((value) => value + 1); }} /><ActionButton label={showDebug ? "Hide details" : "Show details"} tone="ghost" onPress={() => setShowDebug((value) => !value)} /></View>
    </Section>

    <Section title="How this works" subtitle="This is an original GuyVs physics presentation built for quick, readable experiments.">
      <Text style={styles.note}>Each Guy has gravity, speed, bounce, and health. When bodies collide, the impact is visible, health drops, and the round can produce a winner. Add more Guys from the Guys workspace to turn this into a chaotic free-for-all.</Text>
    </Section>
  </ScrollView></AppScreen>;
}

const styles = StyleSheet.create({
  content: { padding: 18, gap: 16, paddingBottom: 36 },
  title: { color: palette.text, fontSize: 30, fontWeight: "900", letterSpacing: -0.8 },
  subtitle: { color: palette.muted, marginTop: -8, fontSize: 14, lineHeight: 20 },
  stats: { flexDirection: "row", gap: 10 },
  canvas: { height: HEIGHT, width: "100%", backgroundColor: palette.ink, borderRadius: 20, overflow: "hidden", borderWidth: 1, borderColor: palette.border, position: "relative" },
  canvasHeader: { position: "absolute", top: 12, left: 14, right: 14, zIndex: 2, gap: 3 },
  roundLabel: { color: palette.cyan, fontSize: 10, fontWeight: "900", letterSpacing: 1.2 },
  status: { color: palette.text, fontSize: 12, fontWeight: "800" },
  actor: { position: "absolute", width: 54, alignItems: "center", gap: 3 },
  healthTrack: { height: 5, width: 48, borderRadius: 4, backgroundColor: palette.panelRaised, overflow: "hidden" },
  healthFill: { height: 5, borderRadius: 4 },
  actorCore: { height: 34, width: 34, borderRadius: 17, borderWidth: 3, alignItems: "center", justifyContent: "center" },
  actorInitial: { color: palette.ink, fontSize: 16, fontWeight: "900" },
  actorName: { color: palette.text, fontSize: 9, fontWeight: "800", maxWidth: 58 },
  ko: { color: palette.warning, fontSize: 11, fontWeight: "900" },
  velocity: { color: palette.lime, fontSize: 7, fontWeight: "700" },
  floor: { position: "absolute", left: 0, right: 0, bottom: 18, height: 3, backgroundColor: palette.magenta, opacity: 0.7 },
  empty: { color: palette.muted, alignSelf: "center", marginTop: HEIGHT / 2 - 20 },
  debug: { position: "absolute", left: 10, bottom: 28, color: palette.lime, fontSize: 10, fontWeight: "700" },
  controls: { flexDirection: "row", gap: 10 },
  note: { color: palette.muted, fontSize: 13, lineHeight: 19 },
});
