import { Component, Input } from '@angular/core';
import {
  Challenge,
  User,
  UserService,
} from '@sagebionetworks/challenge-registry/api-client-angular-deprecated';
import { MOCK_CHALLENGES } from '@sagebionetworks/challenge-registry/ui';
import { Paginator } from 'primeng/paginator';
import { BehaviorSubject, of, switchMap } from 'rxjs';
import { assign } from 'lodash';
// import { map, Observable } from 'rxjs';

@Component({
  selector: 'challenge-registry-user-profile-challenges',
  templateUrl: './user-profile-challenges.component.html',
  styleUrls: ['./user-profile-challenges.component.scss'],
})
export class UserProfileChallengesComponent {
  @Input() user!: User;
  challenges: Challenge[] = MOCK_CHALLENGES;
  limit = 10;
  offset = 0;
  challengeResultsCount = 0;

  private query: BehaviorSubject<any> = new BehaviorSubject<any>({
    offset: 0,
    limit: 10,
  });

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.query
      .pipe(
        switchMap((query: any) =>
          of({
            challenges: []
              .concat(...new Array(100).fill(MOCK_CHALLENGES))
              .slice(query.offset, query.offset + query.limit),
            totalResults: 100 * MOCK_CHALLENGES.length,
          })
        )
      )
      .subscribe((page) => {
        this.challenges = page.challenges;
        console.log(this.challenges);
        this.challengeResultsCount = page.totalResults;
      });
  }

  updateQuery(): void {
    const query = assign(this.query.getValue(), {
      offset: this.offset,
      limit: this.limit,
    });
    this.query.next(query);
  }

  onPageChange(event: Paginator) {
    this.offset = event.first;
    this.limit = event.rows;
    this.updateQuery();
  }
}
