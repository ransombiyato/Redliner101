import { ScrollView, StyleSheet, Text, View } from "react-native";
import { useEffect, useMemo, useState } from "react";

import { ActionButton, AppScreen, palette, Section, Stat } from "@/components/guyvs/ui";
import { useProject } from "@/lib/guyvs/project-store";
import { stepBattle, type SimActor } from "@/lib/guyvs/simulation";

type DisplayActor = SimActor & { accent: string; name: string };
type RoundState = "setup" | "countdown" | "live" | "result";

const WIDTH = 332;
const HEIGHT = 380;
const RADIUS = 18;

function FighterSilhouette({ actor, side }: { actor: DisplayActor; side: "left" | "right" }) {
  const lean = Math.max(-18, Math.min(18, actor.vx / 18));
  const hit = actor.impact > 0;
  return (
    <View style={[styles.actor, { left: actor.x - 31, top: actor.y - 48, opacity: actor.health > 0 ? 1 : 0.25, transform: [{ rotate: `${lean}deg` }, { scale: hit ? 1.12 : 1 }] }]}>
      <View style={styles.healthTrack}><View style={[styles.healthFill, { width: `${Math.max(0, Math.min(100, actor.health))}%`, backgroundColor: actor.health > 40 ? actor.accent : palette.warning }]} /></View>
      <View style={styles.figure}>
        <View style={[styles.arm, side === "left" ? styles.armLeft : styles.armRight, { backgroundColor: actor.accent }]} />
        <View style={[styles.arm, side === "left" ? styles.armRight : styles.armLeft, styles.backArm, { backgroundColor: actor.accent }]} />
        <View style={[styles.head, { borderColor: hit ? palette.warning : actor.accent }]}><Text style={[styles.initial, { color: actor.accent }]}>{actor.name.slice(0, 1).toUpperCase()}</Text></View>
        <View style={[styles.body, { backgroundColor: actor.accent }]} />
        <View style={[styles.leg, styles.legLeft, { backgroundColor: actor.accent }]} />
        <View style={[styles.leg, styles.legRight, { backgroundColor: actor.accent }]} />
      </View>
      <Text numberOfLines={1} style={styles.actorName}>{actor.name}</Text>
      {actor.health <= 0 ? <Text style={styles.ko}>OUT</Text> : null}
    </View>
  );
}

