import AsyncStorage from "@react-native-async-storage/async-storage";
import { createContext, useCallback, useContext, useEffect, useMemo, useReducer } from "react";

import { makeStarterProject } from "@/lib/guyvs/defaults";
import type { Arena, Guy, GuyVsProject } from "@/lib/guyvs/types";
import { isGuyVsProject } from "@/lib/guyvs/validation";

const STORAGE_KEY = "guyvs-k41-project-v1";

type StoreState = { project: GuyVsProject; past: GuyVsProject[]; future: GuyVsProject[]; hydrated: boolean };
type ProjectPatch = Partial<Omit<GuyVsProject, "id" | "createdAt" | "guys" | "arenas" | "battles">>;

type StoreApi = {
  state: StoreState;
  patchProject: (patch: ProjectPatch) => void;
  replaceGuy: (guy: Guy) => void;
  addGuy: (guy: Guy) => void;
  deleteGuy: (id: string) => void;
  replaceArena: (arena: Arena) => void;
  addArena: (arena: Arena) => void;
  deleteArena: (id: string) => void;
  undo: () => void;
  redo: () => void;
  reset: () => void;
  exportProject: () => string;
  importProject: (json: string) => { ok: boolean; error?: string };
};

type Action =
  | { type: "hydrate"; project: GuyVsProject }
  | { type: "commit"; project: GuyVsProject }
  | { type: "undo" }
  | { type: "redo" }
  | { type: "reset" };

function withTimestamp(project: GuyVsProject): GuyVsProject {
  return { ...project, updatedAt: Date.now() };
}

function normalizeProject(project: GuyVsProject): GuyVsProject {
  return { ...project, guys: project.guys.map((guy) => ({ ...guy, animations: guy.animations ?? [], effects: guy.effects ?? [] })) };
}

function reducer(state: StoreState, action: Action): StoreState {
  if (action.type === "hydrate") return { ...state, project: action.project, hydrated: true };
  if (action.type === "commit") return { ...state, project: action.project, past: [...state.past.slice(-39), state.project], future: [] };
  if (action.type === "undo") {
    const previous = state.past.at(-1);
    return previous ? { ...state, project: previous, past: state.past.slice(0, -1), future: [state.project, ...state.future] } : state;
  }
  if (action.type === "redo") {
    const next = state.future[0];
    return next ? { ...state, project: next, past: [...state.past, state.project], future: state.future.slice(1) } : state;
  }
  return { project: makeStarterProject(), past: [], future: [], hydrated: true };
}

const ProjectContext = createContext<StoreApi | null>(null);

export function ProjectProvider({ children }: { children: React.ReactNode }) {
  const [state, dispatch] = useReducer(reducer, undefined, () => ({ project: makeStarterProject(), past: [], future: [], hydrated: false }));

  useEffect(() => {
    AsyncStorage.getItem(STORAGE_KEY).then((raw) => {
      if (!raw) return dispatch({ type: "hydrate", project: makeStarterProject() });
      try {
        const parsed = JSON.parse(raw) as GuyVsProject;
        if (!isGuyVsProject(parsed)) throw new Error("Invalid project");
        dispatch({ type: "hydrate", project: normalizeProject(parsed) });
      } catch {
        dispatch({ type: "hydrate", project: makeStarterProject() });
      }
    });
  }, []);

  useEffect(() => {
    if (state.hydrated) AsyncStorage.setItem(STORAGE_KEY, JSON.stringify(state.project));
  }, [state.hydrated, state.project]);

  const commit = useCallback((project: GuyVsProject) => dispatch({ type: "commit", project: withTimestamp(project) }), []);
  const api = useMemo<StoreApi>(() => ({
    state,
    patchProject: (patch) => commit({ ...state.project, ...patch }),
    replaceGuy: (guy) => commit({ ...state.project, guys: state.project.guys.map((item) => item.id === guy.id ? { ...guy, updatedAt: Date.now() } : item) }),
    addGuy: (guy) => commit({ ...state.project, guys: [...state.project.guys, guy] }),
    deleteGuy: (id) => commit({ ...state.project, guys: state.project.guys.filter((item) => item.id !== id) }),
    replaceArena: (arena) => commit({ ...state.project, arenas: state.project.arenas.map((item) => item.id === arena.id ? { ...arena, updatedAt: Date.now() } : item) }),
    addArena: (arena) => commit({ ...state.project, arenas: [...state.project.arenas, arena] }),
    deleteArena: (id) => commit({ ...state.project, arenas: state.project.arenas.filter((item) => item.id !== id) }),
    undo: () => dispatch({ type: "undo" }),
    redo: () => dispatch({ type: "redo" }),
    reset: () => dispatch({ type: "reset" }),
    exportProject: () => JSON.stringify(state.project, null, 2),
    importProject: (json) => {
      try {
        const parsed = JSON.parse(json) as GuyVsProject;
        if (!isGuyVsProject(parsed)) throw new Error("That file is not a GuyVs project.");
        commit(normalizeProject(parsed));
        return { ok: true };
      } catch (error) {
        return { ok: false, error: error instanceof Error ? error.message : "Unable to import this project." };
      }
    },
  }), [commit, state]);

  return <ProjectContext.Provider value={api}>{children}</ProjectContext.Provider>;
}

export function useProject() {
  const context = useContext(ProjectContext);
  if (!context) throw new Error("useProject must be used within ProjectProvider");
  return context;
}
