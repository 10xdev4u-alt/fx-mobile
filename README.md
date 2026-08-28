# fx-Mobile

> fx on mobile — native Android port of the fx coding agent

[![Build Status](https://github.com/10xdev4u-alt/fx-mobile/workflows/CI/badge.svg)](https://github.com/10xdev4u-alt/fx-mobile/actions)
[![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](LICENSE)

## Status

**Research phase complete.** 40 research issues raised. 0 commits. 0 PRs.

This project is in the early research phase. We are following an agentic git issues-PR driven development workflow where every change starts as an issue, gets validated, and is implemented through reviewed pull requests.

## Vision

Bring the full fx coding agent experience to mobile — terminal, tools, agent runtime, and all — natively on Android.

## Architecture

- **Zig core** — cross-compiled to Android aarch64 (`libfx.so`)
- **Kotlin/Compose UI** — native Android interface
- **Terminal emulation** — based on TerminalView (Termux)
- **Inference** — cloud (Gateway/Codex/Grok) + on-device (Xybrid/llama.cpp)
- **Session persistence** — Room database

## Research

See [RESEARCH.md](RESEARCH.md) for the complete research summary.

All research issues are tracked in the [GitHub Issues](https://github.com/10xdev4u-alt/fx-mobile/issues).

## Development Workflow

1. Research → raise issue
2. Evaluate → discuss and refine
3. Validate → confirm approach with evidence
4. Start → implement solution
5. Commit → strict 6-word conventional commits
6. Local validation → build, test, verify
7. Push & PR → raise PR with co-authoring
8. Review → team review
9. Merge → validate and merge
10. Clean → remove branches, repeat

## Team

- **10xdev4u-alt** — primary developer
- **the-ai-developer** — co-author and reviewer

## License

[Apache-2.0](LICENSE)
