# fx-Mobile

> The AI coding agent, now in your pocket 🚀

[![Build Status](https://github.com/10xdev4u-alt/fx-mobile/workflows/CI%20&%20Build/badge.svg)](https://github.com/10xdev4u-alt/fx-mobile/actions)
[![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](LICENSE)
[![Release](https://img.shields.io/github/v/release/10xdev4u-alt/fx-mobile)](https://github.com/10xdev4u-alt/fx-mobile/releases)
[![Landing Page](https://img.shields.io/badge/landing-page-6366f1)](https://10xdev4u-alt.github.io/fx-mobile/)

## Status

**v1.0 released!** 35 PRs merged. 40 issues closed. 4,500+ lines of code.

fx-mobile is a native Android port of the [fx](https://github.com/vercel-labs/fx) coding agent. It brings the full fx experience to mobile: terminal, AI chat, file management, and more.

## Features

- **AI Chat** — Conversations with 500+ AI models via Kilo API
- **Terminal** — Real shell command execution with working directory support
- **File Explorer** — Navigate, read, write, and create files
- **Sessions** — Persistent conversation history
- **Settings** — Dark mode, notifications, auto-save, model selection
- **Quick Settings Tile** — Open fx from notification shade (Android 14+)
- **Home Screen Widget** — Quick prompt and terminal access
- **Markdown Rendering** — Code blocks, headers, lists in chat
- **Subagents** — Parallel task execution
- **Tools** — Built-in shell, file read/write/list tools

## Architecture

```
┌─────────────────────────────────────────────┐
│              fx-mobile app                   │
├─────────────────────────────────────────────┤
│  UI Layer (Compose)                         │
├─────────────────────────────────────────────┤
│  ViewModel Layer (StateFlow)                │
├─────────────────────────────────────────────┤
│  Data Layer (Room, DataStore, Retrofit)     │
├─────────────────────────────────────────────┤
│  Domain Layer (Models, Tools, Terminal)     │
└─────────────────────────────────────────────┘
```

## Getting Started

### Prerequisites
- Android 8.0+ (API 26)
- 4GB RAM recommended
- Kilo API key (free at [kilocode.ai](https://kilocode.ai))

### Installation
1. Download the latest APK from [Releases](https://github.com/10xdev4u-alt/fx-mobile/releases)
2. Enable "Install from unknown sources" in Android settings
3. Install the APK
4. Open fx and enter your Kilo API key

### Development
```bash
git clone https://github.com/10xdev4u-alt/fx-mobile.git
cd fx-mobile
./gradlew assembleDebug
```

## Documentation

- [Changelog](CHANGELOG.md) — Version history
- [Roadmap](ROADMAP.md) — Future plans
- [Project Summary](PROJECT_SUMMARY.md) — Architecture overview
- [ADR](docs/adr/) — Architecture Decision Records
- [Research](docs/research/) — Deep research documents

## Contributing

1. Fork the repository
2. Create a feature branch
3. Follow conventional commits
4. Write tests for new features
5. Open a pull request

## Team

- **10xdev4u-alt** — primary developer
- **the-ai-developer** — co-author and reviewer

## License

[Apache-2.0](LICENSE) — same as fx
