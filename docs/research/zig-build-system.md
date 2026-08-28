# Zig Cross-Compilation Build System — Deep Research

> **Issue**: #2 [RESEARCH] Zig Cross-Compilation to Android aarch64
> **Status**: CRITICAL — core to the entire project
> **Date**: 2026-08-28

---

## Zig Android Targets

| Target | ABI | API Level | Notes |
|--------|-----|-----------|-------|
| `aarch64-linux-android` | arm64-v8a | 21+ | Modern phones |
| `arm-linux-androideabi` | armeabi-v7a | 19+ | Older phones |
| `x86_64-linux-android` | x86_64 | 21+ | Emulators |
| `i686-linux-android` | x86 | 19+ | Rare |

## Build Challenges

### 1. NDK Sysroot
Zig needs Android NDK for headers and libraries. The NDK path must be provided:

```bash
zig build -Dtarget=aarch64-linux-android \
  -Dsysroot=/path/to/ndk/toolchains/llvm/prebuilt/linux-x86_64/sysroot
```

### 2. Bionic libc Differences
Android's Bionic libc is not glibc. Key differences:

| Feature | glibc | Bionic | Impact |
|---------|-------|--------|--------|
| `fork()` | Full support | Limited | Process spawning |
| `pty.h` | Available | Missing | Terminal emulation |
| `pthread_cancel` | Supported | Not supported | Thread cancellation |
| `iconv` | Available | Limited | Character encoding |
| `dlopen()` | Full | Restricted | Dynamic loading |

### 3. JNI Bridge Overhead
Every Kotlin ↔ Zig call crosses the JNI boundary:

```
Kotlin → JNI → Zig → JNI → Kotlin

Latency: ~0.01ms per call (negligible for most ops)
Memory: Each call allocates JNI local reference
```

### 4. Binary Size
Zig binaries are typically small, but with libc linking:

| Configuration | Size |
|---------------|------|
| Static libc | ~2MB |
| Dynamic libc | ~200KB |
| Strip symbols | -30% |
| LTO | -20% |

## Build System Architecture

```
┌─────────────────────────────────────────────┐
│              Gradle Build                    │
├─────────────────────────────────────────────┤
│  zig-build task                             │
│  ├─ Download NDK                            │
│  ├─ zig build -Dtarget=aarch64-linux-android│
│  └─ Copy .so to jniLibs/arm64-v8a/         │
├─────────────────────────────────────────────┤
│  CMake (fallback)                           │
│  └─ For C/C++ dependencies                  │
├─────────────────────────────────────────────┤
│  APK Assembly                               │
│  └─ Package .so + Kotlin code               │
└─────────────────────────────────────────────┘
```

## build.zig Configuration

```zig
const std = @import("std");

pub fn build(b: *std.Build) void {
    const target = b.standardTargetOptions(.{
        .default_target = .{
            .cpu_arch = .aarch64,
            .os_tag = .linux,
            .abi = .android,
        },
    });
    const optimize = b.standardOptimizeOption(.{});

    const lib = b.addSharedLibrary(.{
        .name = "fx",
        .root_source_file = b.path("src/fx_core.zig"),
        .target = target,
        .optimize = optimize,
    });

    // Android-specific linking
    lib.linkLibC();
    
    // Link against Android system libraries
    if (target.result.os_tag == .android) {
        lib.linkSystemLibrary("log");    // Android logging
        lib.linkSystemLibrary("android"); // Android API
    }

    b.installArtifact(lib);
}
```

## JNI Bridge Design

```kotlin
// Kotlin side
class FxCoreBridge {
    init {
        System.loadLibrary("fx")
    }
    
    // Each method maps to a Zig function
    external fun initialize(): Int
    external fun createSession(config: SessionConfig): String
    external fun sendMessage(sessionId: String, message: String): String
    external fun getSessionHistory(sessionId: String): Array<Message>
    external fun destroySession(sessionId: String): Int
    external fun shutdown(): Int
}
```

```zig
// Zig side
const std = @import("std");
const jni = @import("jni.zig"); // JNI bindings

export fn Java_dev_tenx_fxmobile_bridge_FxCoreBridge_initialize() i32 {
    _ = jni.init();
    return 0;
}

export fn Java_dev_tenx_fxmobile_bridge_FxCoreBridge_createSession(
    env: *jni.Env,
    class: jni.Class,
    config: jni.String
) jni.String {
    const config_str = env.getStringUTFChars(config, null);
    defer env.releaseStringUTFChars(config, config_str);
    
    const session_id = session_create(config_str) catch |err| {
        std.log.err("Failed to create session: {s}", .{@errorName(err)});
        return null;
    };
    
    return env.newStringUTF(session_id);
}
```

## Recommendation

For v1.0:
1. Build Zig as shared library with NDK
2. Minimal JNI bridge (init, send, shutdown)
3. Most logic stays in Kotlin
4. Zig handles agent runtime only

For v2.0:
5. Full Zig agent runtime
6. Process pooling in Zig
7. Terminal emulation in Zig
