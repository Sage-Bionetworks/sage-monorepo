import { Component, input, output } from '@angular/core';
import { BattleEvaluationOutcome } from '@sagebionetworks/bixarena/api-client';
import { BattlePhase } from '../battle.types';

@Component({
  selector: 'bixarena-voting-bar',
  templateUrl: './voting-bar.component.html',
  styleUrl: './voting-bar.component.scss',
})
export class VotingBarComponent {
  readonly canVote = input(false);
  readonly canReuse = input(false);
  readonly promptUsesRemaining = input(0);
  readonly phase = input.required<BattlePhase>();

  readonly vote = output<BattleEvaluationOutcome>();
  readonly hoverSide = output<BattleEvaluationOutcome | null>();
  readonly newBattle = output<void>();
  readonly samePrompt = output<void>();

  voteModel1(): void {
    this.vote.emit(BattleEvaluationOutcome.Model1);
  }

  voteTie(): void {
    this.vote.emit(BattleEvaluationOutcome.Tie);
  }

  voteModel2(): void {
    this.vote.emit(BattleEvaluationOutcome.Model2);
  }
}
