# Release v1.0.0 — fx-Mobile

## 🎉 We Shipped It!

After an intensive research and development phase, **fx-mobile v1.0.0** is now live! This is a native Android port of the [fx coding agent](https://github.com/vercel-labs/fx), bringing terminal, AI chat, file management, and more to your phone.

---

## 📊 Project Stats

| Metric | Count |
|--------|-------|
| **PRs merged** | 38 |
| **Issues closed** | 40/40 (100%) |
| **Commits** | 50+ |
| **Lines of code** | ~4,500+ |
| **Files** | 59 Kotlin files |
| **Test coverage** | Unit + Integration tests |
| **CI status** | ✅ All green |

---

## ✨ What's New in v1.0

### Core Features
- **AI Chat** — Conversations with 500+ AI models via Kilo API
- **Terminal** — Real shell command execution with working directory support
- **File Explorer** — Navigate directories, read/write files, create folders
- **Sessions** — Persistent conversation history with Room database
- **Settings** — Dark mode, notifications, auto-save, model selection
- **Quick Settings Tile** — Open fx from notification shade (Android 14+)
- **Home Screen Widget** — Quick prompt and terminal access
- **Markdown Rendering** — Code blocks, headers, lists in chat
- **Onboarding** — First-run setup with API key configuration
- **API Key Management** — Secure storage in encrypted DataStore

### Advanced Features
- **Subagent Manager** — Parallel task execution (3 concurrent, 30s timeout)
- **Tool Registry** — Built-in tools (shell, file_read, file_write, file_list)
- **Background Execution Manager** — Monitor app foreground/background state
- **Thermal Monitor** — Device temperature monitoring with adaptive behavior
- **Network Monitor** — Real-time connectivity monitoring
- **Analytics Manager** — Privacy-first event tracking (opt-in)
- **Error Handling** — Centralized error bus with user-friendly messages
- **Command Sandboxing** — Allowlist/blocklist for terminal commands
- **Path Traversal Protection** — Prevent unauthorized file access
- **Code Signing** — Android Keystore-based signing utility

### Infrastructure
- **CI/CD** — GitHub Actions (lint, test, build on every PR)
- **Release Workflow** — Automated signed APK builds
- **Code Quality** — Detekt static analysis
- **Dependency Updates** — Dependabot
- **Testing** — Unit tests + Integration tests
- **Documentation** — ADRs, research docs, changelog, roadmap, contributing guide

---

## 📥 Download

### GitHub Releases
The APK is available from [GitHub Releases](https://github.com/10xdev4u-alt/fx-mobile/releases/tag/v0.1.0).

### Build Locally
```bash
git clone https://github.com/10xdev4u-alt/fx-mobile.git
cd fx-mobile
./gradlew assembleDebug
# APK at app/build/outputs/apk/debug/app-debug.apk
```

---

## 🏗️ Architecture

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

---

## 📋 Documentation

- [README](README.md) — Project overview
- [CHANGELOG](CHANGELOG.md) — Version history
- [ROADMAP](ROADMAP.md) — Future plans
- [CONTRIBUTING](CONTRIBUTING.md) — How to contribute
- [ADR](docs/adr/) — Architecture Decision Records
- [Research](docs/research/) — Deep research documents
- [Landing Page](landing/index.html) — Marketing site

---

## 🔮 What's Next

### v1.1 — Polish & Performance
- Streaming chat responses
- Dark/light theme toggle
- Search within sessions
- Export/import sessions
- Baseline profile

### v2.0 — Full Agent Experience
- Zig cross-compilation with Android NDK
- Full PTY emulation
- On-device LLM (Xybrid/llama.cpp)
- Remote MCP via WebSocket
- Foldable/large screen support

---

## 👥 Team

- **10xdev4u-alt** — primary developer
- **the-ai-developer** — co-author and reviewer

---

## 📜 License

Apache-2.0 — same as [fx](https://github.com/vercel-labs/fx)

---

**fx-mobile v1.0.0 — The AI coding agent, now in your pocket.** 🚀

Made with ❤️ by the fx-mobile team.
