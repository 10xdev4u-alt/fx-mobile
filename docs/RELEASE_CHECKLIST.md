# v1.0 Release Checklist

## Code Quality
- [x] Project scaffold (Compose, Hilt, Room)
- [x] CI/CD pipeline (lint, test, build)
- [x] Unit tests for repositories and viewmodels
- [x] Integration tests for full flow
- [x] Code quality checks (detekt)

## Core Features
- [x] Session management with Room persistence
- [x] AI chat with Kilo API integration
- [x] Terminal with real command execution
- [x] File explorer with directory navigation
- [x] Settings with persistent preferences
- [x] Quick settings tile (Android 14+)
- [x] Home screen widget

## Architecture
- [x] Clean architecture (data/domain/ui layers)
- [x] MVVM with StateFlow
- [x] Hilt dependency injection
- [x] Reactive UI with Compose
- [x] Proper error handling

## Documentation
- [x] ADR-001: Zig cross-compilation strategy
- [x] ADR-002: Terminal emulation approach
- [x] ADR-003: MCP on Android
- [x] Research documents for all major decisions
- [x] Professional landing page

## Security
- [x] API key storage in encrypted DataStore
- [x] Command sandboxing with allowlist
- [x] Path traversal protection
- [x] Code signing utility

## Performance
- [x] Database indexing
- [x] Coroutine-based async operations
- [x] Lazy column for large lists

## Known Limitations (v1.0)
- No Zig cross-compilation (requires NDK setup)
- No PTY support (basic terminal only)
- No MCP servers (Android constraint — built-in tools instead)
- No on-device LLM (cloud-only inference)
- Quick settings tile requires Android 14+

## Ready for Release
- [x] All CI checks pass
- [x] No critical bugs
- [x] Feature complete for v1.0 scope
- [x] Documentation complete
