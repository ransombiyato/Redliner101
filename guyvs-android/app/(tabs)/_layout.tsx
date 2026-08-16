import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { Tabs } from "expo-router";
import { useEffect, useState } from "react";
import { Platform, View } from "react-native";
import { GestureHandlerRootView } from "react-native-gesture-handler";
import { useSafeAreaInsets } from "react-native-safe-area-context";

import { palette } from "@/components/guyvs/ui";
import { IconSymbol } from "@/components/ui/icon-symbol";
import { ThemeProvider } from "@/lib/theme-provider";
import { ProjectProvider } from "@/lib/guyvs/project-store";
import { createTRPCClient, trpc } from "@/lib/trpc";

function EmptyStartupSurface() {
  return <View accessibilityLabel="Preparing GuyVs" style={{ flex: 1, backgroundColor: "#0B1020" }} />;
}

export default function TabLayout() {
  const insets = useSafeAreaInsets();
  const [isReady, setIsReady] = useState(false);
  const [queryClient] = useState(() => new QueryClient({ defaultOptions: { queries: { refetchOnWindowFocus: false, retry: 1 } } }));
  const [trpcClient] = useState(() => createTRPCClient());
  const bottomPadding = Platform.OS === "web" ? 8 : Math.max(8, insets.bottom);

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
            </ProjectProvider>
          </QueryClientProvider>
        </trpc.Provider>
      </GestureHandlerRootView>
    </ThemeProvider>
  );
}
