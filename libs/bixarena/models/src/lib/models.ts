export type VoteOutcome = 'model_a' | 'model_b' | 'tie' | 'both_poor';

export type TierLevel = 'S' | 'A' | 'B' | 'C' | 'D';

export interface PanelState {
  loading: boolean;
  streaming: boolean;
  content: string;
}

export interface BattleUiState {
  battleId: string | null;
  roundId: string | null;
  panelA: PanelState;
  panelB: PanelState;
  voted: boolean;
  voteOutcome: VoteOutcome | null;
}
