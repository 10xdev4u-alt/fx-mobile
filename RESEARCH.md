# fx-Mobile Research Summary

> **Project**: fx on mobile — native Android port of the fx coding agent
> **Date**: 2026-08-28
> **Status**: Research phase complete — 40 issues raised, 0 commits, 0 PRs
> **Repo**: https://github.com/10xdev4u-alt/fx-mobile

---

## Executive Summary

This document captures the complete research phase for fx-mobile. We analyzed two reference codebases (fx and Muse), evaluated three technical paths, and identified 40 research areas that must be resolved before writing a single line of code.

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

## Research Issues Raised (40 Total)

### Critical (Must Solve Before v1.0)
1. Android Phantom Process Limits & Mitigation Strategy
2. Security Model: Sandboxing & Code Execution
3. Android 12+ Background Execution Limits
4. Code Signing & Distribution Security

### Architecture (Design Decisions Needed)
5. Zig Cross-Compilation to Android aarch64
6. Terminal Emulation on Android
7. Android Storage & Filesystem Constraints
8. Inference Engine Options for On-Device AI
9. Android Permission Model & Security
10. Touch Input & Mobile UX for Terminal
11. Project Structure & Module Architecture
12. Session Management & Persistence
13. MCP Server Support on Android
14. Subagent Architecture for Mobile
15. Performance Benchmarks & Target Devices
16. fx Core Library API Surface for Mobile
17. Offline-First Architecture & Sync
18. fx-Mobile vs Termux: Differentiation Strategy
19. fx-Mobile Monorepo vs Multi-Repo Strategy
20. fx-Mobile Thermal Management & Battery Optimization
21. fx-Mobile Networking & API Gateway Integration
22. fx-Mobile Dependency Injection with Hilt
23. fx-Mobile Error Handling & Recovery
24. fx-Mobile State Management: StateFlow vs Flow
25. fx-Mobile Analytics & Telemetry Strategy
26. fx-Mobile Architecture Decision Records (ADRs)

### Infrastructure (Build & Distribution)
27. Build System & CI/CD for Android
28. App Distribution Strategy
29. Testing Strategy for Mobile
30. fx-Mobile CI/CD Pipeline & GitHub Actions
31. fx-Mobile Code Signing & Distribution Security
32. fx-Mobile ProGuard/R8 Optimization Strategy
33. fx-Mobile Baseline Profile & Startup Optimization

### Features (User-Facing)
34. Landing Page & Marketing Site
35. User Onboarding & First-Run Experience
36. Accessibility & Internationalization
37. fx-Mobile Branding & Visual Identity
38. fx-Mobile Foldable & Large Screen Support
39. fx-Mobile Quick Settings & Widgets

### Documentation
40. fx-Mobile Release Checklist & Go/No-Go Criteria
41. fx-Mobile Open Source License & CLA

---

## Key Technical Findings

### Android Phantom Process Limits (CRITICAL)
- Android 12+ enforces 32 phantom processes per device (all apps combined)
- Killing triggers: battery power changes, CPU sampling every 5 mins
- Android 14+ has developer toggle: "Disable child process restrictions"
- Workaround: `device_config put activity_manager max_phantom_processes 2147483647` (requires adb/root)

### Zig Cross-Compilation
- Zig supports `aarch64-linux-android` target
- Requires Android NDK for sysroot headers
- ReleaseSafe recommended for production
- Bionic libc differences must be handled

### Terminal Emulation
- Termux's TerminalView is proven, open source, ANSI XTerm compliant
- Handles touch gestures, virtual keyboard, PTY integration via JNI
- Recommended as library for fx-mobile

### Inference Engine: Kilo (cloud) — ACCEPTED
- **Kilo** — cloud provider for frontier coding models
  - Primary for complex agentic workflows
  - Same role as Gateway/Codex/Grok in fx CLI
- **No on-device LLM for v1.0** — cloud-only inference
  - Simpler architecture, no model download/thermal concerns
  - Offline mode = session persistence + queued prompts (no inference until online)

### Storage Constraints
- Scoped Storage (Android 10+) — apps can't access arbitrary paths
- App-private storage: /data/data/<pkg>/files/ is only guaranteed writable location
- SAF (Storage Access Framework) required for user-selected directories
- No /tmp, /home, /usr — standard Unix paths don't exist

---

## Recommended Next Steps

1. **Resolve critical issues** — phantom process limits, security model, background execution
2. **Create ADRs** — document all major architectural decisions
3. **Set up build system** — Zig cross-compilation + Android Gradle
4. **Build proof-of-concept** — minimal terminal + agent on Android
5. **Iterate on UX** — touch-first terminal, onboarding, session management

---

## Development Workflow (Agentic Git Issues-PR Driven)

1. **Research** → raise issues (DONE — 40 issues)
2. **Evaluate** → discuss and refine issues
3. **Validate** → confirm approach with evidence
4. **Start** → implement solution for one issue
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
