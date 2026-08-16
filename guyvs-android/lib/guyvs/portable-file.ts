import * as DocumentPicker from "expo-document-picker";
import * as FileSystem from "expo-file-system/legacy";
import * as Sharing from "expo-sharing";
import { Platform } from "react-native";

function filenameForProject(name: string) {
  return `${name.trim().toLowerCase().replace(/[^a-z0-9]+/g, "-").replace(/^-|-$/g, "") || "guyvs-project"}.guyvs.json`;
}

export async function exportGuyVsProject(name: string, json: string) {
  if (Platform.OS === "web") throw new Error("Native file export is available in the Android build. Use the app build to share a project file.");
  const uri = `${FileSystem.documentDirectory}${filenameForProject(name)}`;
  await FileSystem.writeAsStringAsync(uri, json, { encoding: FileSystem.EncodingType.UTF8 });
  if (!(await Sharing.isAvailableAsync())) throw new Error("The system share sheet is unavailable on this device.");
  await Sharing.shareAsync(uri, { mimeType: "application/json", dialogTitle: "Export GuyVs project" });
  return uri;
}

export async function chooseGuyVsProject() {
  const result = await DocumentPicker.getDocumentAsync({ type: ["application/json", "text/json", "text/plain"], copyToCacheDirectory: true });
  if (result.canceled) return null;
  const asset = result.assets[0];
  if (!asset) return null;
  return FileSystem.readAsStringAsync(asset.uri, { encoding: FileSystem.EncodingType.UTF8 });
}
