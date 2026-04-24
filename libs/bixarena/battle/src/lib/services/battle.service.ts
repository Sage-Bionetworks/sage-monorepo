import { computed, DestroyRef, inject, Injectable, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { firstValueFrom, Subscription } from 'rxjs';
import {
  BattleService as BattleApiService,
  BattleEvaluationOutcome,
  Model,
} from '@sagebionetworks/bixarena/api-client';
import { ConfigService } from '@sagebionetworks/bixarena/config';
import { BattlePhase, INITIAL_STREAM_STATE, ModelStreamState } from '../battle.types';
import { SLOW_MODEL_THRESHOLD_MS, MODEL_TIMEOUT_MS } from '../battle.constants';
import { StreamHttpError, mapStreamHttpError } from '../battle-errors';
import { BattleStreamService } from './battle-stream.service';

@Injectable()
export class BattleStateService {
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));
  private readonly battleApi = inject(BattleApiService);
  private readonly streamService = inject(BattleStreamService);
  private readonly config = inject(ConfigService).config;

  constructor() {
    // Cancel active streams when the service is destroyed
    inject(DestroyRef).onDestroy(() => this.cleanupStreams());
  }

  // Phase state machine
  readonly phase = signal<BattlePhase>('landing');

  // Battle and round identifiers
  readonly battleId = signal<string | null>(null);
  readonly roundId = signal<string | null>(null);
  private completedRounds = 0;

  // Competing models
  readonly model1 = signal<Model | null>(null);
  readonly model2 = signal<Model | null>(null);

  // Per-model stream state
  readonly model1Stream = signal<ModelStreamState>({ ...INITIAL_STREAM_STATE });
  readonly model2Stream = signal<ModelStreamState>({ ...INITIAL_STREAM_STATE });

  // Prompt reuse tracking — initialPrompt is the first question (used for "Same Question New Matchup"),
  // lastPrompt is the most recent submission (may be a follow-up).
  readonly initialPrompt = signal<string | null>(null);
  readonly lastPrompt = signal<string | null>(null);
  readonly promptUsesRemaining = signal(0);
  readonly promptUseLimit = this.config.battle.promptUseLimit;
  readonly promptLengthLimit = this.config.battle.promptLengthLimit;
  private readonly roundLimit = this.config.battle.roundLimit;
  readonly promptUseDots = Array.from({ length: this.promptUseLimit }, (_, i) => i + 1);
  readonly canReuse = computed(
    () => this.initialPrompt() !== null && this.promptUsesRemaining() > 0,
  );
  readonly completedMatches = computed(
    () => this.config.battle.promptUseLimit - this.promptUsesRemaining(),
  );
  readonly currentMatch = computed(() => {
    const completed = this.completedMatches();
    return this.phase() === 'reveal' ? completed : completed + 1;
  });
  readonly allMatchesComplete = computed(
    () => this.completedMatches() >= this.config.battle.promptUseLimit,
  );

  // Voting state — current selection and stream completion gates
  readonly selectedOutcome = signal<BattleEvaluationOutcome | null>(null);
  readonly bothComplete = computed(() => {
    const s1 = this.model1Stream().status;
    const s2 = this.model2Stream().status;
    return (s1 === 'complete' || s1 === 'error') && (s2 === 'complete' || s2 === 'error');
  });
  readonly canVote = computed(
    () =>
      this.phase() === 'voting' &&
      (this.model1Stream().status !== 'error' || this.model2Stream().status !== 'error'),
  );

  // Active stream subscriptions, timer handles, and submission guards
  private model1Sub?: Subscription;
  private model2Sub?: Subscription;
  private model1Timeout?: ReturnType<typeof setTimeout>;
  private model2Timeout?: ReturnType<typeof setTimeout>;
  private model1SlowTimeout?: ReturnType<typeof setTimeout>;
  private model2SlowTimeout?: ReturnType<typeof setTimeout>;
  private isVoting = false;

  async submitPrompt(prompt: string): Promise<void> {
    if (!this.isBrowser) return;
    this.initialPrompt.set(prompt);
    this.lastPrompt.set(prompt);
    this.promptUsesRemaining.set(this.config.battle.promptUseLimit);
    this.selectedOutcome.set(null);

    const userMsg = { role: 'user' as const, content: prompt };
    this.resetStreams([userMsg], [userMsg]);

    let battle;
    try {
      battle = await firstValueFrom(this.battleApi.createBattle({ title: prompt.slice(0, 50) }));
      if (!battle) throw new Error('Failed to create battle');
    } catch (err) {
      console.error('Failed to create battle', err);
      this.setStreamError('Something went wrong. Try a new battle', false);
      return;
    }
    this.battleId.set(battle.id);
    this.model1.set(battle.model1);
    this.model2.set(battle.model2);
    try {
      await this.createRoundAndStream(battle.id, battle.model1.id, battle.model2.id, prompt);
    } catch (err) {
      console.error('Failed to start streaming', err);
      this.setStreamError('Something went wrong', true);
    }
  }

  async submitFollowUp(prompt: string): Promise<void> {
    const battleId = this.battleId();
    const model1 = this.model1();
    const model2 = this.model2();
    if (!battleId || !model1 || !model2) return;

    this.lastPrompt.set(prompt);
    this.selectedOutcome.set(null);

    const userMsg = { role: 'user' as const, content: prompt };
    this.resetStreams(
      [...this.model1Stream().messages, userMsg],
      [...this.model2Stream().messages, userMsg],
    );
    try {
      await this.createRoundAndStream(battleId, model1.id, model2.id, prompt);
    } catch (err) {
      console.error('Failed to create follow-up round', err);
      this.setStreamError('Something went wrong', true);
    }
  }

  async newMatchup(): Promise<void> {
    const prompt = this.initialPrompt();
    if (!prompt || this.promptUsesRemaining() <= 0) return;

    this.battleId.set(null);
    this.roundId.set(null);
    this.model1.set(null);
    this.model2.set(null);
    this.completedRounds = 0;
    this.lastPrompt.set(prompt);
    this.selectedOutcome.set(null);

    const userMsg = { role: 'user' as const, content: prompt };
    this.resetStreams([userMsg], [userMsg]);

    let battle;
    try {
      battle = await firstValueFrom(this.battleApi.createBattle({ title: prompt.slice(0, 50) }));
      if (!battle) throw new Error('Failed to create battle');
    } catch (err) {
      console.error('Failed to create matchup', err);
      this.setStreamError('Something went wrong. Try a new battle', false);
      return;
    }
    this.battleId.set(battle.id);
    this.model1.set(battle.model1);
    this.model2.set(battle.model2);
    try {
      await this.createRoundAndStream(battle.id, battle.model1.id, battle.model2.id, prompt);
    } catch (err) {
      console.error('Failed to start streaming', err);
      this.setStreamError('Something went wrong', true);
    }
  }

  async retry(): Promise<void> {
    const prompt = this.lastPrompt();
    const battleId = this.battleId();
    const model1 = this.model1();
    const model2 = this.model2();
    if (!prompt || !battleId || !model1 || !model2) return;

    this.resetStreams(this.model1Stream().messages, this.model2Stream().messages);
    try {
      await this.createRoundAndStream(battleId, model1.id, model2.id, prompt);
    } catch (err) {
      console.error('Failed to retry round', err);
      this.setStreamError('Something went wrong', true);
    }
  }

  async submitVote(outcome: BattleEvaluationOutcome): Promise<void> {
    const battleId = this.battleId();
    if (!battleId || this.isVoting) return;

    this.isVoting = true;
    try {
      await firstValueFrom(this.battleApi.createBattleEvaluation(battleId, { outcome }));
    } catch {
      this.isVoting = false;
      return;
    }

    this.selectedOutcome.set(outcome);
    this.promptUsesRemaining.update((n) => Math.max(0, n - 1));
    this.phase.set('reveal');
    this.isVoting = false;

    firstValueFrom(this.battleApi.updateBattle(battleId, { endedAt: new Date().toISOString() }))
      // eslint-disable-next-line @typescript-eslint/no-empty-function
      .catch(() => {});
  }

  reset(): void {
    this.cleanupStreams();
    this.completedRounds = 0;
    this.phase.set('landing');
    this.battleId.set(null);
    this.roundId.set(null);
    this.model1.set(null);
    this.model2.set(null);
    this.model1Stream.set({ ...INITIAL_STREAM_STATE });
    this.model2Stream.set({ ...INITIAL_STREAM_STATE });
    this.initialPrompt.set(null);
    this.lastPrompt.set(null);
    this.promptUsesRemaining.set(0);
    this.selectedOutcome.set(null);
  }

  private resetStreams(
    m1Messages: { role: 'user' | 'assistant'; content: string }[],
    m2Messages: { role: 'user' | 'assistant'; content: string }[],
  ): void {
    this.cleanupStreams();
    this.phase.set('creating');
    this.model1Stream.set({ ...INITIAL_STREAM_STATE, messages: m1Messages, status: 'waiting' });
    this.model2Stream.set({ ...INITIAL_STREAM_STATE, messages: m2Messages, status: 'waiting' });
  }

  // Shared round creation + streaming kickoff
  private async createRoundAndStream(
    battleId: string,
    model1Id: string,
    model2Id: string,
    prompt: string,
  ): Promise<void> {
    if (this.completedRounds >= this.roundLimit) {
      this.setStreamError(
        "You've reached the limit for this battle. Vote or start a new battle",
        false,
      );
      return;
    }
    const round = await firstValueFrom(
      this.battleApi.createBattleRound(battleId, {
        promptMessage: { role: 'user', content: prompt },
      }),
    );
    if (!round) throw new Error('Failed to create round');
    this.roundId.set(round.id);
    this.startStreaming(battleId, round.id, model1Id, model2Id);
  }

  // Shared error setter for both stream panels
  private setStreamError(message: string, retryable: boolean): void {
    const errorState = {
      ...INITIAL_STREAM_STATE,
      status: 'error' as const,
      errorMessage: message,
      retryable,
    };
    this.model1Stream.set({ ...errorState, messages: this.model1Stream().messages });
    this.model2Stream.set({ ...errorState, messages: this.model2Stream().messages });
    this.phase.set('error');
  }

  private startStreaming(
    battleId: string,
    roundId: string,
    model1Id: string,
    model2Id: string,
  ): void {
    this.phase.set('streaming');
    this.cleanupStreams();

    this.model1Sub = this.subscribeToStream(battleId, roundId, model1Id, this.model1Stream);
    this.model2Sub = this.subscribeToStream(battleId, roundId, model2Id, this.model2Stream);

    this.startTimeouts(this.model1Stream, 'model1');
    this.startTimeouts(this.model2Stream, 'model2');
  }

  private subscribeToStream(
    battleId: string,
    roundId: string,
    modelId: string,
    streamSignal: ReturnType<typeof signal<ModelStreamState>>,
  ): Subscription {
    // Per-model accumulator that builds the full response text across chunks
    let accumulatedText = '';

    return this.streamService.streamCompletion(battleId, roundId, modelId).subscribe({
      next: (chunk) => {
        // Cancel slow and hard timeouts on first received chunk
        this.clearTimeouts(streamSignal === this.model1Stream ? 'model1' : 'model2');

        if (chunk.status === 'streaming' && chunk.content) {
          // Append incoming content and update the live text display
          accumulatedText += chunk.content;
          streamSignal.set({
            ...streamSignal(),
            text: accumulatedText,
            status: 'streaming',
            isSlowHint: false,
          });
        } else if (chunk.status === 'complete') {
          // Move accumulated text into message history and clear the streaming buffer
          const current = streamSignal();
          streamSignal.set({
            ...current,
            messages: [
              ...current.messages,
              { role: 'assistant' as const, content: accumulatedText },
            ],
            text: '',
            status: 'complete',
            finishReason: chunk.finishReason ?? null,
            isSlowHint: false,
          });
          this.checkBothComplete();
        } else if (chunk.status === 'error') {
          // Handle stream-level error reported by the backend (details already logged to DB)
          streamSignal.set({
            ...streamSignal(),
            status: 'error',
            errorMessage: 'Something went wrong',
            retryable: true,
            isSlowHint: false,
          });
          this.checkBothComplete();
        }
      },
      error: (err: unknown) => {
        // Map HTTP status errors; fall back to generic connection message for transport failures
        const mapped =
          err instanceof StreamHttpError
            ? mapStreamHttpError(err.status)
            : { message: 'Connection lost', retryable: true };
        streamSignal.set({
          ...streamSignal(),
          status: 'error',
          errorMessage: mapped.message,
          retryable: mapped.retryable,
          isSlowHint: false,
        });
        this.checkBothComplete();
      },
    });
  }

  private checkBothComplete(): void {
    // Advance phase once both models reach a terminal state
    if (this.bothComplete()) {
      const s1 = this.model1Stream().status;
      const s2 = this.model2Stream().status;
      if (s1 === 'error' || s2 === 'error') {
        this.phase.set('error');
      } else {
        this.completedRounds++;
        this.phase.set('voting');
      }
    }
  }

  private startTimeouts(
    streamSignal: ReturnType<typeof signal<ModelStreamState>>,
    key: 'model1' | 'model2',
  ): void {
    // Soft hint — show "taking longer than expected" after threshold
    const slowTimeout = setTimeout(() => {
      if (streamSignal().status === 'waiting' || streamSignal().status === 'streaming') {
        streamSignal.set({ ...streamSignal(), isSlowHint: true });
      }
    }, SLOW_MODEL_THRESHOLD_MS);

    // Hard cutoff — force error if the model never responds in time
    const hardTimeout = setTimeout(() => {
      if (streamSignal().status === 'waiting' || streamSignal().status === 'streaming') {
        streamSignal.set({
          ...streamSignal(),
          status: 'error',
          errorMessage: 'Model took too long to respond',
          retryable: true,
          isSlowHint: false,
        });
        this.checkBothComplete();
      }
    }, MODEL_TIMEOUT_MS);

    // Store handles by key so they can be cleared when the first chunk arrives
    if (key === 'model1') {
      this.model1SlowTimeout = slowTimeout;
      this.model1Timeout = hardTimeout;
    } else {
      this.model2SlowTimeout = slowTimeout;
      this.model2Timeout = hardTimeout;
    }
  }

  private clearTimeouts(key: 'model1' | 'model2'): void {
    if (key === 'model1') {
      clearTimeout(this.model1SlowTimeout);
      clearTimeout(this.model1Timeout);
    } else {
      clearTimeout(this.model2SlowTimeout);
      clearTimeout(this.model2Timeout);
    }
  }

  private cleanupStreams(): void {
    // Cancel subscriptions and clear all timers for both models
    this.model1Sub?.unsubscribe();
    this.model2Sub?.unsubscribe();
    this.clearTimeouts('model1');
    this.clearTimeouts('model2');
  }
}
