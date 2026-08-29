# Changelog

All notable changes to fx-mobile will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Planned for v1.1
- Streaming chat responses
- Dark/light theme toggle
- Search within sessions
- Export/import sessions
- Widget configuration

### Planned for v2.0
- Zig cross-compilation with Android NDK
- Full PTY emulation via TerminalView from Termux
- On-device LLM (Xybrid/llama.cpp)
- Remote MCP support via WebSocket
- Foldable/large screen adaptive UI
- Desktop mode support (Samsung DeX)

---

## [0.1.0] — 2026-08-29

### Added

#### Core Features
- **AI Chat**: Conversations with Kilo API (Claude, GPT, and 500+ models)
- **Terminal**: Real shell command execution via ProcessBuilder with working directory support
- **File Explorer**: Navigate directories, read/write files, create folders
- **Sessions**: Persistent conversation history with Room database
- **Settings**: Dark mode toggle, notifications toggle, auto-save toggle, model selection
- **Quick Settings Tile**: Open fx from notification shade (Android 14+)
- **Home Screen Widget**: Quick prompt and terminal access from launcher
- **Markdown Rendering**: Code blocks, headers, lists in chat messages
- **Onboarding**: First-run experience with API key setup
- **API Key Management**: Secure storage in encrypted DataStore

#### Advanced Features
- **Subagent Manager**: Parallel task execution with coroutines (max 3 concurrent, 30s timeout)
- **Tool Registry**: Built-in tools (shell, file_read, file_write, file_list)
- **Background Execution Manager**: Monitor app foreground/background state
- **Thermal Monitor**: Device temperature monitoring with adaptive behavior
- **Network Monitor**: Real-time connectivity monitoring
- **Analytics Manager**: Privacy-first event tracking (opt-in)
- **Error Handling**: Centralized error bus with user-friendly messages
- **Command Sandboxing**: Allowlist/blocklist for terminal commands
- **Path Traversal Protection**: Prevent unauthorized file access
- **Code Signing**: Android Keystore-based signing utility

#### UI Components
- **Bottom Navigation**: 4 tabs (Home, Sessions, Terminal, Settings)
- **Message Bubbles**: User/assistant message display with Material 3
- **Markdown Widget**: Headers, code blocks, lists, bold text
- **Session Cards**: Preview with title, date, message count
- **File Row**: Directory/file icons with size display
- **Loading States**: Progress indicators during API calls
- **Snackbar Notifications**: Error and success messages

#### Architecture
- **Clean Architecture**: Data → Domain → UI layers
- **MVVM**: ViewModels with StateFlow
- **Hilt DI**: Modular dependency injection (Database, Network, Storage, Tool, Terminal, Permission modules)
- **Room Database**: Session and message persistence with Flow
- **DataStore**: Preferences and token storage
- **Retrofit**: Kilo API client with OkHttp logging
- **Coroutines**: Async operations with Dispatchers.IO

#### Infrastructure
- **CI/CD**: GitHub Actions (lint, test, build on every PR)
- **Code Quality**: Detekt static analysis
- **Dependency Updates**: Dependabot
- **Testing**: Unit tests (SessionRepository, KiloRepository, MainViewModel) + Integration tests (FullFlow)
- **Documentation**: ADRs, research documents, release checklist, landing page

#### Security
- **API Key Storage**: Encrypted DataStore
- **Command Validation**: Sanitize input, block dangerous commands
- **Path Validation**: Prevent access to /system, /data/data
- **Foreground Service**: Secure agent execution with notification

### Fixed

- Fixed Kilo API base URL from `https://api.kilo.ai/` to `https://api.kilo.ai/api/gateway`
- Fixed model IDs from `kimi-k2` to `anthropic/claude-sonnet-4.5` format
- Fixed 402 error handling for insufficient balance
- Fixed duplicate ShellExecutor binding in NetworkModule
- Fixed deprecated TileService.startActivityAndCollapse API usage
- Fixed compileSdk compatibility (pinned to 35 for AGP 8.7.3)
- Fixed kotlinx-coroutines version (pinned to 1.8.1 for kapt compatibility)
- Fixed okhttp version (pinned to 4.12.0 for compileSdk 35)
- Fixed androidx.core version (pinned to 1.13.1 for compileSdk 35)
- Fixed MainActivity unused Scaffold padding parameter
- Fixed Accessibility modifier import issues

### Documentation

- ADR-001: Zig Cross-Compilation Strategy for Android
- ADR-002: Terminal Emulation Approach (basic v1.0, full PTY v2.0)
- ADR-003: MCP Server Support on Android (built-in tools + remote MCP)
- Research: Android Phantom Process Limits & Mitigation
- Research: Terminal Emulation on Android
- Research: MCP Server Support on Android
- Research: Subagent Architecture for Mobile
- Research: Zig Build System
- Landing Page: Professional single-page marketing site
- Release Checklist: v1.0 readiness criteria
- Project Summary: Architecture, features, and roadmap

### Dependencies Added

- androidx.core:core-ktx:1.13.1
- androidx.compose:compose-bom:2024.10.00
- androidx.navigation:navigation-compose:2.8.2
- com.google.dagger:hilt-android:2.52
- androidx.room:room-runtime:2.6.1
- androidx.datastore:datastore-preferences:1.1.1
- com.squareup.okhttp3:okhttp:4.12.0
- com.squareup.retrofit2:retrofit:2.11.0
- org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1
- androidx.work:work-runtime-ktx:2.9.1
- io.mockk:mockk:1.13.8 (test)
- app.cash.turbine:turbine:1.2.1 (test)

---

## [0.0.1] — 2026-08-28

### Added

- Initial project scaffold
- Project structure and module setup
- Research phase (40 issues raised)
- README.md and .gitignore

---

[Unreleased]: https://github.com/10xdev4u-alt/fx-mobile/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/10xdev4u-alt/fx-mobile/releases/tag/v0.1.0
