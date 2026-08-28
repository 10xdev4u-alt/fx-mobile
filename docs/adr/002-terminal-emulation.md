# ADR-002: Terminal Emulation Approach

## Status
Proposed

## Context
Fx's terminal UI relies on kernel PTY support, which doesn't exist on Android. We need to implement terminal emulation entirely in userspace. This is a core feature — without it, fx can't offer shell access or command execution.

## Decision
Implement a **two-phase approach**:
1. **v1.0**: Basic terminal using `ProcessBuilder` + `mksh` + Compose Canvas for rendering
2. **v2.0**: Integrate TerminalView from Termux for full PTY emulation

## v1.0 Architecture
```
┌─────────────────────────────────────────────┐
│  Compose Canvas (text rendering)            │
├─────────────────────────────────────────────┤
│  TerminalSession (I/O management)           │
├─────────────────────────────────────────────┤
│  ShellProcess (ProcessBuilder + mksh)       │
└─────────────────────────────────────────────┘
```

### Key Components

**ShellProcess**: Wraps `ProcessBuilder` to run mksh
```kotlin
class ShellProcess {
    private val process: Process
    private val input: OutputStream
    private val output: InputStream
    
    fun start() {
        process = ProcessBuilder()
            .command("/system/bin/mksh")
            .environment().putAll(androidEnv)
            .redirectErrorStream(true)
            .start()
        input = process.outputStream
        output = process.inputStream
    }
    
    fun write(text: String) {
        input.write(text.toByteArray())
        input.flush()
    }
    
    fun readLoop(onOutput: (String) -> Unit) {
        val buffer = ByteArray(1024)
        while (process.isAlive) {
            val n = output.read(buffer)
            if (n > 0) onOutput(String(buffer, 0, n))
        }
    }
}
```

**TerminalSession**: Manages I/O and state
```kotlin
class TerminalSession {
    private val shell = ShellProcess()
    private val ansiParser =AnsiParser()
    
    fun start(onDisplay: (TerminalCell) -> Unit) {
        shell.start()
        thread { shell.readLoop { raw -> 
            ansiParser.parse(raw, onDisplay)
        }}
    }
    
    fun sendKey(key: TerminalKey) {
        shell.write(key.toEscapeSequence())
    }
}
```

**AnsiParser**: Handles basic escape sequences
- Colors (30-37, 90-97)
- Cursor movement (H, J, K)
- Screen clearing
- Bold, underline, reverse

### Limitations of v1.0
- No PTY (some programs won't work)
- No job control (fg, bg)
- No signals (Ctrl+C may not work reliably)
- Basic ANSI (no 256-color, no true color)
- No mouse support

## v2.0 Architecture
```
┌─────────────────────────────────────────────┐
│  Compose wrapper for TerminalView           │
├─────────────────────────────────────────────┤
│  TerminalView (Termux library)              │
│  - Full VT100/VT220/XTerm emulation        │
│  - Touch handling                           │
│  - Mouse support                            │
│  - 256-color + true color                   │
├─────────────────────────────────────────────┤
│  Termux terminal emulation engine           │
└─────────────────────────────────────────────┘
```

## Alternatives Considered

### Alternative 1: xterm.js in WebView
- **Pros**: Mature, feature-rich
- **Cons**: WebView overhead, not native, performance issues

### Alternative 2: Full custom VT100 implementation
- **Pros**: Full control, no dependencies
- **Cons**: Months of work, bug-prone

### Alternative 3: libvte Android port
- **Pros**: Very mature
- **Cons**: GTK dependency, complex build

## Consequences
- ✅ Works on Android without root
- ✅ Basic terminal works in v1.0
- ✅ Path to full emulation in v2.0
- ⚠️ v1.0 has limitations (no PTY, no job control)
- ⚠️ v2.0 requires Termux dependency

## Implementation
See `docs/research/terminal-emulation.md` for detailed research.
