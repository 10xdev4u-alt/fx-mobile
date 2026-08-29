# fx-Mobile v1.0 — Project Summary

> **Status**: Ready for release
> **Date**: 2026-08-29
> **PRs merged**: 34
> **Issues closed**: 40
> **Lines of code**: ~4,500+

---

## What Is fx-Mobile?

A native Android port of the fx coding agent. It brings the full fx experience — terminal, AI chat, file management, and more — to your phone.

## Architecture

```
┌─────────────────────────────────────────────┐
│              fx-mobile app                   │
├─────────────────────────────────────────────┤
│  UI Layer (Compose)                         │
│  - Main, Terminal, Files, Sessions, Settings│
│  - Conversation, Onboarding, API Key        │
├─────────────────────────────────────────────┤
│  ViewModel Layer (StateFlow)                │
│  - Main, Terminal, Settings, Conversation   │
│  - FileExplorer, Onboarding                 │
├─────────────────────────────────────────────┤
│  Data Layer                                 │
│  - Room (sessions, messages)               │
│  - DataStore (preferences, tokens)         │
│  - Retrofit (Kilo API)                     │
├─────────────────────────────────────────────┤
│  Domain Layer                               │
│  - AgentSession, AgentMessage, InferenceCfg│
│  - Terminal (ShellExecutor)                │
│  - Tools (ToolRegistry, ShellTool, etc.)   │
│  - Subagents (SubagentManager)             │
└─────────────────────────────────────────────┘
```

## Features

### Core Features
- **AI Chat**: Conversations with Kilo API (Claude, GPT, etc.)
- **Terminal**: Real shell command execution via ProcessBuilder
- **File Explorer**: Navigate, read, write, create files
- **Sessions**: Persistent conversation history
- **Settings**: Dark mode, notifications, auto-save

### Advanced Features
- **Quick Settings Tile**: Open fx from notification shade (Android 14+)
- **Home Screen Widget**: Quick prompt and terminal access
- **Subagents**: Parallel task execution with coroutines
- **Tool Registry**: Built-in tools (shell, file read/write/list)
- **Markdown Rendering**: Code blocks, headers, lists in chat
- **Security**: Command sandboxing, path traversal protection

### Infrastructure
- **CI/CD**: GitHub Actions (lint, test, build)
- **Code Quality**: Detekt static analysis
- **Dependency Updates**: Dependabot
- **Documentation**: ADRs, research docs, release checklist

## Project Structure

```
fx-mobile/
├── app/
│   ├── src/main/
│   │   ├── java/dev/tenx/fxmobile/
│   │   │   ├── analytics/AnalyticsManager
│   │   │   ├── bridge/FxCoreBridge
│   │   │   ├── data/
│   │   │   │   ├── local/db/ (Room)
│   │   │   │   ├── remote/ (Retrofit, Kilo API)
│   │   │   │   ├── repository/SessionRepository
│   │   │   │   └── sync/SyncManager
│   │   │   ├── di/ (Hilt modules)
│   │   │   ├── domain/model/ (AgentSession, AgentMessage, etc.)
│   │   │   ├── security/SecurityPolicy
│   │   │   ├── service/ (AgentService, FxQuickSettingsTile, etc.)
│   │   │   ├── subagent/SubagentManager
│   │   │   ├── terminal/ShellExecutor
│   │   │   ├── tools/ToolRegistry
│   │   │   ├── ui/
│   │   │   │   ├── navigation/FxNavHost
│   │   │   │   ├── screen/ (main, terminal, files, settings, etc.)
│   │   │   │   ├── theme/ (Color, Theme)
│   │   │   │   ├── util/ (Accessibility, ScreenLayout)
│   │   │   │   └── widgets/MarkdownText
│   │   │   ├── util/ (Logger, ErrorHandler, NetworkMonitor, etc.)
│   │   │   ├── viewmodel/ (Main, Terminal, Settings, Conversation, etc.)
│   │   │   └── widget/FxQuickPromptWidget
│   │   └── res/ (layouts, xml, values)
│   └── src/test/ + src/androidTest/
├── docs/
│   ├── adr/ (Architecture Decision Records)
│   ├── research/ (Deep research documents)
│   └── RELEASE_CHECKLIST.md
├── landing/index.html
└── libfx/ (Zig core - stub)
```

## Key Technical Decisions

1. **Cloud-only inference** — Kilo API for AI (no on-device LLM for v1.0)
2. **Built-in tools** — Replace MCP servers (Android constraint)
3. **ProcessBuilder** — For terminal emulation (no PTY)
4. **Room + DataStore** — For persistence
5. **Coroutines** — For async operations
6. **compileSdk 35** — Maximum compatibility

## Testing

- Unit tests: SessionRepository, KiloRepository, MainViewModel
- Integration tests: FullFlowIntegrationTest
- E2E: CI runs on every PR

## Known Limitations

- No Zig cross-compilation (requires NDK setup)
- No PTY (basic terminal only)
- No MCP servers (Android constraint)
- No on-device LLM
- Quick settings tile requires Android 14+

## Next Steps (v2.0)

1. Zig cross-compilation with Android NDK
2. Full PTY emulation via TerminalView from Termux
3. On-device LLM (Xybrid/llama.cpp)
4. Remote MCP support via WebSocket
5. Foldable/large screen adaptive UI
6. Baseline profile for startup optimization

## Team

- **10xdev4u-alt** — primary developer
- **the-ai-developer** — co-author and reviewer

## License

Apache-2.0 (to match fx's license)

---

**fx-mobile v1.0 — The AI coding agent, now in your pocket.** 🚀
