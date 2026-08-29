# fx-Mobile Roadmap

> Vision: Bring the full fx coding agent experience to every platform

---

## v1.1 — Polish & Performance (Next)

### UI/UX Improvements
- [ ] Streaming chat responses (real-time token display)
- [ ] Dark/light theme toggle (currently dark only)
- [ ] Search within sessions
- [ ] Export/import sessions (JSON format)
- [ ] Widget configuration screen
- [ ] Haptic feedback for terminal keys
- [ ] External keyboard shortcuts

### Performance
- [ ] Baseline profile for startup optimization
- [ ] Lazy loading for large file lists
- [ ] Image caching for markdown
- [ ] Database query optimization

### Bug Fixes
- [ ] Terminal scrollback buffer limits
- [ ] Memory leak in message observation
- [ ] Configuration changes restore state

---

## v2.0 — Full Agent Experience

### Core
- [ ] Zig cross-compilation with Android NDK
- [ ] Full PTY emulation via TerminalView from Termux
- [ ] On-device LLM (Xybrid/llama.cpp)
- [ ] Remote MCP support via WebSocket

### UI/UX
- [ ] Foldable/large screen adaptive UI
- [ ] Desktop mode support (Samsung DeX)
- [ ] Multi-window support
- [ ] Drag and drop files
- [ ] Command palette (Ctrl+K)

### Features
- [ ] Git integration (clone, commit, push, pull)
- [ ] Code editor with syntax highlighting
- [ ] Diff viewer
- [ ] Task runner (npm, cargo, make, etc.)
- [ ] Package manager integration

---

## v3.0 — Platform Expansion

### Platforms
- [ ] iOS port (SwiftUI)
- [ ] Web app (WebAssembly)
- [ ] Desktop (Compose Multiplayer)

### Features
- [ ] Cloud sync across devices
- [ ] Collaborative sessions
- [ ] Voice input for prompts
- [ ] AR code visualization

---

## Research Areas

### Active Research
1. Zig → Android cross-compilation build system
2. On-device LLM performance on mobile
3. PTY emulation without kernel support
4. MCP server sandboxing on Android

### Completed Research
1. Android phantom process limits and mitigation
2. Terminal emulation approaches
3. Subagent architecture for mobile
4. Security model and sandboxing
5. fx core library API surface for mobile

---

## How to Contribute

1. Check open issues for tasks
2. Read ADRs for architecture decisions
3. Follow existing code patterns
4. Write tests for new features
5. Update documentation

---

*Last updated: 2026-08-29*
