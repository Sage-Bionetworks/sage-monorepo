import { Injectable, inject } from '@angular/core';
import { GoogleTagManagerService } from 'angular-google-tag-manager';
import { BattleEvaluationOutcome } from '@sagebionetworks/bixarena/api-client';

export type LoginEntryPoint =
  | 'nav_login_button'
  | 'home_composer'
  | 'home_trending_card'
  | 'home_ask_own_button'
  | 'nav_battle_link';

export type BattleEntryPoint =
  | 'home_composer'
  | 'home_trending_card'
  | 'battle_composer'
  | 'battle_example_card'
  | 'new_matchup_button';

const OUTCOME_MAP: Record<BattleEvaluationOutcome, string> = {
  model1: 'model_a',
  model2: 'model_b',
  tie: 'tie',
};

const LOGIN_ENTRY_POINT_KEY = 'bixarena.loginEntryPoint';

@Injectable({ providedIn: 'root' })
export class AnalyticsService {
  private readonly gtm = inject(GoogleTagManagerService, { optional: true });

  private push(event: string, params?: Record<string, unknown>): void {
    this.gtm?.pushTag({ event, ...params }).catch(() => undefined);
  }

  private saveLoginEntryPoint(entryPoint: LoginEntryPoint): void {
    try {
      sessionStorage.setItem(LOGIN_ENTRY_POINT_KEY, entryPoint);
    } catch {
      /* private mode / SSR */
    }
  }

  private consumeLoginEntryPoint(): string | null {
    try {
      const value = sessionStorage.getItem(LOGIN_ENTRY_POINT_KEY);
      sessionStorage.removeItem(LOGIN_ENTRY_POINT_KEY);
      return value;
    } catch {
      return null;
    }
  }

  trackLoginInitiated(entryPoint: LoginEntryPoint): void {
    this.saveLoginEntryPoint(entryPoint);
    this.push('login_initiated', { entry_point: entryPoint });
  }

  trackLogin(): void {
    const entryPoint = this.consumeLoginEntryPoint();
    this.push('login', {
      method: 'synapse_oauth',
      ...(entryPoint ? { entry_point: entryPoint } : {}),
    });
  }

  trackLogout(): void {
    this.push('logout');
  }

  trackOnboardingViewed(): void {
    this.push('onboarding_viewed');
  }

  trackOnboardingCompleted(): void {
    this.push('onboarding_completed');
  }

  trackOnboardingDismissed(stepIndex: number): void {
    this.push('onboarding_dismissed', { step_index: stepIndex });
  }

  trackOnboardingReopened(): void {
    this.push('onboarding_reopened');
  }

  trackBattleStarted(
    battleId: string,
    entryPoint: BattleEntryPoint,
    useExamplePrompt: boolean,
  ): void {
    this.push('battle_started', {
      battle_id: battleId,
      entry_point: entryPoint,
      use_example_prompt: useExamplePrompt,
    });
  }

  trackVoteSubmitted(
    battleId: string,
    outcome: BattleEvaluationOutcome,
    roundNumber: number,
  ): void {
    this.push('vote_submitted', {
      battle_id: battleId,
      vote_choice: OUTCOME_MAP[outcome],
      round_number: roundNumber,
    });
  }

  trackFollowupQuestionSubmitted(battleId: string, roundNumber: number): void {
    this.push('followup_question_submitted', {
      battle_id: battleId,
      round_number: roundNumber,
    });
  }

  trackLeaderboardViewed(
    activeCategory: string,
    modelCount: number,
    entryPoint?: 'home_section',
  ): void {
    this.push('leaderboard_viewed', {
      active_category: activeCategory,
      model_count: modelCount,
      ...(entryPoint ? { entry_point: entryPoint } : {}),
    });
  }

  trackLeaderboardFilterChanged(filterType: 'category' | 'view', filterValue: string): void {
    this.push('leaderboard_filter_changed', {
      filter_type: filterType,
      filter_value: filterValue,
    });
  }
}
