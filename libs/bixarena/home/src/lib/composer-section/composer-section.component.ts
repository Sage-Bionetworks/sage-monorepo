import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { ConfigService } from '@sagebionetworks/bixarena/config';
import { AuthService, BattleGateService } from '@sagebionetworks/bixarena/services';
import { PromptComposerComponent } from '@sagebionetworks/bixarena/ui';

@Component({
  selector: 'bixarena-composer-section',
  imports: [PromptComposerComponent],
  templateUrl: './composer-section.component.html',
  styleUrl: './composer-section.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ComposerSectionComponent {
  private readonly auth = inject(AuthService);
  private readonly gate = inject(BattleGateService);
  private readonly router = inject(Router);

  readonly promptLengthLimit = inject(ConfigService).config.battle.promptLengthLimit;

  onPromptSubmit(prompt: string): void {
    this.gate.savePendingPrompt(prompt);
    if (this.auth.isAuthenticated()) {
      void this.router.navigate(['/battle']);
    } else {
      this.gate.showLoginModal.set(true);
    }
  }
}
