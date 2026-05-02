import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { TooltipModule } from 'primeng/tooltip';
import { BattleEvaluationOutcome } from '@sagebionetworks/bixarena/api-client';
import {
  BattleGateService,
  OnboardingService,
  PendingPrompt,
} from '@sagebionetworks/bixarena/services';
import {
  BlueprintBgComponent,
  HeroComponent,
  OnboardingFabComponent,
  OnboardingModalComponent,
  PromptComposerComponent,
} from '@sagebionetworks/bixarena/ui';
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
    OnboardingFabComponent,
    BlueprintBgComponent,
    HeroComponent,
    TooltipModule,
  ],
  providers: [BattleStateService, BattleStreamService],
  templateUrl: './battle.component.html',
  styleUrl: './battle.component.scss',
})
export class BattleComponent implements OnInit, OnDestroy {
  readonly state = inject(BattleStateService);
  readonly gate = inject(BattleGateService);
  private readonly onboardingService = inject(OnboardingService);
  readonly hoverSide = signal<BattleEvaluationOutcome | null>(null);

  readonly showOnboardingModal = signal(false);
  // Held while the onboarding modal is open so streaming doesn't start behind
  // the explainer. Submitted on modal close.
  private pendingPrompt: PendingPrompt | null = null;

  ngOnInit(): void {
    const pending = this.gate.consumePendingPrompt();
    const firstTime = !this.onboardingService.hasSeen();

    if (firstTime) {
      this.pendingPrompt = pending;
      this.showOnboardingModal.set(true);
    } else if (pending) {
      void this.state.submitPrompt(pending.prompt, pending.examplePromptId);
    }
  }

  onPromptSubmit(prompt: string): void {
    void this.state.submitPrompt(prompt);
  }

  onExamplePromptSelect(event: { question: string; examplePromptId: string }): void {
    void this.state.submitPrompt(event.question, event.examplePromptId);
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

  onOnboardingClose(): void {
    this.onboardingService.markSeen();
    this.showOnboardingModal.set(false);
    const pending = this.pendingPrompt;
    this.pendingPrompt = null;
    if (pending) {
      void this.state.submitPrompt(pending.prompt, pending.examplePromptId);
    }
  }

  ngOnDestroy(): void {
    this.state.reset();
  }
}
