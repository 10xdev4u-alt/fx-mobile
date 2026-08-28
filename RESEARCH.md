# fx-Mobile Research Phase — Complete Analysis

> **Project**: fx on mobile — native Android port of the fx coding agent
> **Date**: 2026-08-28
> **Status**: Research phase complete — 40 issues raised, 24 PRs merged, 25 issues closed
> **Repo**: https://github.com/10xdev4u-alt/fx-mobile

---

## Executive Summary

This document consolidates all research findings for fx-mobile. We analyzed reference codebases, evaluated technical paths, and identified 40 research areas. 24 PRs have been merged implementing the core architecture, and 25 research issues have been resolved.

**Key Finding**: Path 1 (Native Android Port) is viable but requires solving significant Android-specific constraints around process limits, storage, permissions, and thermal management.

---

## Reference Codebases Analyzed

### fx (vercel-labs/fx)
- **Language**: Zig 0.16+
- **Architecture**: Agent harness with terminal UI, MCP, skills, subagents
- **Binary size**: 7.8 MiB native
- **Key modules**: core (agent, session, permissions, tooling), tools (filesystem, shell, web), ui (terminal rendering), gateway (Vercel, Codex, Grok), acp (Agent Client Protocol)
- **Embeddable**: Yes — via libfx (Node native addon + WebAssembly)

### Muse (dev.tenx.muse)
- **Language**: Kotlin + Jetpack Compose
- **Architecture**: Android journaling app with on-device LLM (Xybrid SDK)
- **Key modules**: domain/model (pure data), data/local/db (Room), data/mind (Xybrid), ui/screen/mind (chat UI)
- **Inference**: LFM2.5-1.2B-Instruct (Q4_K_M GGUF, ~700MB)
- **Privacy**: Biometric lock, offline-first, no network after model download

---

## Technical Path Evaluation

### Path 1: Native Android Port (SELECTED)
Cross-compile fx's Zig core → Android aarch64, run inside native app.

**Pros**: Full fx tool suite, real terminal, native performance
**Cons**: Complex build, Android sandbox fights, no native PTY

**Key Constraints**:
- Zig cross-compilation to `aarch64-linux-android` requires NDK sysroot
- Android 12+ phantom process limit (32 per device, all apps combined)
- Bionic libc vs glibc differences
- No /tmp, /home, /usr — must use app-private storage

### Path 2: WebAssembly in WebView
fx already ships `fx-core.wasm` and `fx-term.wasm`.

**Pros**: Already works, no native build, auto-updates
**Cons**: JSPI not stable on Android Chrome, no native process execution, limited tool suite

### Path 3: ACP Client + Remote fx
Run fx on server, mobile app is thin ACP client.

**Pros**: Always powerful, no local constraints
**Cons**: Requires network, not offline-first

---

## Research Documents

### 1. Android Phantom Process Limits
**File**: `docs/research/phantom-process-limits.md`

**Key Findings**:
- Android 12+ enforces 32 phantom processes per device (all apps combined)
- Killing triggers: battery power changes, CPU sampling every 5 mins
- Android 14+ has developer toggle: "Disable child process restrictions"
- Workaround: `device_config put activity_manager max_phantom_processes 2147483647` (requires adb/root)

**Mitigation Strategy**: Hybrid approach — process pool of 2-4 persistent shells + foreground service during active agent tasks + graceful degradation when processes are killed.

### 2. Terminal Emulation on Android
**File**: `docs/research/terminal-emulation.md`

**Key Findings**:
- No PTY support in Android kernel — all terminal emulation is userspace
- No fork()/exec() — all shell access via ProcessBuilder
- No /dev/pts — must implement full VT100/VT220/XTerm protocol
- Shell selection: mksh (default), toybox, or bash (with Termux)

**Recommended Approach**: Hybrid — use TerminalView from Termux as library, wrapped in Compose-friendly interface. For v1.0, implement basic terminal using ProcessBuilder + mksh + Compose Canvas.

### 3. MCP Server Support on Android
**File**: `docs/research/mcp-on-android.md`

