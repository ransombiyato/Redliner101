import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { Tabs } from "expo-router";
import { useEffect, useState } from "react";
import { Platform, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import type { BottomTabBarProps } from "@react-navigation/bottom-tabs";
import { GestureHandlerRootView } from "react-native-gesture-handler";
import { useSafeAreaInsets } from "react-native-safe-area-context";

import { palette } from "@/components/guyvs/ui";
import { IconSymbol } from "@/components/ui/icon-symbol";
import { ThemeProvider } from "@/lib/theme-provider";
import { ProjectProvider } from "@/lib/guyvs/project-store";
import { createTRPCClient, trpc } from "@/lib/trpc";

const workspace = {
  index: { label: "Home", hint: "Start here", icon: "house.fill" as const },
  create: { label: "Create", hint: "Make things", icon: "hammer.fill" as const },
  guys: { label: "Guys", hint: "Fighters", icon: "person.fill" as const },
  arenas: { label: "Rings", hint: "Battlefields", icon: "circle.fill" as const },
  combat: { label: "Combat", hint: "Moves", icon: "bolt.fill" as const },
  battles: { label: "Battles", hint: "Simulate", icon: "flame.fill" as const },
  library: { label: "Assets", hint: "Your library", icon: "square.grid.2x2.fill" as const },
  workshop: { label: "Workshop", hint: "Import/export", icon: "wrench.fill" as const },
} as const;

type WorkspaceName = keyof typeof workspace;

function WorkspaceBar({ state, descriptors, navigation, insets }: BottomTabBarProps & { insets: { bottom: number } }) {
  return (
    <View style={[styles.bar, { paddingBottom: Math.max(insets.bottom, 8) }]}>
      <Text style={styles.barHint}>WORKSPACE</Text>
      <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.scrollContent}>
        {state.routes.map((route) => {
          const name = route.name as WorkspaceName;
          const item = workspace[name];
          if (!item) return null;
          const focused = state.index === state.routes.indexOf(route);
          const onPress = () => {
            const event = navigation.emit({ type: "tabPress", target: route.key, canPreventDefault: true });
            if (!focused && !event.defaultPrevented) navigation.navigate(route.name, route.params);
          };
          return (
            <Pressable
              key={route.key}
              accessibilityRole="tab"
              accessibilityLabel={`${item.label}, ${item.hint}`}
              accessibilityState={focused ? { selected: true } : {}}
              onPress={onPress}
              style={({ pressed }) => [styles.tab, focused && styles.tabFocused, pressed && styles.tabPressed]}
            >
              <IconSymbol name={item.icon} color={focused ? palette.ink : palette.text} size={19} />
              <View>
                <Text style={[styles.tabLabel, focused && styles.tabLabelFocused]}>{item.label}</Text>
                <Text style={[styles.tabHint, focused && styles.tabHintFocused]}>{item.hint}</Text>
              </View>
            </Pressable>
          );
        })}
      </ScrollView>
    </View>
  );
}

function EmptyStartupSurface() {
  return <View accessibilityLabel="Preparing GuyVs" style={{ flex: 1, backgroundColor: "#0B1020" }} />;
}

export default function TabLayout() {
  const insets = useSafeAreaInsets();
  const [isReady, setIsReady] = useState(false);
  const [queryClient] = useState(() => new QueryClient({ defaultOptions: { queries: { refetchOnWindowFocus: false, retry: 1 } } }));
  const [trpcClient] = useState(() => createTRPCClient());

  useEffect(() => {
    const timer = setTimeout(() => setIsReady(true), 32);
    return () => clearTimeout(timer);
  }, []);

  if (!isReady) return <EmptyStartupSurface />;

  return (
    <ThemeProvider>
      <GestureHandlerRootView style={{ flex: 1 }}>
        <trpc.Provider client={trpcClient} queryClient={queryClient}>
          <QueryClientProvider client={queryClient}>
            <ProjectProvider>
              <Tabs
                tabBar={(props) => <WorkspaceBar {...props} insets={insets} />}
                screenOptions={{ headerShown: false, sceneStyle: { backgroundColor: palette.ink } }}
              >
                <Tabs.Screen name="index" options={{ title: "Home" }} />
                <Tabs.Screen name="create" options={{ title: "Create" }} />
                <Tabs.Screen name="guys" options={{ title: "Guys" }} />
                <Tabs.Screen name="arenas" options={{ title: "Rings" }} />
                <Tabs.Screen name="combat" options={{ title: "Combat" }} />
                <Tabs.Screen name="battles" options={{ title: "Battles" }} />
                <Tabs.Screen name="library" options={{ title: "Assets" }} />
                <Tabs.Screen name="workshop" options={{ title: "Workshop" }} />
              </Tabs>
            </ProjectProvider>
          </QueryClientProvider>
        </trpc.Provider>
      </GestureHandlerRootView>
    </ThemeProvider>
  );
}

const styles = StyleSheet.create({
  bar: { backgroundColor: palette.panel, borderTopWidth: 1, borderTopColor: palette.border, paddingTop: 7 },
  barHint: { color: palette.muted, fontSize: 9, fontWeight: "900", letterSpacing: 1.5, paddingHorizontal: 14, marginBottom: 4 },
  scrollContent: { gap: 8, paddingHorizontal: 12 },
  tab: { minWidth: 83, minHeight: 58, borderRadius: 13, paddingHorizontal: 10, paddingVertical: 8, flexDirection: "row", alignItems: "center", gap: 7, backgroundColor: palette.panelRaised },
  tabFocused: { backgroundColor: palette.cyan },
  tabPressed: { opacity: 0.7, transform: [{ scale: 0.98 }] },
  tabLabel: { color: palette.text, fontSize: 12, fontWeight: "900" },
  tabLabelFocused: { color: palette.ink },
  tabHint: { color: palette.muted, fontSize: 9, marginTop: 2 },
  tabHintFocused: { color: "#164E63" },
});
