---
paths:
  - '**'
---

# Nx Workspace

This is an Nx 21.5.3 workspace using pnpm as the package manager. The Nx MCP server and its tools are available — use them.

## General Guidelines

- When answering questions, use the `nx_workspace` tool first to gain an understanding of the workspace architecture
- For questions around Nx configuration, best practices, or if unsure, use the `nx_docs` tool to get relevant, up-to-date docs — always use this instead of assuming things about Nx configuration
- If the user needs help with an Nx configuration or project graph error, use the `nx_workspace` tool to get any errors
- To help answer questions about the workspace structure or demonstrate how tasks depend on each other, use the `nx_visualize_graph` tool

## Generation Guidelines

If the user wants to generate something, use the following flow:

1. Learn about the Nx workspace and any specifics using the `nx_workspace` tool and `nx_project_details` tool if applicable
2. Get available generators using the `nx_generators` tool
3. Decide which generator to use. If none seem relevant, check `nx_available_plugins` to see if a plugin could help
4. Get generator details using the `nx_generator_schema` tool
5. Use the `nx_docs` tool to learn more about a specific generator or technology if unsure
6. Decide which options to provide — keep options minimalistic and don't make assumptions
7. Open the generator UI using the `nx_open_generate_ui` tool
8. Wait for the user to finish the generator
9. Read the generator log file using the `nx_read_generator_log` tool
10. Use the log information to answer the user's question or continue with what they were doing

## Running Tasks Guidelines

If the user wants help with tasks or commands (keywords like "test", "build", "lint", or similar actions):

1. Use `nx_current_running_tasks_details` to get the list of tasks (includes completed, stopped, or failed tasks)
2. If there are tasks, ask the user if they'd like help with a specific one, then use `nx_current_running_task_output` to get the terminal output
3. Use the terminal output to see what's wrong and help fix the problem
4. If the user wants to rerun a task, always use `nx run <taskId>` — this ensures the task runs in the Nx context the same way it originally executed
5. If a task is marked "continuous", do not offer to rerun it — it's already running. Use `nx_current_running_task_output` to verify output if needed

## CI Error Guidelines

If the user wants help fixing an error in their CI pipeline:

1. Retrieve the list of current CI Pipeline Executions (CIPEs) using `nx_cloud_cipe_details`
2. If there are errors, use `nx_cloud_fix_cipe_failure` to retrieve logs for a specific task
3. Use the task logs to diagnose and fix the problem
4. Verify the fix by running the task that was passed into `nx_cloud_fix_cipe_failure`
