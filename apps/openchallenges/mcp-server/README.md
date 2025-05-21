# OpenChallenges MCP Server

## Overview

The OpenChallenges Model Context Protocol (MCP) Server bridges Large Language Models (LLMs) with the OpenChallenges REST API through MCP clients (e.g. Claude Desktop). The Server is running on port 8887 by default.

## Architecture

```mermaid
flowchart LR
    LLM

    subgraph MCP_Host [MCP Host<br> Claude Desktop]
        MCP_Client["MCP Client"]
    end

    subgraph MCP_Server [MCP Server]
        subgraph Spring_AI [Spring AI]
        end

        subgraph Java_API_Client [OC API Client]
        end
    end

    MCP_Client -- 1: Tool Registration --> MCP_Server
    User -- 2: User Prompt --> MCP_Host
    MCP_Host -- 3: Send prompt with tools --> LLM
    MCP_Client -- 4: Tool Invocation --> MCP_Server
    MCP_Server -- 5: HTTP Request --> OC_API["OC REST API"]
    OC_API -- 6: Fetch Data --> OC_DB[("OC Database")]
    MCP_Host -- 7: Return the Tool Response --> LLM
    MCP_Host -- 8: Final Answer --> User
```
