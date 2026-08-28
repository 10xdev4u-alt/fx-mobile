# fx-Mobile Architecture Decision Records

This directory contains Architecture Decision Records (ADRs) for fx-mobile.

## ADR-001: Zig Cross-Compilation Strategy
- **Status**: Proposed
- **File**: [001-zig-cross-compilation.md](001-zig-cross-compilation.md)
- **Summary**: Build fx core as a shared library via Zig cross-compilation, wrapped by a thin Kotlin JNI bridge.

## ADR-002: Terminal Emulation Approach
- **Status**: Proposed
- **File**: [002-terminal-emulation.md](002-terminal-emulation.md)
- **Summary**: Basic terminal for v1.0 (ProcessBuilder + mksh + Compose Canvas), TerminalView from Termux for v2.0.

## ADR-003: MCP Server Support on Android
- **Status**: Proposed
- **File**: [003-mcp-on-android.md](003-mcp-on-android.md)
- **Summary**: Built-in tools as Kotlin classes (MCP-compatible interface) + remote MCP via WebSocket for extensibility.

## Future ADRs
- ADR-004: Session Persistence Mechanism
- ADR-005: Build System Architecture
- ADR-006: Process Pooling for Phantom Process Mitigation
- ADR-007: Built-in Tool Architecture
- ADR-008: State Management Approach
