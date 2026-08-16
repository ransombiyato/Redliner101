import { Pressable, StyleSheet, Text, TextInput, View, type TextInputProps, type ViewStyle } from "react-native";
import { ScreenContainer } from "@/components/screen-container";

export const palette = {
  ink: "#0B1020",
  panel: "#151D33",
  panelRaised: "#202B47",
  border: "#334155",
  text: "#F8FAFC",
  muted: "#94A3B8",
  cyan: "#22D3EE",
  magenta: "#F472B6",
  lime: "#A3E635",
  warning: "#FBBF24",
  danger: "#FB7185",
};

export function AppScreen({ children }: { children: React.ReactNode }) {
  return <ScreenContainer containerClassName="bg-[#0B1020]" className="flex-1">{children}</ScreenContainer>;
}

export function Section({ title, subtitle, children, style }: { title: string; subtitle?: string; children: React.ReactNode; style?: ViewStyle }) {
  return (
    <View style={[styles.section, style]}>
      <Text style={styles.sectionTitle}>{title}</Text>
      {subtitle ? <Text style={styles.sectionSubtitle}>{subtitle}</Text> : null}
      {children}
    </View>
  );
}

export function ActionButton({ label, onPress, tone = "cyan", small = false, disabled = false }: { label: string; onPress: () => void; tone?: "cyan" | "magenta" | "lime" | "ghost" | "danger"; small?: boolean; disabled?: boolean }) {
  const color = tone === "cyan" ? palette.cyan : tone === "magenta" ? palette.magenta : tone === "lime" ? palette.lime : tone === "danger" ? palette.danger : palette.panelRaised;
  const textColor = tone === "ghost" ? palette.text : palette.ink;
  return (
    <Pressable
      accessibilityRole="button"
      disabled={disabled}
      onPress={onPress}
      style={({ pressed }) => [styles.button, small && styles.smallButton, tone === "ghost" && styles.ghostButton, { backgroundColor: color, opacity: disabled ? 0.45 : pressed ? 0.78 : 1 }]}
    >
      <Text style={[styles.buttonText, small && styles.smallButtonText, { color: textColor }]}>{label}</Text>
    </Pressable>
  );
}

export function Stat({ label, value, color = palette.cyan }: { label: string; value: string | number; color?: string }) {
  return <View style={styles.stat}><Text style={[styles.statValue, { color }]}>{value}</Text><Text style={styles.statLabel}>{label}</Text></View>;
}

export function NumericField({ label, value, onChange, step = 1, min = -9999, max = 9999 }: { label: string; value: number; onChange: (value: number) => void; step?: number; min?: number; max?: number }) {
  const set = (candidate: number) => onChange(Math.min(max, Math.max(min, Number.isFinite(candidate) ? candidate : value)));
  return (
    <View style={styles.numericRow}>
      <Text style={styles.fieldLabel}>{label}</Text>
      <View style={styles.numericControls}>
        <ActionButton label="−" tone="ghost" small onPress={() => set(value - step)} />
        <TextInput value={String(value)} onChangeText={(raw) => set(Number(raw))} keyboardType="numeric" selectTextOnFocus style={styles.numberInput} />
        <ActionButton label="+" tone="ghost" small onPress={() => set(value + step)} />
      </View>
    </View>
  );
}

export function TextField({ style, ...props }: TextInputProps) {
  return <TextInput placeholderTextColor={palette.muted} style={[styles.textInput, style]} {...props} />;
}

const styles = StyleSheet.create({
  section: { backgroundColor: palette.panel, borderRadius: 18, padding: 16, borderWidth: 1, borderColor: palette.border, gap: 10 },
  sectionTitle: { color: palette.text, fontSize: 16, fontWeight: "800", letterSpacing: 0.2 },
  sectionSubtitle: { color: palette.muted, fontSize: 12, lineHeight: 17, marginTop: -5 },
  button: { minHeight: 44, borderRadius: 13, alignItems: "center", justifyContent: "center", paddingHorizontal: 14 },
  smallButton: { minHeight: 34, minWidth: 34, paddingHorizontal: 10, borderRadius: 10 },
  ghostButton: { borderWidth: 1, borderColor: palette.border },
  buttonText: { fontWeight: "900", fontSize: 14 },
  smallButtonText: { fontSize: 17, lineHeight: 18 },
  stat: { flex: 1, minWidth: 76, backgroundColor: palette.panelRaised, borderRadius: 14, padding: 12, gap: 3 },
  statValue: { fontSize: 21, lineHeight: 25, fontWeight: "900" },
  statLabel: { color: palette.muted, fontSize: 10, fontWeight: "700", textTransform: "uppercase", letterSpacing: 0.7 },
  numericRow: { gap: 8, paddingVertical: 5 },
  fieldLabel: { color: palette.text, fontSize: 13, fontWeight: "700" },
  numericControls: { flexDirection: "row", alignItems: "center", gap: 8 },
  numberInput: { flex: 1, height: 36, backgroundColor: palette.ink, borderRadius: 10, borderWidth: 1, borderColor: palette.border, color: palette.text, fontSize: 14, fontWeight: "800", textAlign: "center", paddingHorizontal: 8 },
  textInput: { minHeight: 44, backgroundColor: palette.ink, borderRadius: 12, borderWidth: 1, borderColor: palette.border, color: palette.text, paddingHorizontal: 12, fontSize: 14 },
});
