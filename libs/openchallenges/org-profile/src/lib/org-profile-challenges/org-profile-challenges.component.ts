import { Component, inject, Input, OnInit } from '@angular/core';
import {
  Challenge,
  ChallengeSearchQuery,
  ChallengeService,
  Organization,
} from '@sagebionetworks/openchallenges/api-client';
import { BehaviorSubject, switchMap } from 'rxjs';
import { assign } from 'lodash';

import { ChallengeCardComponent, PaginatorComponent } from '@sagebionetworks/openchallenges/ui';

@Component({
  selector: 'openchallenges-org-profile-challenges',
  imports: [PaginatorComponent, ChallengeCardComponent],
  templateUrl: './org-profile-challenges.component.html',
  styleUrls: ['./org-profile-challenges.component.scss'],
})
export class OrgProfileChallengesComponent implements OnInit {
  private readonly challengeService = inject(ChallengeService);

  @Input({ required: true }) organization!: Organization;
  challenges: Challenge[] = [];
  // default pagination
  pageNumber = 0;
  pageSize = 12;
  pageSizeOptions!: number[];
  totalChallengesCount = 0;

  private query: BehaviorSubject<ChallengeSearchQuery> = new BehaviorSubject<ChallengeSearchQuery>(
    {},
  );

  ngOnInit(): void {
    this.pageSizeOptions = this.getPageSizeOptions(this.pageSize);

    const defaultQuery: ChallengeSearchQuery = {
      pageNumber: this.pageNumber,
      pageSize: this.pageSize,
      organizations: [this.organization.id],
    };
    this.query.next(defaultQuery);

    this.query
      .pipe(switchMap((query) => this.challengeService.listChallenges(query)))
      .subscribe((page) => {
        this.totalChallengesCount = page.totalElements;
        this.challenges = page.challenges;
      });
  }

  onPageChange(event: any) {
    const newQuery = assign(this.query.getValue(), {
      pageNumber: event.page,
      pageSize: event.rows,
    });
    this.query.next(newQuery);
  }

  getPageSizeOptions(pageSize: number): number[] {
    return [pageSize, pageSize * 2, pageSize * 3];
  }
}
