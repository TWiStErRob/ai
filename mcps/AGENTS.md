# MCP servers

Each child directory contains one canonical MCP server implementation.
Register the server through each tool's native configuration;
do not duplicate the implementation inside tool overlays.

Use the repository-specific `register-mcp` skill when adding or changing a registration.

Local Kotlin STDIO servers use `kotlin` with their `.main.kts` entrypoint
and must reserve stdout for MCP protocol messages.