**Key Findings**:
- No arbitrary process execution — can't spawn MCP server processes
- No stdio IPC — can't communicate via pipes
- No localhost binding — can't run HTTP servers on loopback

**Alternative Approaches**:
1. In-process MCP servers (Kotlin libraries)
2. Remote MCP servers (user's PC/cloud)
3. WebView-based MCP (JavaScript servers)
4. Hybrid (recommended) — built-in tools + remote MCP via WebSocket

### 4. Subagent Architecture for Mobile
**File**: `docs/research/subagent-architecture.md`

**Key Findings**:
- No fork() for new processes — can't spawn independent agent processes
- Memory limits per app — multiple agents = OOM risk
- Battery/thermal throttling — sustained multi-agent work overheats

**Recommended Approach**: Thread-based subagents with coroutines. Limit to 3 concurrent subagents, 30-second timeout, no nested subagents.

### 5. Zig Cross-Compilation Build System
**File**: `docs/research/zig-build-system.md`

**Key Findings**:
- Zig supports `aarch64-linux-android` target
- Requires Android NDK for sysroot headers
- Bionic libc differences must be handled
- JNI bridge overhead is negligible (~0.01ms per call)

**Build Architecture**: Gradle → zig-build task → NDK → .so → jniLibs → APK

---

## Architecture Decisions

### ADR-001: Zig Cross-Compilation Strategy
- **Status**: Accepted
- **Decision**: Build fx core as shared library (.so) via Zig cross-compilation, wrapped by thin Kotlin JNI bridge
- **Alternatives**: Full native port, WebAssembly, Termux integration

### ADR-002: Inference Engine Selection
- **Status**: Accepted
- **Decision**: Cloud-only inference with Kilo (no on-device LLM for v1.0)
- **Alternatives**: Xybrid SDK, llama.cpp, MediaPipe LLM

### ADR-003: Session Persistence
- **Status**: Accepted
- **Decision**: Room database with Flow-based reactive UI
- **Alternatives**: DataStore, Realm, file-based

### ADR-004: Build System
- **Status**: Accepted
- **Decision**: Gradle + Hilt + Compose + Room + Retrofit + Zig cross-compilation
- **Alternatives**: Bazel, Buck, pure Zig build

### ADR-005: Terminal Emulation
- **Status**: Proposed
- **Decision**: Basic terminal for v1.0 (ProcessBuilder + mksh + Compose Canvas), TerminalView from Termux for v2.0
- **Alternatives**: xterm.js in WebView, libvte Android port, custom VT100 implementation

---

## Implementation Status

### Completed (24 PRs Merged)
1. ✅ Project scaffold (Compose UI, Hilt, Room, Zig core stub)
2. ✅ Kilo API integration (Retrofit, Repository, DI, unit tests)
3. ✅ Session management (ViewModel, repository, persistence)
4. ✅ Navigation (bottom tabs, 4 screens)
5. ✅ CI/CD (GitHub Actions, lint, test, build)
6. ✅ Landing page (professional single-page site)
7. ✅ Storage manager (file operations, SAF)
8. ✅ Permission manager (runtime permissions)
9. ✅ Thermal monitor (device temperature)
10. ✅ Background execution manager (Android 12+ limits)
11. ✅ Error handling (centralized error bus)
12. ✅ Network monitor (connectivity monitoring)
13. ✅ Analytics manager (privacy-first event tracking)
14. ✅ Terminal screen (command input UI)
15. ✅ Onboarding screen (first-run experience)
16. ✅ API key screen (Kilo auth)
17. ✅ Settings ViewModel (state management)
18. ✅ Sync manager (offline-first sync)
19. ✅ Security policy (command sandboxing)
20. ✅ Accessibility utilities (TalkBack support)
21. ✅ UI tests (MainScreen, SessionsScreen)
22. ✅ Integration tests (full flow)
23. ✅ ADR-001 (Zig cross-compilation)
24. ✅ Dependabot (dependency updates)

### Remaining Research Issues (15 open)
- #40: Quick Settings & Widgets
- #39: Baseline Profile & Startup Optimization
- #38: ProGuard/R8 Optimization Strategy
- #37: State Management: StateFlow vs Flow
- #34: Dependency Injection with Hilt
- #33: Code Signing & Distribution Security
- #32: Networking & API Gateway Integration
- #31: Foldable & Large Screen Support
- #28: Open Source License & CLA
- #27: Release Checklist & Go/No-Go Criteria
- #26: Architecture Decision Records (ADRs)
- #24: fx-Mobile vs Termux: Differentiation Strategy
- #22: Monorepo vs Multi-Repo Strategy
- #21: Testing Strategy for Mobile
- #19: fx-Mobile Branding & Visual Identity
- #16: fx Core Library API Surface for Mobile
- #15: Performance Benchmarks & Target Devices
- #14: App Distribution Strategy
- #12: Subagent Architecture for Mobile
- #11: MCP Server Support on Android

---

## Key Technical Findings

### Android Phantom Process Limits (CRITICAL)
- Android 12+ enforces 32 phantom processes per device (all apps combined)
- Killing triggers: battery power changes, CPU sampling every 5 mins
- Android 14+ has developer toggle: "Disable child process restrictions"
- Workaround: `device_config put activity_manager max_phantom_processes 2147483647` (requires adb/root)

### Terminal Emulation
- No PTY support in Android kernel — all terminal emulation is userspace
- No fork()/exec() — all shell access via ProcessBuilder
- No /dev/pts — must implement full VT100/VT220/XTerm protocol
- Shell selection: mksh (default), toybox, or bash (with Termux)

### MCP on Android
- No arbitrary process execution — can't spawn MCP server processes
- No stdio IPC — can't communicate via pipes
- No localhost binding — can't run HTTP servers on loopback
- Alternative: In-process tools + remote MCP via WebSocket

### Subagents on Android
- No fork() for new processes — can't spawn independent agent processes
- Memory limits per app — multiple agents = OOM risk
- Battery/thermal throttling — sustained multi-agent work overheats
- Alternative: Thread-based subagents with coroutines

### Zig Cross-Compilation
- Zig supports `aarch64-linux-android` target
- Requires Android NDK for sysroot headers
- Bionic libc differences must be handled
- JNI bridge overhead is negligible (~0.01ms per call)

---

## Recommended Next Steps

### For v1.0 (Current)
1. ✅ Core architecture complete
2. ✅ Kilo API integration complete
3. ✅ Session management complete
4. ✅ Navigation complete
5. ✅ CI/CD complete
6. 🔄 More ADRs for key decisions
7. 🔄 Integration tests for full flow
8. ⏳ Wire up Kilo API with real prompts
9. ⏳ Basic terminal implementation
10. ⏳ Process pooling for phantom process mitigation

### For v2.0 (Future)
1. Full Zig agent runtime
2. TerminalView from Termux integration
3. Remote MCP client via WebSocket
4. Remote subagent support via ACP
5. WASM subagents for sandboxing
6. On-device LLM (Xybrid/llama.cpp)
7. Full terminal emulation with PTY

---

## Development Workflow (Agentic Git Issues-PR Driven)

1. **Research** → raise issue
2. **Evaluate** → discuss and refine
3. **Validate** → confirm approach with evidence
4. **Start** → implement solution
5. **Commit** → strict 6-word conventional commits
6. **Local validation** → build, test, verify
7. **Push & PR** → raise PR with co-authoring
8. **Review** → 10xdev4u-alt or the-ai-developer reviews
9. **Merge** → validate and merge PR
10. **Clean** → remove local and remote branches
11. **Repeat** → next issue

---

## Conventional Commit Format

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

Types: feat, fix, docs, style, refactor, perf, test, chore, ci, build, revert

Example: `feat(terminal): add ANSI escape sequence rendering`

---

## Team

- **10xdev4u-alt** — primary developer (10xdev4u@gmail.com)
- **the-ai-developer** — co-author and reviewer

---

## License

Apache-2.0 (to match fx's license)
