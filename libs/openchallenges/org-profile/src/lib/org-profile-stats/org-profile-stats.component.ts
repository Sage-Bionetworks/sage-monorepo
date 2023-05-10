import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  Organization,
  OrganizationService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { catchError, Observable, switchMap, throwError } from 'rxjs';
import {
  HttpStatusRedirect,
  handleHttpError,
} from '@sagebionetworks/openchallenges/util';

@Component({
  selector: 'openchallenges-org-profile-stats',
  templateUrl: './org-profile-stats.component.html',
  styleUrls: ['./org-profile-stats.component.scss'],
})
export class OrgProfileStatsComponent implements OnInit {
  @Input() loggedIn = false;
  organization$!: Observable<Organization>;
  mockViews!: number;
  mockStargazers!: number;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private organizationService: OrganizationService
  ) {}

  ngOnInit(): void {
    this.mockViews = 2;
    this.mockStargazers = 9_999;

    this.organization$ = this.activatedRoute.params.pipe(
      switchMap((params) =>
        this.organizationService.getOrganization(params['orgLogin'])
      ),
      catchError((err) => {
        const error = handleHttpError(err, this.router, {
          404: '/not-found',
          400: '/org',
        } as HttpStatusRedirect);
        return throwError(() => error);
      })
    );
  }

  shorthand(n: number) {
    return Intl.NumberFormat('en-US', {
      notation: 'compact',
      maximumFractionDigits: 1,
    }).format(n);
  }
}
