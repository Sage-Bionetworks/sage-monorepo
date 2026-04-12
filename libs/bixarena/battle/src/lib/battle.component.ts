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
  readonly hoverSide = signal<'model1' | 'model2' | 'tie' | null>(null);
  readonly promptUseLimit = inject(ConfigService).config.battle.promptUseLimit;
  readonly promptUseDots = Array.from({ length: this.promptUseLimit }, (_, i) => i + 1);
  readonly currentBattleNumber = computed(() => {
    const remaining = this.state.promptUsesRemaining();
    const phase = this.state.phase();
    // After voting, remaining decrements but the next battle hasn't started yet
    if (phase === 'reveal' || phase === 'error') {
      return this.promptUseLimit - remaining;
    }
    return this.promptUseLimit - remaining + 1;
  });

  onPromptSubmit(prompt: string): void {
    this.state.submitPrompt(prompt);
  }

  onVote(outcome: BattleEvaluationOutcome): void {
    this.state.submitVote(outcome);
  }

  onNewBattle(): void {
    this.state.newBattle();
  }

  onSamePrompt(): void {
    const prompt = this.state.lastPrompt();
    if (prompt) {
      this.state.newBattle(prompt);
    }
  }

  ngOnDestroy(): void {
    this.state.reset();
  }
}
