import { Component, computed, inject, OnDestroy, signal } from '@angular/core';
import {
  BattleService as BattleApiService,
  BattleEvaluationOutcome,
} from '@sagebionetworks/bixarena/api-client';
import { ConfigService } from '@sagebionetworks/bixarena/config';
import { BattleStateService } from './services/battle.service';
import { BattleStreamService } from './services/battle-stream.service';
import { PromptComposerComponent } from './prompt-composer/prompt-composer.component';
import { ModelPanelComponent } from './model-panel/model-panel.component';
import { VotingBarComponent } from './voting-bar/voting-bar.component';
import { ExamplePromptsComponent } from './example-prompts/example-prompts.component';

@Component({
  selector: 'bixarena-battle',
  imports: [
    PromptComposerComponent,
    ModelPanelComponent,
    VotingBarComponent,
    ExamplePromptsComponent,
  ],
  providers: [BattleStateService, BattleStreamService, BattleApiService],
  templateUrl: './battle.component.html',
  styleUrl: './battle.component.scss',
})
export class BattleComponent implements OnDestroy {
  readonly state = inject(BattleStateService);
  readonly hoverSide = signal<BattleEvaluationOutcome | null>(null);
  readonly promptUseLimit = inject(ConfigService).config.battle.promptUseLimit;
  readonly promptUseDots = Array.from({ length: this.promptUseLimit }, (_, i) => i + 1);

  readonly completedMatches = computed(
    () => this.promptUseLimit - this.state.promptUsesRemaining(),
  );

  // On reveal, promptUsesRemaining already decremented so completedMatches is accurate.
  // During active phases, add 1 to show the in-progress match number.
  readonly currentMatch = computed(() => {
    const completed = this.completedMatches();
    return this.state.phase() === 'reveal' ? completed : completed + 1;
  });

  readonly allMatchesComplete = computed(() => this.completedMatches() >= this.promptUseLimit);

  onPromptSubmit(prompt: string): void {
    void this.state.submitPrompt(prompt);
  }

  onVote(outcome: BattleEvaluationOutcome): void {
    void this.state.submitVote(outcome);
  }

  onNewBattle(): void {
    this.state.reset();
  }

  onSamePrompt(): void {
    const prompt = this.state.lastPrompt();
    if (prompt && this.state.promptUsesRemaining() > 0) {
      this.state.newBattle(prompt);
    }
  }

  ngOnDestroy(): void {
    this.state.reset();
  }
}
