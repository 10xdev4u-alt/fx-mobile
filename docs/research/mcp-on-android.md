# MCP Server Support on Android — Deep Research

> **Issue**: #11 [RESEARCH] MCP Server Support on Android
> **Status**: HIGH — extensibility is core to fx
> **Date**: 2026-08-28

---

## What is MCP?

Model Context Protocol (MCP) is a standard for AI models to interact with external tools. MCP servers run as separate processes and communicate via stdio or HTTP.

## Why MCP Doesn't Work on Android

| Constraint | Impact |
|------------|--------|
| No arbitrary process execution | Can't spawn MCP server processes |
| No stdio IPC | Can't communicate via pipes |
| Android sandbox | Can't access files outside app-private storage |
| No localhost binding | Can't run HTTP servers on loopback |
| Background execution limits | Long-running services get killed |

## MCP Server Types

### 1. Stdio-based (most common)
```
┌─────────────┐     pipes      ┌─────────────┐
│  AI Model   │ ──────────────→ │ MCP Server  │
│  (client)   │ ←────────────── │ (process)   │
└─────────────┘                └─────────────┘
```

**Problem**: Android apps can't create pipes to child processes.

### 2. HTTP-based (Streamable HTTP)
```
┌─────────────┐     HTTP      ┌─────────────┐
│  AI Model   │ ────────────→ │ MCP Server  │
│  (client)   │ ←────────────── │ (HTTP server)│
└─────────────┘                └─────────────┘
```

**Problem**: Android apps can't bind to localhost ports.

### 3. SSE-based (legacy)
```
┌─────────────┐     SSE       ┌─────────────┐
│  AI Model   │ ────────────→ │ MCP Server  │
│  (client)   │ ←────────────── │ (SSE server) │
└─────────────┘                └─────────────┘
```

**Problem**: Same as HTTP.

## Alternative Approaches

### Approach 1: In-Process MCP Servers
Run MCP servers as Kotlin/Java libraries within the app process.

```
┌─────────────────────────────────────────────┐
│              fx-mobile app                   │
├─────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐           │
│  │ MCP Server  │  │ MCP Server  │  ...      │
│  │ (in-process)│  │ (in-process)│           │
│  └─────────────┘  └─────────────┘           │
├─────────────────────────────────────────────┤
│  MCP Protocol (in-memory)                   │
├─────────────────────────────────────────────┤
│  AI Model (Kilo API)                        │
└─────────────────────────────────────────────┘
```

**Pros**: No process spawning, works on Android
**Cons**: Must reimplement each server in Kotlin, no ecosystem reuse

### Approach 2: Remote MCP Servers
Connect to MCP servers running on user's PC or cloud.

```
┌─────────────┐     HTTP/WebSocket     ┌─────────────┐
│  fx-mobile  │ ──────────────────────→ │ MCP Server  │
│  (client)   │ ←────────────────────── │ (remote)    │
└─────────────┘                        └─────────────┘
```

**Pros**: Full MCP ecosystem, no reimplementation
**Cons**: Requires network, not offline-friendly

### Approach 3: WebView-based MCP
Run JavaScript MCP servers in WebView.

```
┌─────────────────────────────────────────────┐
│              fx-mobile app                   │
├─────────────────────────────────────────────┤
│  ┌─────────────────────────────────────┐    │
│  │  WebView                            │    │
│  │  ┌─────────────┐  ┌─────────────┐   │    │
│  │  │ MCP Server  │  │ MCP Server  │   │    │
│  │  │ (JS)        │  │ (JS)        │   │    │
│  │  └─────────────┘  └─────────────┘   │    │
│  └─────────────────────────────────────┘    │
├─────────────────────────────────────────────┤
│  JavaScript Bridge (Kotlin ↔ JS)            │
└─────────────────────────────────────────────┘
```

**Pros**: Can reuse JS MCP servers, sandboxed
**Cons**: WebView overhead, complex bridge, performance

### Approach 4: Hybrid (Recommended)
Combine in-process for built-in tools, remote for extensibility.

```
┌─────────────────────────────────────────────┐
│              fx-mobile app                   │
├─────────────────────────────────────────────┤
│  Built-in Tools (in-process)                │
│  ┌─────────────┐  ┌─────────────┐           │
│  │ File System │  │ Git         │  ...      │
│  └─────────────┘  └─────────────┘           │
├─────────────────────────────────────────────┤
│  Remote MCP (optional)                      │
│  ┌─────────────────────────────────────┐    │
│  │  WebSocket to user's PC             │    │
│  └─────────────────────────────────────┘    │
└─────────────────────────────────────────────┘
```

## Built-in Tools for v1.0

Instead of MCP servers, implement these as Kotlin classes:

| Tool | Implementation | MCP Equivalent |
|------|----------------|----------------|
| File read/write | `StorageManager` | filesystem MCP |
| Git operations | `GitManager` | git MCP |
| Shell commands | `ShellExecutor` | shell MCP |
| Web fetch | `OkHttp` | fetch MCP |
| Search | `FileManager` | search MCP |

## Remote MCP Architecture

For users who want full MCP support:

```kotlin
class RemoteMcpClient {
    private var webSocket: WebSocket? = null
    
    fun connect(serverUrl: String) {
        val request = Request.Builder().url(serverUrl).build()
        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                // Handle MCP protocol message
                handleMcpMessage(text)
            }
        })
    }
    
    fun callTool(toolName: String, args: Map<String, Any>): String {
        val request = McpRequest(toolName, args)
        webSocket?.send(Json.encodeToString(request))
        // Wait for response
    }
}
```

## Recommendation

For v1.0:
1. Implement **built-in tools** as Kotlin classes (no MCP)
2. Design tool interface to be MCP-compatible for future
3. Document how to use remote MCP via WebSocket

For v2.0:
4. Add remote MCP client for extensibility
5. Consider WebView-based MCP for JS servers
