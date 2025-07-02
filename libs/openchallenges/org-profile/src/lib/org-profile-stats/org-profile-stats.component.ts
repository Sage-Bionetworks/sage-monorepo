import { Component, inject, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  Organization,
  OrganizationService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { catchError, Observable, switchMap, throwError } from 'rxjs';
import { HttpStatusRedirect, handleHttpError } from '@sagebionetworks/openchallenges/util';
import { NgPlural, NgPluralCase, AsyncPipe } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'openchallenges-org-profile-stats',
  imports: [NgPlural, NgPluralCase, AsyncPipe, MatIconModule],
  templateUrl: './org-profile-stats.component.html',
  styleUrls: ['./org-profile-stats.component.scss'],
})
export class OrgProfileStatsComponent implements OnInit {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly organizationService = inject(OrganizationService);

  @Input({ required: true }) loggedIn = false;
  organization$!: Observable<Organization>;
  mockMembers!: number;

  ngOnInit(): void {
    this.mockMembers = 3;

    this.organization$ = this.activatedRoute.params.pipe(
      switchMap((params) => this.organizationService.getOrganization(params['orgLogin'])),
      catchError((err) => {
        const error = handleHttpError(err, this.router, {
          404: '/not-found',
          400: '/org',
        } as HttpStatusRedirect);
        return throwError(() => error);
      }),
    );
  }

  shorthand(n: number | undefined) {
    if (n) {
      return Intl.NumberFormat('en-US', {
        maximumFractionDigits: 1,
      }).format(n);
    } else {
      return 0;
    }
  }
}
