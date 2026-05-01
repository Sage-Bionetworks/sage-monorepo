import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { TooltipModule } from 'primeng/tooltip';
import { BattleEvaluationOutcome } from '@sagebionetworks/bixarena/api-client';
import { BattleGateService } from '@sagebionetworks/bixarena/services';
import {
  BlueprintBgComponent,
  OnboardingModalComponent,
  PromptComposerComponent,
} from '@sagebionetworks/bixarena/ui';
import { ConfigService } from '@sagebionetworks/bixarena/config';
import { BattleStateService } from './services/battle.service';
import { BattleStreamService } from './services/battle-stream.service';
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
    OnboardingModalComponent,
    BlueprintBgComponent,
    TooltipModule,
  ],
  providers: [BattleStateService, BattleStreamService],
  templateUrl: './battle.component.html',
  styleUrl: './battle.component.scss',
})
export class BattleComponent implements OnInit, OnDestroy {
  readonly state = inject(BattleStateService);
  readonly gate = inject(BattleGateService);
  readonly hoverSide = signal<BattleEvaluationOutcome | null>(null);
  readonly termsUrl = inject(ConfigService).config.links.termsOfService;

  ngOnInit(): void {
    const pending = this.gate.consumePendingPrompt();
    if (pending) void this.gatedSubmit(pending.prompt, pending.examplePromptId);
  }

  onPromptSubmit(prompt: string): void {
    void this.gatedSubmit(prompt);
  }

  onExamplePromptSelect(event: { question: string; examplePromptId: string }): void {
    void this.gatedSubmit(event.question, event.examplePromptId);
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

  private async gatedSubmit(prompt: string, examplePromptId?: string | null): Promise<void> {
    const passed = await this.gate.checkOnboarding();
    if (passed) {
      void this.state.submitPrompt(prompt, examplePromptId);
    }
  }
}