export default function BattlesScreen() {
  const { state } = useProject();
  const [roundState, setRoundState] = useState<RoundState>("setup");
  const [countdown, setCountdown] = useState(3);
  const [slow, setSlow] = useState(false);
  const [showDebug, setShowDebug] = useState(false);
  const [tick, setTick] = useState(0);
  const arena = state.project.arenas[0];
  const duelGuys = useMemo(() => {
    const source = state.project.guys.slice(0, 2);
    if (source.length !== 1) return source;
    return [source[0], { ...source[0], id: `${source[0].id}-sparring`, name: `${source[0].name} Rival`, accent: palette.magenta }];
  }, [state.project.guys]);
  const profiles = useMemo(() => Object.fromEntries(duelGuys.map((guy) => [guy.id, guy.dvd])), [duelGuys]);
  const initial = useMemo<DisplayActor[]>(() => duelGuys.map((guy, index) => ({
    id: guy.id,
    name: guy.name,
    accent: guy.accent,
    x: index === 0 ? 84 : WIDTH - 84,
    y: 210,
    vx: index === 0 ? 170 : -170,
    vy: 0,
    radius: RADIUS,
    health: guy.health,
    impact: 0,
  })), [duelGuys]);
  const [actors, setActors] = useState(initial);
  const alive = actors.filter((actor) => actor.health > 0);
  const impact = actors.find((actor) => actor.impact > 0);
  const winner = alive.length === 1 ? alive[0] : undefined;

  useEffect(() => {
    setActors(initial);
    setTick(0);
    setRoundState("setup");
  }, [initial]);

  useEffect(() => {
    if (roundState !== "countdown") return;
    const timer = setTimeout(() => {
      setCountdown((value) => {
        if (value <= 1) {
          setRoundState("live");
          return 0;
        }
        return value - 1;
      });
    }, 700);
    return () => clearTimeout(timer);
  }, [roundState, countdown]);

  useEffect(() => {
    if (roundState !== "live") return;
    const interval = setInterval(() => {
      setActors((current) => stepBattle(current, profiles, { width: WIDTH, height: HEIGHT - 34, floor: HEIGHT - 34 }, slow ? 0.004 : 0.016) as DisplayActor[]);
      setTick((value) => value + 1);
    }, slow ? 32 : 16);
    return () => clearInterval(interval);
  }, [profiles, roundState, slow]);

  useEffect(() => {
    if (roundState === "live" && actors.length > 1 && alive.length <= 1) setRoundState("result");
  }, [actors.length, alive.length, roundState]);

  const startRound = () => {
    setActors(initial);
    setTick(0);
    setCountdown(3);
    setRoundState("countdown");
  };
  const pauseRound = () => setRoundState((value) => value === "live" ? "setup" : "live");
  const launchImpact = () => setActors((current) => current.map((actor, index) => ({ ...actor, vx: index === 0 ? 360 : -360, vy: -250, impact: 0.22 })));
  const stepFrame = () => { setRoundState("setup"); setActors((current) => stepBattle(current, profiles, { width: WIDTH, height: HEIGHT - 34, floor: HEIGHT - 34 }, 0.016) as DisplayActor[]); setTick((value) => value + 1); };
  const stageStatus = roundState === "result" ? `${winner?.name ?? "No one"} takes the round` : roundState === "countdown" ? "Get ready" : roundState === "live" ? impact ? "IMPACT" : "Round live" : "Matchup ready";

  return <AppScreen><ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
    <Text style={styles.title}>GuyVs Round</Text>
    <Text style={styles.subtitle}>Pick a matchup, hit begin, and watch the physics tell the story.</Text>
    <View style={styles.stats}><Stat label="Duel" value={`${actors.length || 0} Guys`} /><Stat label="State" value={roundState.toUpperCase()} color={roundState === "live" ? palette.lime : palette.magenta} /><Stat label="Frame" value={tick} color={palette.cyan} /></View>

    <View style={[styles.stage, { backgroundColor: arena?.background || palette.ink }]}>
      <View style={[styles.stageFrame, { borderColor: arena?.shapes[0]?.color || palette.cyan }]} />
      <View style={styles.stageHeader}><Text style={styles.stageTitle}>{arena?.name ?? "Practice Ring"}</Text><Text style={[styles.stageStatus, impact && styles.impactStatus]}>{stageStatus}</Text></View>
      <View style={styles.centerMark}><Text style={styles.centerMarkText}>VS</Text></View>
      {actors.map((actor, index) => <FighterSilhouette key={actor.id} actor={actor} side={index === 0 ? "left" : "right"} />)}
      {impact ? <View pointerEvents="none" style={[styles.hitBurst, { left: Math.max(28, Math.min(WIDTH - 56, impact.x - 20)), top: Math.max(70, Math.min(HEIGHT - 100, impact.y - 20)) }]}><Text style={styles.hitBurstText}>HIT!</Text></View> : null}
      <View style={[styles.stageFloor, { backgroundColor: arena?.shapes[0]?.color || palette.magenta }]} />
      {showDebug ? <Text style={styles.debug}>gravity · body bounce · impact damage · frame {tick}</Text> : null}

      {roundState === "setup" ? <View style={styles.setupOverlay}>
        <Text style={styles.overlayKicker}>MATCHUP READY</Text>
        <View style={styles.vsRow}><Text numberOfLines={1} style={[styles.vsName, { color: actors[0]?.accent || palette.cyan }]}>{actors[0]?.name ?? "Create a Guy"}</Text><Text style={styles.vsText}>VS</Text><Text numberOfLines={1} style={[styles.vsName, { color: actors[1]?.accent || palette.magenta }]}>{actors[1]?.name ?? "Create a Rival"}</Text></View>
        <Text style={styles.overlayHint}>Stable camera. One ring. One clear fight.</Text>
        <ActionButton label="Begin round" tone="lime" onPress={startRound} />
      </View> : null}
      {roundState === "countdown" ? <View pointerEvents="none" style={styles.countdownOverlay}><Text style={styles.countdown}>{countdown}</Text><Text style={styles.countdownLabel}>READY</Text></View> : null}
      {roundState === "result" ? <View style={styles.setupOverlay}><Text style={styles.overlayKicker}>ROUND COMPLETE</Text><Text style={styles.resultName}>{winner?.name ?? "No winner"}</Text><Text style={styles.overlayHint}>wins by surviving the collision storm.</Text><ActionButton label="Replay round" tone="lime" onPress={startRound} /></View> : null}
    </View>

    <Section title="Round controls" subtitle="Begin starts a short countdown. Use the controls below only when you want to inspect the physics.">
      <View style={styles.controls}><ActionButton label={roundState === "live" ? "Pause" : "Resume"} tone="cyan" onPress={pauseRound} /><ActionButton label={slow ? "Normal speed" : "Slow motion"} tone="magenta" onPress={() => setSlow((value) => !value)} /></View>
      <View style={styles.controls}><ActionButton label="Force collision" tone="lime" onPress={launchImpact} /><ActionButton label="Replay" tone="ghost" onPress={startRound} /></View>
      <View style={styles.controls}><ActionButton label="Step one frame" tone="ghost" onPress={stepFrame} /><ActionButton label={showDebug ? "Hide details" : "Show details"} tone="ghost" onPress={() => setShowDebug((value) => !value)} /></View>
    </Section>

    <Section title="Make it yours" subtitle="The core loop is simple on purpose: build a Guy, build a ring, then run a match.">
      <Text style={styles.note}>Add a second Guy in the Guys workspace to replace the automatic sparring rival. Your saved gravity, speed, and bounce settings control how each original Guy behaves once the round begins.</Text>
    </Section>
  </ScrollView></AppScreen>;
}

