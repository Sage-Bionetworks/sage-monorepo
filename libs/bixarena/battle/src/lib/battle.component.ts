import { Component, inject, OnDestroy } from '@angular/core';
import {
  BattleService as BattleApiService,
  BattleEvaluationOutcome,
} from '@sagebionetworks/bixarena/api-client';
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
