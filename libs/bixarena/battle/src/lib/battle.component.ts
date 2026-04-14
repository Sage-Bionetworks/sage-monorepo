import { Component, inject, OnDestroy, signal } from '@angular/core';
import { BattleEvaluationOutcome } from '@sagebionetworks/bixarena/api-client';
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
  providers: [BattleStateService, BattleStreamService],
  templateUrl: './battle.component.html',
  styleUrl: './battle.component.scss',
})
export class BattleComponent implements OnDestroy {
  readonly state = inject(BattleStateService);
  readonly hoverSide = signal<BattleEvaluationOutcome | null>(null);

  onPromptSubmit(prompt: string): void {
    void this.state.submitPrompt(prompt);
  }

  onFollowUpSubmit(prompt: string): void {
    void this.state.submitFollowUp(prompt);
  }

  onVote(outcome: BattleEvaluationOutcome): void {
    void this.state.submitVote(outcome);
  }

  onNewBattle(): void {
    this.state.reset();
  }

  onSamePromptSubmit(): void {
    void this.state.newMatchup();
  }

  ngOnDestroy(): void {
    this.state.reset();
  }
}
