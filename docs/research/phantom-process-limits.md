# Android Phantom Process Limits — Deep Research

> **Issue**: #1 [RESEARCH] Android Phantom Process Limits & Mitigation Strategy
> **Status**: CRITICAL — affects every aspect of fx on mobile
> **Date**: 2026-08-28

---

## What Are Phantom Processes?

Android 12 (API 31) introduced `PhantomProcessList` — a mechanism that monitors and kills forked child processes. The limit is **32 processes per device** (not per app). When exceeded, Android kills the oldest processes first.

## How Fx Creates Processes

Every fx action spawns processes:

| Action | Processes Created |
|--------|------------------|
| Shell command | 1 per command |
| Terminal session | 1 PTY + 1 shell |
| Subagent | 1 per subagent |
| MCP server | 1 per server |
| Git operation | 1-3 per operation |
| Build command | 1-N per build step |

A typical fx session with 5 commands, 2 subagents, and 1 MCP server = **9 processes**. With 3 users on a device, that's 27/32 — one more command and processes start dying.

## Android Version Behavior

| Version | Behavior | Mitigation |
|---------|----------|------------|
| Android 11 and below | No phantom process limit | None needed |
| Android 12 (API 31) | 32 process limit, no toggle | `device_config` workaround (adb/root) |
| Android 12L (API 32) | 32 process limit + developer toggle | Settings → Developer options → Disable child process restrictions |
| Android 13 (API 33) | Same as 12L | Same toggle |
| Android 14 (API 34) | `settings_enable_monitor_phantom_procs` flag | Automatic disable when flag is false |

## The Killing Algorithm

From AOSP source (`PhantomProcessList.java`):

```java
// Processes are sorted by:
// 1. Parent app's oom_adj (higher = killed first)
// 2. Process age (older = killed first)
// Then killed from the end until count <= MAX_PHANTOM_PROCESSES
```

**Key insight**: Foreground apps have lower oom_adj, so their processes survive. Background app processes die first.

## Mitigation Strategies for Fx

### Strategy 1: Process Pooling (Recommended)
Instead of fork-per-command, maintain a pool of reusable shell processes.

```
┌─────────────────────────────────┐
│         fx-mobile app           │
├─────────────────────────────────┤
│     Process Pool (max 4)        │
│  ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐│
│  │Shell│ │Shell│ │Shell│ │Shell││
│  │  1  │ │  2  │ │  3  │ │  4  ││
│  └─────┘ └─────┘ └─────┘ └─────┘│
├─────────────────────────────────┤
│     Command Queue               │
└─────────────────────────────────┘
```

**Pros**: Bounded process count, fast command execution
**Cons**: Complex state management, shell state leaks between commands

### Strategy 2: Single-Process Architecture
Run everything in a single persistent shell process, use cooperative multitasking.

**Pros**: Minimal process count
**Cons**: No true parallelism, complex async handling

### Strategy 3: Foreground Service Priority
Run agent tasks in a foreground service to keep oom_adj low.

**Pros**: Processes survive longer
**Cons**: Notification always visible, battery impact, doesn't prevent kills

### Strategy 4: Hybrid Approach (Recommended for v1.0)
Combine strategies 1 + 3:
- Process pool of 2-4 persistent shells
- Foreground service during active agent tasks
- Graceful degradation when processes are killed

### Strategy 5: User Education + Workaround
Document the `device_config` workaround for power users:

```bash
adb shell device_config put activity_manager max_phantom_processes 2147483647
```

**Pros**: No code changes
**Cons**: Requires adb, not user-friendly, resets on reboot

## Process Lifecycle on Android

```
App foreground → oom_adj = 0 (visible app)
App background → oom_adj = 50 (cached app)
Foreground svc → oom_adj = -1000 (system kill protected)
```

Phantom processes inherit their parent app's oom_adj. So:
- Foreground fx → shell processes survive
- Background fx → shell processes killed first

## Testing Matrix

| Scenario | Expected Behavior |
|----------|------------------|
| 10 commands, foreground | All complete |
| 10 commands, background | First 6-8 complete, rest killed |
| 32+ commands, foreground | Oldest killed, newest survive |
| Multiple apps with processes | Lowest oom_adj wins |
| After process kill | Partial results preserved |

## Recommendation

Implement **Strategy 4 (Hybrid)** for v1.0:
1. Process pool of 2 persistent shells
2. Foreground service during agent runs
3. Detect process kills and resume gracefully
4. Document workaround for power users
5. Consider single-process architecture for v2.0