const styles = StyleSheet.create({
  content: { padding: 18, gap: 16, paddingBottom: 36 },
  title: { color: palette.text, fontSize: 30, fontWeight: "900", letterSpacing: -0.8 },
  subtitle: { color: palette.muted, marginTop: -8, fontSize: 14, lineHeight: 20 },
  stats: { flexDirection: "row", gap: 10 },
  stage: { height: HEIGHT, width: "100%", borderRadius: 22, overflow: "hidden", position: "relative", borderWidth: 1, borderColor: palette.border },
  stageFrame: { position: "absolute", top: 44, left: 12, right: 12, bottom: 22, borderWidth: 3, borderRadius: 12, opacity: 0.95 },
  stageHeader: { position: "absolute", top: 12, left: 16, right: 16, zIndex: 6, flexDirection: "row", justifyContent: "space-between", alignItems: "center" },
  stageTitle: { color: palette.text, fontSize: 11, fontWeight: "900", letterSpacing: 0.8, textTransform: "uppercase" },
  stageStatus: { color: palette.text, fontSize: 11, fontWeight: "900" },
  impactStatus: { color: palette.warning, transform: [{ scale: 1.08 }] },
  centerMark: { position: "absolute", top: 166, alignSelf: "center", width: 52, height: 52, borderRadius: 26, backgroundColor: palette.ink, borderWidth: 2, borderColor: palette.border, alignItems: "center", justifyContent: "center", opacity: 0.85 },
  centerMarkText: { color: palette.muted, fontSize: 16, fontWeight: "900", letterSpacing: 1.4 },
  actor: { position: "absolute", width: 62, alignItems: "center", zIndex: 4 },
  healthTrack: { height: 5, width: 54, borderRadius: 5, backgroundColor: palette.panel, overflow: "hidden", marginBottom: 3 },
  healthFill: { height: 5, borderRadius: 5 },
  figure: { width: 44, height: 57, position: "relative", alignItems: "center" },
  head: { width: 20, height: 20, borderRadius: 10, backgroundColor: palette.ink, borderWidth: 3, alignItems: "center", justifyContent: "center", zIndex: 3 },
  initial: { fontSize: 10, fontWeight: "900" },
  body: { position: "absolute", top: 18, width: 18, height: 24, borderRadius: 6, zIndex: 2 },
  arm: { position: "absolute", top: 24, width: 25, height: 7, borderRadius: 5, zIndex: 1 },
  armLeft: { left: 0, transform: [{ rotate: "25deg" }] },
  armRight: { right: 0, transform: [{ rotate: "-25deg" }] },
  backArm: { opacity: 0.58 },
  leg: { position: "absolute", top: 39, width: 8, height: 21, borderRadius: 5, zIndex: 1 },
  legLeft: { left: 14, transform: [{ rotate: "13deg" }] },
  legRight: { right: 14, transform: [{ rotate: "-13deg" }] },
  actorName: { color: palette.text, fontSize: 9, fontWeight: "900", maxWidth: 62 },
  ko: { color: palette.warning, fontSize: 11, fontWeight: "900", marginTop: 1 },
  hitBurst: { position: "absolute", width: 54, height: 54, borderRadius: 27, alignItems: "center", justifyContent: "center", backgroundColor: palette.warning, borderWidth: 4, borderColor: palette.text, zIndex: 8, transform: [{ rotate: "-10deg" }] },
  hitBurstText: { color: palette.ink, fontSize: 13, fontWeight: "900" },
  stageFloor: { position: "absolute", left: 18, right: 18, bottom: 31, height: 4, borderRadius: 2, opacity: 0.9 },
  debug: { position: "absolute", bottom: 7, left: 14, color: palette.lime, fontSize: 9, fontWeight: "800" },
  setupOverlay: { position: "absolute", left: 28, right: 28, top: 102, bottom: 68, zIndex: 12, borderRadius: 18, backgroundColor: "rgba(11, 16, 32, 0.94)", borderWidth: 1, borderColor: palette.border, padding: 18, alignItems: "center", justifyContent: "center", gap: 12 },
  overlayKicker: { color: palette.cyan, fontSize: 10, fontWeight: "900", letterSpacing: 1.7 },
  vsRow: { flexDirection: "row", alignItems: "center", gap: 10, width: "100%", justifyContent: "center" },
  vsName: { flex: 1, textAlign: "center", fontSize: 14, fontWeight: "900" },
  vsText: { color: palette.text, fontSize: 18, fontWeight: "900" },
  overlayHint: { color: palette.muted, fontSize: 11, lineHeight: 16, textAlign: "center" },
  countdownOverlay: { position: "absolute", top: 0, left: 0, right: 0, bottom: 0, zIndex: 12, backgroundColor: "rgba(11, 16, 32, 0.4)", alignItems: "center", justifyContent: "center" },
  countdown: { color: palette.lime, fontSize: 96, lineHeight: 100, fontWeight: "900", textShadowColor: palette.ink, textShadowRadius: 8 },
  countdownLabel: { color: palette.text, fontSize: 14, letterSpacing: 3, fontWeight: "900" },
  resultName: { color: palette.lime, fontSize: 23, fontWeight: "900", textAlign: "center" },
  controls: { flexDirection: "row", gap: 10 },
  note: { color: palette.muted, fontSize: 13, lineHeight: 19 },
});
