# Terminal Emulation on Android — Deep Research

> **Issue**: #3 [RESEARCH] Terminal Emulation on Android
> **Status**: CRITICAL — core feature of fx
> **Date**: 2026-08-28

---

## What Makes Terminal Emulation Hard on Android

1. **No PTY**: Android kernel has no pseudo-terminal support. All terminal emulation is userspace.
2. **No fork()/exec()**: Android apps can't spawn arbitrary processes. All shell access must go through `Runtime.exec()` or `ProcessBuilder`.
3. **No /dev/pts**: No device files for PTYs. Terminal emulation must implement the full VT100/VT220/XTerm protocol.
4. **Shell selection**: No bash/zsh by default. Most Android shells are mksh or toybox.
5. **Signal handling**: Android's Bionic libc has limited signal support.

## Terminal Emulation Components

### Display Layer
```
┌─────────────────────────────────────────────┐
│              Terminal Screen                 │
├─────────────────────────────────────────────┤
│  Line 1: $ fx ask "explain this"            │
│  Line 2: This function implements...        │
│  Line 3:                                    │
│  Line 4: $ git status                       │
│  Line 5: On branch main                     │
│  Line 6: modified: src/main.rs              │
│  ...                                        │
├─────────────────────────────────────────────┤
│  Scrollback Buffer (1000 lines)             │
└─────────────────────────────────────────────┘
```

### Input Layer
- Touch events → cursor positioning
- Virtual keyboard → character input
- Special keys (Ctrl, Tab, Esc, arrows) → escape sequences
- Gestures → scroll, select, copy

### Protocol Layer
- ANSI escape sequences (colors, cursor movement, screen clearing)
- XTerm mouse reporting
- Bracketed paste mode
- Alternate screen mode

### Shell Layer
- Process management (start, stop, signal)
- I/O redirection (stdin, stdout, stderr)
- Job control (fg, bg, jobs)
- Environment variables

## Available Libraries

### 1. TerminalView (Termux)
- **Pros**: Proven, open source, ANSI XTerm compliant, handles touch
- **Cons**: Tightly coupled to Termux internals, hard to extract

### 2. xterm.js
- **Pros**: Mature, feature-rich, used by VS Code
- **Cons**: WebView-based, not native, performance overhead

### 3. Custom Compose Canvas
- **Pros**: Full control, native performance, Material 3 integration
- **Cons**: Massive implementation effort, months of work

### 4. libvte Android Port
- **Pros**: GTK's terminal widget, very mature
- **Cons**: GTK dependency, complex build, not Android-native

### 5. Build on Termux's libtermux-terminal
- **Pros**: Already works on Android, actively maintained
- **Cons**: GPL license, Termux-specific paths

## Recommended Approach: Hybrid

Use **TerminalView from Termux** as a library, wrapped in a Compose-friendly interface.

```
┌─────────────────────────────────────────────┐
│         FxTerminalCompose                    │
│  ┌─────────────────────────────────────┐    │
│  │  TerminalView (Termux)              │    │
│  │  - ANSI rendering                   │    │
│  │  - Touch handling                   │    │
│  │  - PTY emulation                    │    │
│  └─────────────────────────────────────┘    │
├─────────────────────────────────────────────┤
│  TerminalSession                             │
│  - Process management                        │
│  - I/O streams                               │
│  - Signal handling                           │
├─────────────────────────────────────────────┤
│  ShellProcess                                │
│  - mksh/toybox shell                         │
│  - Environment setup                         │
│  - Job control                               │
└─────────────────────────────────────────────┘
```

## PTY Implementation Without Kernel PTY

```kotlin
class PseudoTerminal {
    // Create pipe pair for master/slave
    val masterFd: FileDescriptor  // App reads/writes here
    val slaveFd: FileDescriptor   // Shell reads/writes here
    
    // Fork shell process with slave as stdin/stdout/stderr
    fun startShell(): Process {
        return ProcessBuilder()
            .command("/system/bin/mksh")
            .redirectInput(slaveFd)
            .redirectOutput(slaveFd)
            .redirectError(slaveFd)
            .start()
    }
    
    // Write to master → shell receives on slave
    fun write(text: String) {
        masterFd.write(text.toByteArray())
    }
    
    // Read from master → shell output appears
    fun read(): String {
        return masterFd.read()
    }
}
```

## Key Implementation Details

### 1. Shell Selection
- **mksh**: Default on Android, POSIX-compliant, small
- **toybox**: Single binary, includes many utilities
- **bash**: Not available without root or Termux

### 2. Environment Setup
```kotlin
val env = mapOf(
    "PREFIX" to "/data/data/dev.tenx.fxmobile/files/usr",
    "HOME" to "/data/data/dev.tenx.fxmobile/files/home",
    "TMPDIR" to "/data/data/dev.tenx.fxmobile/files/tmp",
    "SHELL" to "/system/bin/mksh",
    "PATH" to "/data/data/dev.tenx.fxmobile/files/usr/bin:/system/bin",
    "TERM" to "xterm-256color",
    "TERMINFO" to "/data/data/dev.tenx.fxmobile/files/usr/share/terminfo"
)
```

### 3. Signal Handling
Android signals are limited. Key mappings:
- SIGINT (Ctrl+C): Interrupt process
- SIGTSTP (Ctrl+Z): Stop process
- SIGQUIT (Ctrl+\): Quit process

### 4. I/O Streams
```kotlin
class TerminalSession {
    private val inputStream = ByteArrayOutputStream()
    private val outputStream = PipedOutputStream()
    
    fun start() {
        val shell = ProcessBuilder()
            .command("/system/bin/mksh")
            .environment().putAll(env)
            .start()
            
        // Thread: read shell output → terminal display
        Thread {
            val buffer = ByteArray(1024)
            while (true) {
                val n = shell.inputStream.read(buffer)
                if (n == -1) break
                onOutput(String(buffer, 0, n))
            }
        }.start()
        
        // Thread: read terminal input → shell
        Thread {
            val buffer = ByteArray(1024)
            while (true) {
                val n = outputStream.read(buffer)
                if (n == -1) break
                shell.outputStream.write(buffer, 0, n)
            }
        }.start()
    }
}
```

## Recommendation

For v1.0, implement a **basic terminal** using:
1. `ProcessBuilder` to run mksh
2. `PipedInputStream`/`PipedOutputStream` for I/O
3. Basic ANSI escape sequence rendering in Compose Canvas

For v2.0, integrate **TerminalView from Termux** for full PTY emulation.
