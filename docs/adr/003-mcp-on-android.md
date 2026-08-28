# ADR-003: MCP Server Support on Android

## Status
Proposed

## Context
Fx supports MCP (Model Context Protocol) servers for extensibility. However, Android's sandbox prevents spawning external processes, which is how most MCP servers work. We need an alternative approach that works within Android's constraints.

## Decision
Implement a **hybrid approach**:
1. **Built-in tools** as Kotlin classes (no MCP, but MCP-compatible interface)
2. **Remote MCP** via WebSocket for extensibility (optional, requires network)

## Built-in Tools Architecture

```kotlin
// MCP-compatible tool interface
interface McpTool {
    val name: String
    val description: String
    val inputSchema: JsonSchema
    suspend fun execute(arguments: Map<String, Any>): String
}

// Built-in tools
class FileSystemTool : McpTool {
    override val name = "filesystem"
    override val description = "Read, write, and manage files"
    
    override suspend fun execute(arguments: Map<String, Any>): String {
        val action = arguments["action"] as String
        return when (action) {
            "read" -> storageManager.readFile(arguments["path"] as String)
            "write" -> storageManager.writeFile(arguments["path"] as String, arguments["content"] as String)
            "list" -> storageManager.listFiles(arguments["path"] as String).toString()
            else -> "Unknown action: $action"
        }
    }
}

class GitTool : McpTool {
    override val name = "git"
    override val description = "Git operations"
    
    override suspend fun execute(arguments: Map<String, Any>): String {
        // Execute git commands via ProcessBuilder
    }
}

class ShellTool : McpTool {
    override val name = "shell"
    override val description = "Execute shell commands"
    
    override suspend fun execute(arguments: Map<String, Any>): String {
        val command = arguments["command"] as String
        return shellExecutor.execute(command)
    }
}
```

## Tool Registry

```kotlin
class ToolRegistry @Inject constructor(
    private val fileSystemTool: FileSystemTool,
    private val gitTool: GitTool,
    private val shellTool: ShellTool,
    private val webFetchTool: WebFetchTool
) {
    private val tools = mapOf(
        "filesystem" to fileSystemTool,
        "git" to gitTool,
        "shell" to shellTool,
        "web_fetch" to webFetchTool
    )
    
    suspend fun execute(toolName: String, arguments: Map<String, Any>): String {
        val tool = tools[toolName] ?: return "Unknown tool: $toolName"
        return tool.execute(arguments)
    }
    
    fun getToolSchemas(): List<ToolSchema> {
        return tools.values.map { ToolSchema(it.name, it.description, it.inputSchema) }
    }
}
```

## Remote MCP via WebSocket

For users who want full MCP support:

```kotlin
class RemoteMcpClient @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    private var webSocket: WebSocket? = null
    
    fun connect(serverUrl: String) {
        val request = Request.Builder().url(serverUrl).build()
        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                handleMcpMessage(Json.decodeFromString<McpMessage>(text))
            }
        })
    }
    
    suspend fun callTool(toolName: String, arguments: Map<String, Any>): String {
        val request = McpRequest(
            jsonrpc = "2.0",
            id = UUID.randomUUID().toString(),
            method = "tools/call",
            params = mapOf("name" to toolName, "arguments" to arguments)
        )
        webSocket?.send(Json.encodeToString(request))
        // Wait for response via suspendCancellableCoroutine
    }
}
```

## Integration with Kilo API

```kotlin
class AgentWithTools @Inject constructor(
    private val kiloRepository: KiloRepository,
    private val toolRegistry: ToolRegistry
) {
    suspend fun sendMessage(sessionId: String, userMessage: String): String {
        // 1. Send user message + tool schemas to Kilo
        val response = kiloRepository.sendMessage(
            messages = listOf(AgentMessage(userMessage)),
            config = InferenceConfig(),
            tools = toolRegistry.getToolSchemas()
        )
        
        // 2. If Kilo requests a tool call, execute it
        if (response.toolCall != null) {
            val result = toolRegistry.execute(response.toolCall.name, response.toolCall.arguments)
            
            // 3. Send tool result back to Kilo
            return kiloRepository.sendMessage(
                messages = listOf(
                    AgentMessage(userMessage),
                    AgentMessage(response.content),
                    AgentMessage("Tool result: $result", role = MessageRole.SYSTEM)
                ),
                config = InferenceConfig()
            )
        }
        
        return response.content
    }
}
```

## Alternatives Considered

### Alternative 1: In-process MCP servers only
- **Pros**: Works offline, no network needed
- **Cons**: Must reimplement each server in Kotlin, no ecosystem reuse

### Alternative 2: Remote MCP only
- **Pros**: Full MCP ecosystem
- **Cons**: Requires network, not offline-friendly

### Alternative 3: WebView-based MCP
- **Pros**: Can reuse JS MCP servers
- **Cons**: WebView overhead, complex bridge

## Consequences
- ✅ Works offline with built-in tools
- ✅ Extensible via remote MCP
- ✅ MCP-compatible interface for future
- ⚠️ Built-in tools must be maintained
- ⚠️ Remote MCP requires network

## Implementation
See `docs/research/mcp-on-android.md` for detailed research.
