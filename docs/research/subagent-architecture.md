# Subagent Architecture for Mobile — Deep Research

> **Issue**: #12 [RESEARCH] Subagent Architecture for Mobile
> **Status**: HIGH — agentic workflows need delegation
> **Date**: 2026-08-28

---

## What Are Subagents?

In fx, subagents are independent agent instances that handle delegated work. The main agent spawns subagents for parallel tasks, then collects results.

## Why Subagents Don't Work on Android

| Constraint | Impact |
|------------|--------|
| No fork() for new processes | Can't spawn independent agent processes |
| Memory limits per app | Multiple agents = OOM risk |
| Battery/thermal throttling | Sustained multi-agent work overheats |
| Phantom process limit | Each subagent = 1+ processes |

## Subagent Patterns

### Pattern 1: Thread-based (Recommended for v1.0)
Run subagents as coroutines within the same process.

```
┌─────────────────────────────────────────────┐
│              fx-mobile app                   │
├─────────────────────────────────────────────┤
│  Main Agent (coroutine scope)               │
│  ┌─────────────┐  ┌─────────────┐           │
│  │ Subagent 1  │  │ Subagent 2  │  ...      │
│  │ (coroutine) │  │ (coroutine) │           │
│  └─────────────┘  └─────────────┘           │
├─────────────────────────────────────────────┤
│  Shared State (Room DB)                     │
└─────────────────────────────────────────────┘
```

**Pros**: No process spawning, works on Android, low memory
**Cons**: No true parallelism (single CPU core), shared state complexity

### Pattern 2: Remote Subagents
Run subagents on user's PC or cloud.

```
┌─────────────┐     ACP      ┌─────────────┐
│  fx-mobile  │ ───────────→ │ fx (remote) │
│  (client)   │ ←─────────── │ (subagent)  │
└─────────────┘              └─────────────┘
```

**Pros**: True parallelism, full fx capabilities
**Cons**: Requires network, not offline-friendly

### Pattern 3: WebAssembly Subagents
Run subagents as WASM modules.

```
┌─────────────────────────────────────────────┐
│              fx-mobile app                   │
├─────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐           │
│  │ Subagent 1  │  │ Subagent 2  │           │
│  │ (WASM)      │  │ (WASM)      │           │
│  └─────────────┘  └─────────────┘           │
├─────────────────────────────────────────────┤
│  WASM Runtime (Wasmtime)                    │
└─────────────────────────────────────────────┘
```

**Pros**: Sandboxed, isolated, cross-platform
**Cons**: WASM runtime overhead, limited tool access

## Implementation: Thread-based Subagents

```kotlin
class SubagentManager(
    private val kiloRepository: KiloRepository,
    private val sessionDao: MessageDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val activeSubagents = mutableMapOf<String, Job>()
    
    suspend fun spawnSubagent(
        parentSessionId: String,
        task: String,
        context: String = ""
    ): String {
        val subagentId = "subagent-${UUID.randomUUID()}"
        
        val job = CoroutineScope(dispatcher).launch {
            try {
                // Build subagent prompt
                val prompt = buildSubagentPrompt(task, context)
                
                // Send to Kilo API
                val result = kiloRepository.sendMessage(
                    messages = listOf(
                        AgentMessage(
                            id = "system",
                            sessionId = subagentId,
                            role = MessageRole.SYSTEM,
                            content = prompt,
                            createdAt = System.currentTimeMillis()
                        )
                    ),
                    config = InferenceConfig()
                )
                
                // Save result to parent session
                result.onSuccess { response ->
                    sessionDao.insert(
                        MessageEntity(
                            id = "subagent-result-$subagentId",
                            sessionId = parentSessionId,
                            role = MessageRole.SYSTEM.name,
                            content = "[Subagent $subagentId completed]: ${response.content}",
                            createdAt = System.currentTimeMillis()
                        )
                    )
                }
            } catch (e: Exception) {
                // Log error, don't crash parent
            }
        }
        
        activeSubagents[subagentId] = job
        job.invokeOnCompletion { activeSubagents.remove(subagentId) }
        
        return subagentId
    }
    
    fun cancelSubagent(subagentId: String) {
        activeSubagents[subagentId]?.cancel()
    }
    
    fun cancelAll() {
        activeSubagents.values.forEach { it.cancel() }
        activeSubagents.clear()
    }
}
```

## Concurrency Model

```kotlin
class AgentOrchestrator {
    suspend fun runParallelTasks(
        parentSessionId: String,
        tasks: List<String>
    ): List<String> = coroutineScope {
        tasks.mapIndexed { index, task ->
            async {
                subagentManager.spawnSubagent(
                    parentSessionId = parentSessionId,
                    task = task,
                    context = "Task ${index + 1} of ${tasks.size}"
                )
            }
        }.awaitAll()
    }
}
```

## Resource Limits

```kotlin
object SubagentLimits {
    const val MAX_CONCURRENT_SUBAGENTS = 3
    const val MAX_SUBAGENT_DEPTH = 2  // No nested subagents
    const val SUBAGENT_TIMEOUT_MS = 30_000L  // 30 seconds
    const val MAX_MEMORY_MB = 512  // Per subagent
}
```

## Recommendation

For v1.0:
1. Implement thread-based subagents with coroutines
2. Limit to 3 concurrent subagents
3. 30-second timeout per subagent
4. No nested subagents

For v2.0:
5. Add remote subagent support via ACP
6. Implement WASM subagents for sandboxing
