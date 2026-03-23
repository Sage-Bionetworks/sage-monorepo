export interface AiInsightsMessage {
  role: 'user' | 'assistant' | 'system';
  content: string;
}

export type AiModel =
  | 'anthropic/claude-sonnet-4-6'
  | 'openai/gpt-4o'
  | 'google/gemini-2.5-pro'
  | 'meta-llama/llama-4-maverick';

export interface AiInsightsStreamEvent {
  type: 'content' | 'done' | 'error';
  content?: string;
  fullContent?: string;
  error?: string;
}

export const AI_MODELS: { id: AiModel; label: string }[] = [
  { id: 'anthropic/claude-sonnet-4-6', label: 'Claude Sonnet 4.6' },
  { id: 'openai/gpt-4o', label: 'GPT-4o' },
  { id: 'google/gemini-2.5-pro', label: 'Gemini 2.5 Pro' },
  { id: 'meta-llama/llama-4-maverick', label: 'Llama 4 Maverick' },
];
