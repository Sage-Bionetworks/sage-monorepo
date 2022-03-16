import { Component, Inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {
  Account,
  AccountService,
  ModelError as ApiClientError,
  Organization,
  User,
  UserService,
} from '@challenge-registry/api-angular';
import { AppConfig, APP_CONFIG } from '@challenge-registry/web/config';
import { isApiClientError } from '@challenge-registry/web/util';
import {
  catchError,
  filter,
  map,
  Observable,
  of,
  Subscription,
  switchMap,
  throwError,
} from 'rxjs';

@Component({
  selector: 'challenge-registry-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss'],
})
export class UserComponent implements OnInit {
  public appVersion: string;
  account$!: Observable<Account | undefined>;
  user$!: Observable<User>;
  orgs: Organization[] = [];
  private subscriptions: Subscription[] = [];

  constructor(
    private route: ActivatedRoute,
    private accountService: AccountService,
    private userService: UserService,
    @Inject(APP_CONFIG) private appConfig: AppConfig
  ) {
    this.appVersion = appConfig.appVersion;
  }

  ngOnInit(): void {
    this.account$ = this.route.params.pipe(
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

    this.account$.subscribe((account) => {
      const pageTitle = account ? `${account.login}` : 'Page not found';
      console.log(pageTitle);
      // this.pageTitleService.setTitle(`${pageTitle} · ROCC`);
      // this.accountNotFound = !account;
    });

    this.user$ = this.account$.pipe(
      filter((account): account is Account => account !== undefined),
      switchMap((account) => this.userService.getUser(account.id))
    );

    const orgs$ = this.account$.pipe(
      filter((account): account is Account => account !== undefined),
      switchMap((account) =>
        this.userService.listUserOrganizations(account.id)
      ),
      map((page) => page.organizations)
    );

    const orgsSub = orgs$.subscribe((orgs) => (this.orgs = orgs));
    this.subscriptions.push(orgsSub);
  }
}
