export type BattlePhase = 'landing' | 'creating' | 'streaming' | 'voting' | 'reveal' | 'error';

// Per-model streaming state. 'text' holds in-progress chunks; on complete it moves to 'messages'.
export interface ModelStreamState {
  messages: { role: 'user' | 'assistant'; content: string }[];
  text: string;
  status: 'idle' | 'waiting' | 'streaming' | 'complete' | 'error';
  finishReason: string | null;
  errorMessage: string | null;
  isSlowHint: boolean;
}

export const INITIAL_STREAM_STATE: ModelStreamState = {
  messages: [],
  text: '',
  status: 'idle',
  finishReason: null,
  errorMessage: null,
  isSlowHint: false,
};
