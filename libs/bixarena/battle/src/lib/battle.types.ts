export type BattlePhase =
  | 'landing'
  | 'creating'
  | 'streaming'
  | 'voting'
  | 'validating'
  | 'reveal'
  | 'error';

// Per-model streaming state. 'text' holds in-progress chunks; on complete it moves to 'messages'.
export interface ModelStreamState {
  messages: { role: 'user' | 'assistant'; content: string }[];
  text: string;
  status: 'idle' | 'waiting' | 'streaming' | 'complete' | 'error';
  finishReason: string | null;
  errorMessage: string | null;
  retryable: boolean;
  isSlowHint: boolean;
}

export const INITIAL_STREAM_STATE: ModelStreamState = {
  messages: [],
  text: '',
  status: 'idle',
  finishReason: null,
  errorMessage: null,
  retryable: false,
  isSlowHint: false,
};
