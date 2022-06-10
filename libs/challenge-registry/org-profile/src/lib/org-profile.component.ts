import { Component, Inject, Input, OnInit } from '@angular/core';
import {
  Account,
  AccountService,
  ModelError as ApiClientError,
  Organization,
  OrganizationService,
} from '@sagebionetworks/api-angular';
import {
  catchError,
  filter,
  Observable,
  of,
  switchMap,
  throwError,
} from 'rxjs';
import {
  AppConfig,
  APP_CONFIG,
} from '@sagebionetworks/challenge-registry/config';
import { ActivatedRoute, Router } from '@angular/router';
import { isApiClientError } from '@sagebionetworks/challenge-registry/util';
import { OrgProfileDataServiceService } from './org-profile-data-service.service';

@Component({
  selector: 'challenge-registry-org-profile',
  templateUrl: './org-profile.component.html',
  styleUrls: ['./org-profile.component.scss'],
})
export class OrgProfileComponent implements OnInit {
  public appVersion: string;

  @Input() accountId!: string;
  org$!: Observable<Organization>;

  sections: any[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private accountService: AccountService,
    private orgService: OrganizationService,
    private orgProfileDataService: OrgProfileDataServiceService,
    @Inject(APP_CONFIG) private appConfig: AppConfig
  ) {
    this.appVersion = appConfig.appVersion;
  }

  ngOnInit(): void {
    const account$ = this.route.params.pipe(
      switchMap((params) => this.accountService.getAccount(params['login'])),
      catchError((err) => {
        const error = err.error as ApiClientError;
        if (isApiClientError(error)) {
          if (error.status === 404) {
            return of(undefined);
          }
        }
        return throwError(err);
      })
    );

    account$.subscribe((account) => {
      const pageTitle = account ? `${account.login}` : 'Page not found';
      console.log(pageTitle);
      // this.pageTitleService.setTitle(`${pageTitle} · ROCC`);
      // this.accountNotFound = !account;
    });

    this.org$ = account$.pipe(
      filter((account): account is Account => account !== undefined),
      switchMap((account) => this.orgService.getOrganization(account.id))
    );

    this.org$.subscribe((org) => {
      console.log(org);
      this.orgProfileDataService.setOrg(org);

      this.sections = [
        {
          label: 'Overview',
          path: '.',
        },
        {
          label: 'Challenges',
          path: `/org/${org.login}/challenges`,
        },
        {
          label: 'People',
          path: `/org/${org.login}/people`,
        },
        {
          label: 'Settings',
          path: `/org/${org.login}/settings`,
        },
      ];
    });
  }
}
