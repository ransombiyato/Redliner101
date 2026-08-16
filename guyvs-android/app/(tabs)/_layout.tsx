import { Tabs } from "expo-router";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { Platform } from "react-native";

import { palette } from "@/components/guyvs/ui";
import { IconSymbol } from "@/components/ui/icon-symbol";

export default function TabLayout() {
  const insets = useSafeAreaInsets();
  const bottomPadding = Platform.OS === "web" ? 8 : Math.max(8, insets.bottom);
  return (
    <Tabs
      screenOptions={{
        headerShown: false,
        tabBarStyle: {
          backgroundColor: palette.panel,
          borderTopColor: palette.border,
          height: 54 + bottomPadding,
          paddingBottom: bottomPadding,
          paddingTop: 6,
        },
        tabBarActiveTintColor: palette.cyan,
        tabBarInactiveTintColor: palette.muted,
        tabBarLabelStyle: { fontSize: 11, fontWeight: "700" },
      }}
    >
      <Tabs.Screen name="index" options={{ title: "Home", tabBarIcon: ({ color }) => <IconSymbol name="house.fill" color={color} size={20} /> }} />
      <Tabs.Screen name="create" options={{ title: "Create", tabBarIcon: ({ color }) => <IconSymbol name="hammer.fill" color={color} size={20} /> }} />
      <Tabs.Screen name="library" options={{ title: "Assets", tabBarIcon: ({ color }) => <IconSymbol name="square.grid.2x2.fill" color={color} size={20} /> }} />
    </Tabs>
  );
}
