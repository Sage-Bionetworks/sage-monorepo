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
import { BattleStreamService } from './battle-stream.service';

/**
 * BattleStateService — orchestrates the battle lifecycle.
 *
 * Phase state machine:
 *   landing  --[submitPrompt]--> creating
 *   creating --[API success]---> streaming
 *   creating --[API error]-----> error
 *   streaming -[both complete]-> voting   (no model errored)
 *   streaming -[both complete]-> error    (any model errored)
 *   voting   --[submitVote]----> reveal
 *   reveal   --[newBattle]-----> creating ("same question new matchup")
 *   reveal   --[reset]---------> landing  ("new battle")
 *   error    --[newBattle]-----> creating
 *   error    --[reset]---------> landing
 *
 * Dot rail (same-question matchup tracking):
 *   promptUsesRemaining decrements on each vote. Budget resets only
 *   when the user types a genuinely new prompt. Errors don't decrement.
 *
 * Timeouts (two-tier):
 *   SLOW_MODEL_THRESHOLD_MS - soft UI hint ("Taking longer than expected")
 *   MODEL_TIMEOUT_MS        - hard cutoff (forces error status)
 *   Both clear on first received chunk.
 */
@Injectable()
export class BattleStateService {
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));
  private readonly battleApi = inject(BattleApiService);
  private readonly streamService = inject(BattleStreamService);
  private readonly config = inject(ConfigService).config;

  constructor() {
    inject(DestroyRef).onDestroy(() => this.cleanupStreams());
  }

  readonly phase = signal<BattlePhase>('landing');
  readonly battleId = signal<string | null>(null);
  readonly roundId = signal<string | null>(null);
  readonly roundNumber = signal(0);
  readonly model1 = signal<Model | null>(null);
  readonly model2 = signal<Model | null>(null);
  readonly model1Stream = signal<ModelStreamState>({ ...INITIAL_STREAM_STATE });
  readonly model2Stream = signal<ModelStreamState>({ ...INITIAL_STREAM_STATE });
  readonly lastPrompt = signal<string | null>(null);
  readonly promptUsesRemaining = signal(0);
  readonly selectedOutcome = signal<BattleEvaluationOutcome | null>(null);

  // Gates transition to voting/error phase
  readonly bothComplete = computed(() => {
    const s1 = this.model1Stream().status;
    const s2 = this.model2Stream().status;
    return (s1 === 'complete' || s1 === 'error') && (s2 === 'complete' || s2 === 'error');
  });

  // Voting allowed when at least one model succeeded (both-error goes to error phase instead)
  readonly canVote = computed(
    () =>
      this.phase() === 'voting' &&
      (this.model1Stream().status !== 'error' || this.model2Stream().status !== 'error'),
  );

  readonly canReuse = computed(() => this.lastPrompt() !== null && this.promptUsesRemaining() > 0);

  readonly model1DisplayName = computed(() =>
    this.phase() === 'reveal' ? (this.model1()?.name ?? 'Model A') : 'Model A',
  );

  readonly model2DisplayName = computed(() =>
    this.phase() === 'reveal' ? (this.model2()?.name ?? 'Model B') : 'Model B',
  );

  private model1Sub?: Subscription;
  private model2Sub?: Subscription;
  private model1Timeout?: ReturnType<typeof setTimeout>;
  private model2Timeout?: ReturnType<typeof setTimeout>;
  private model1SlowTimeout?: ReturnType<typeof setTimeout>;
  private model2SlowTimeout?: ReturnType<typeof setTimeout>;

  async submitPrompt(prompt: string): Promise<void> {
    if (!this.isBrowser) return;

    // First use of a prompt gets full reuse budget; same prompt does not reset it
    const isNewPrompt = prompt !== this.lastPrompt();
    if (isNewPrompt) {
      this.promptUsesRemaining.set(this.config.battle.promptUseLimit);
    }
    this.lastPrompt.set(prompt);

    this.phase.set('creating');
    this.selectedOutcome.set(null);

    // Carry forward chat history so follow-up rounds show prior messages
    const userMsg = { role: 'user' as const, content: prompt };
    const prev1 = this.model1Stream().messages;
    const prev2 = this.model2Stream().messages;
    this.model1Stream.set({
      ...INITIAL_STREAM_STATE,
      messages: [...prev1, userMsg],
      status: 'waiting',
    });
    this.model2Stream.set({
      ...INITIAL_STREAM_STATE,
      messages: [...prev2, userMsg],
      status: 'waiting',
    });

    try {
      const battle = await firstValueFrom(
        this.battleApi.createBattle({ title: prompt.slice(0, 50) }),
      );

      if (!battle) throw new Error('Failed to create battle');

      this.battleId.set(battle.id);
      this.model1.set(battle.model1);
      this.model2.set(battle.model2);

      const round = await firstValueFrom(
        this.battleApi.createBattleRound(battle.id, {
          promptMessage: { role: 'user', content: prompt },
        }),
      );

      if (!round) throw new Error('Failed to create round');

      this.roundId.set(round.id);
      this.roundNumber.set(round.roundNumber);

      this.startStreaming(battle.id, round.id, battle.model1.id, battle.model2.id);
    } catch {
      // Reset streams so panels don't show stale "Connecting..." status
      this.model1Stream.set({
        ...INITIAL_STREAM_STATE,
        status: 'error',
        errorMessage: 'Failed to start battle',
      });
      this.model2Stream.set({
        ...INITIAL_STREAM_STATE,
        status: 'error',
        errorMessage: 'Failed to start battle',
      });
      this.phase.set('error');
    }
  }

  private isVoting = false;

  async submitVote(outcome: BattleEvaluationOutcome): Promise<void> {
    const battleId = this.battleId();
    if (!battleId || this.isVoting) return;

    this.isVoting = true;
    try {
      await firstValueFrom(this.battleApi.createBattleEvaluation(battleId, { outcome }));
    } catch {
      this.isVoting = false;
      return; // Evaluation failed — stay in voting phase
    }

    this.selectedOutcome.set(outcome);
    this.promptUsesRemaining.update((n) => Math.max(0, n - 1));
    this.phase.set('reveal');
    this.isVoting = false;

    // Best-effort: record battle end time (backend validation may update the battle concurrently)
    firstValueFrom(this.battleApi.updateBattle(battleId, { endedAt: new Date().toISOString() }))
      // eslint-disable-next-line @typescript-eslint/no-empty-function
      .catch(() => {});
  }

  // "Same question new matchup": reset streams but keep lastPrompt so reuse budget continues
  async newBattle(prompt?: string): Promise<void> {
    this.cleanupStreams();
    this.model1Stream.set({ ...INITIAL_STREAM_STATE });
    this.model2Stream.set({ ...INITIAL_STREAM_STATE });
    this.selectedOutcome.set(null);
    const text = prompt ?? this.lastPrompt();
    if (text) {
      await this.submitPrompt(text);
    }
  }

  reset(): void {
    this.cleanupStreams();
    this.phase.set('landing');
    this.battleId.set(null);
    this.roundId.set(null);
    this.roundNumber.set(0);
    this.model1.set(null);
    this.model2.set(null);
    this.model1Stream.set({ ...INITIAL_STREAM_STATE });
    this.model2Stream.set({ ...INITIAL_STREAM_STATE });
    this.lastPrompt.set(null);
    this.promptUsesRemaining.set(0);
    this.selectedOutcome.set(null);
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
    let accumulatedText = '';

    return this.streamService.streamCompletion(battleId, roundId, modelId).subscribe({
      next: (chunk) => {
        // First chunk arrived — cancel slow/timeout timers
        this.clearTimeouts(streamSignal === this.model1Stream ? 'model1' : 'model2');

        if (chunk.status === 'streaming' && chunk.content) {
          accumulatedText += chunk.content;
          streamSignal.set({
            ...streamSignal(),
            text: accumulatedText,
            status: 'streaming',
            isSlowHint: false,
          });
        } else if (chunk.status === 'complete') {
          // Move accumulated text into messages array and clear streaming text
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
          streamSignal.set({
            ...streamSignal(),
            status: 'error',
            errorMessage: chunk.errorMessage ?? 'An error occurred',
            isSlowHint: false,
          });
          this.checkBothComplete();
        }
      },
      error: () => {
        streamSignal.set({
          ...streamSignal(),
          status: 'error',
          errorMessage: 'Connection lost',
          isSlowHint: false,
        });
        this.checkBothComplete();
      },
    });
  }

  private checkBothComplete(): void {
    if (this.bothComplete()) {
      const s1 = this.model1Stream().status;
      const s2 = this.model2Stream().status;
      if (s1 === 'error' || s2 === 'error') {
        this.phase.set('error');
      } else {
        this.phase.set('voting');
      }
    }
  }

  private startTimeouts(
    streamSignal: ReturnType<typeof signal<ModelStreamState>>,
    key: 'model1' | 'model2',
  ): void {
    const slowTimeout = setTimeout(() => {
      if (streamSignal().status === 'waiting' || streamSignal().status === 'streaming') {
        streamSignal.set({ ...streamSignal(), isSlowHint: true });
      }
    }, SLOW_MODEL_THRESHOLD_MS);

    const hardTimeout = setTimeout(() => {
      if (streamSignal().status === 'waiting' || streamSignal().status === 'streaming') {
        streamSignal.set({
          ...streamSignal(),
          status: 'error',
          errorMessage: 'Model took too long to respond',
          isSlowHint: false,
        });
        this.checkBothComplete();
      }
    }, MODEL_TIMEOUT_MS);

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
    this.model1Sub?.unsubscribe();
    this.model2Sub?.unsubscribe();
    this.clearTimeouts('model1');
    this.clearTimeouts('model2');
  }
}
