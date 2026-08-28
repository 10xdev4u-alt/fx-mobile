# ADR-001: Zig Cross-Compilation Strategy for Android

## Status
Proposed

## Context
fx is written in Zig and needs to run natively on Android. Zig supports cross-compilation to `aarch64-linux-android`, but there are significant challenges:

1. **Bionic libc**: Android uses Bionic instead of glibc — many POSIX APIs behave differently
2. **NoPTY**: No kernel-level pseudo-terminal support — terminal emulation must be userspace
3. **Process limits**: Android 12+ enforces 32 phantom processes per device
4. **JNI overhead**: Kotlin ↔ Zig interop adds latency
5. **Binary size**: Android apps have strict APK size limits

## Decision
Build fx core as a **shared library** (`.so`) via Zig's cross-compilation, wrapped by a thin Kotlin JNI bridge.

## Architecture
```
┌─────────────────────────────────────────────┐
│              fx-mobile app                   │
├─────────────────────────────────────────────┤
│  Kotlin/Compose UI                          │
├─────────────────────────────────────────────┤
│  FxCoreBridge (JNI)                        │
├─────────────────────────────────────────────┤
│  libfx.so (Zig shared library)             │
│  ┌─────────────────────────────────────┐    │
│  │  Agent runtime                      │    │
│  │  Session management                 │    │
│  │  Tool contracts                     │    │
│  │  Permission system                  │    │
│  └─────────────────────────────────────┘    │
└─────────────────────────────────────────────┘
```

## Build Process
1. `zig build -Dtarget=aarch64-linux-android` produces `libfx.so`
2. Gradle copies `.so` into `app/src/main/jniLibs/arm64-v8a/`
3. APK packages the native library alongside Kotlin code

## Alternatives Considered

### Alternative 1: Full native port (no Kotlin)
- **Pros**: Smaller binary, direct hardware access
- **Cons**: No access to Android UI APIs, can't use Compose, massive rewrite

### Alternative 2: WebAssembly runtime
- **Pros**: Already supported by fx, runs in WebView
- **Cons**: Limited tool suite, no filesystem access, JSPI not stable on Android

### Alternative 3: Termux integration
- **Pros**: Proven Linux environment, native binaries work
- **Cons**: Requires Termux installation, not standalone app

## Consequences
- ✅ Full fx feature set available on Android
- ✅ Native performance for agent runtime
- ✅ Reuses existing Zig codebase
- ⚠️ Complex build setup (Zig + NDK + Gradle)
- ⚠️ JNI bridge requires careful memory management
- ⚠️ Binary size will be ~10MB for core

## Implementation
See `libfx/build.zig` for the build configuration and `FxCoreBridge.kt` for the JNI interface.
