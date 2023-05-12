import { Component, OnInit } from '@angular/core';
import {
  Organization,
  OrganizationService,
  OrganizationSearchQuery,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { BehaviorSubject, switchMap } from 'rxjs';

@Component({
  selector: 'openchallenges-challenge-host-list',
  templateUrl: './challenge-host-list.component.html',
  styleUrls: ['./challenge-host-list.component.scss'],
})
export class ChallengeHostListComponent implements OnInit {
  private query: BehaviorSubject<OrganizationSearchQuery> =
    new BehaviorSubject<OrganizationSearchQuery>({});

  organizations: Organization[] = [];

  constructor(private organizationService: OrganizationService) {}

  ngOnInit() {
    const defaultQuery: OrganizationSearchQuery = {
      pageNumber: 0,
      pageSize: 4,
      searchTerms: '',
      sort: 'challenge_count',
    } as OrganizationSearchQuery;
    this.query.next(defaultQuery);
    this.query
      .pipe(
        switchMap((query) => this.organizationService.listOrganizations(query))
      )
      .subscribe((page) => {
        this.organizations = page.organizations;
      });
  }
}
