import { Component, Inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {
  Account,
  AccountService,
  ModelError as ApiClientError,
} from '@challenge-registry/api-angular';
import { AppConfig, APP_CONFIG } from '@challenge-registry/web/config';
import { isApiClientError } from '@challenge-registry/web/util';
import { catchError, Observable, of, switchMap, throwError } from 'rxjs';

@Component({
  selector: 'challenge-registry-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss'],
})
export class UserComponent implements OnInit {
  public appVersion: string;
  account$!: Observable<Account | undefined>;

  constructor(
    private route: ActivatedRoute,
    private accountService: AccountService,
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
  }
}
