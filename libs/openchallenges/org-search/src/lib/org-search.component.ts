import { AfterContentInit, Component, OnDestroy, OnInit } from '@angular/core';
import {
  OrganizationService,
  OrganizationSearchQuery,
  ImageService,
  ImageQuery,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
import {
  Filter,
  FilterValue,
  OrganizationCard,
} from '@sagebionetworks/openchallenges/ui';
import { contributorRolesFilter } from './org-search-filters';
import { organizationSortFilterValues } from './org-search-filters-values';
import {
  BehaviorSubject,
  Subject,
  switchMap,
  tap,
  throwError,
  map,
  of,
  combineLatest,
} from 'rxjs';
import {
  catchError,
  debounceTime,
  distinctUntilChanged,
  takeUntil,
} from 'rxjs/operators';
import { assign } from 'lodash';
import { MatSnackBar } from '@angular/material/snack-bar';
import { forkJoinConcurrent } from '@sagebionetworks/openchallenges/util';

@Component({
  selector: 'openchallenges-org-search',
  templateUrl: './org-search.component.html',
  styleUrls: ['./org-search.component.scss'],
})
export class OrgSearchComponent implements OnInit, AfterContentInit, OnDestroy {
  public appVersion: string;

  private query: BehaviorSubject<OrganizationSearchQuery> =
    new BehaviorSubject<OrganizationSearchQuery>({});

  // set a default behaviorSubject to trigger searchTearm's changes
  private searchTerms: BehaviorSubject<string> = new BehaviorSubject<string>(
    ''
  );

  private destroy = new Subject<void>();
  searchTermValue = '';

  // organizations: Organization[] = [];
  organizationCards: OrganizationCard[] = [];
  totalOrgCount = 0;

  pageNumber = 0;
  pageSize = 20;
  searchResultsCount = 0;

  // define filters
  checkboxFilters: Filter[] = [contributorRolesFilter];
  sortFilters: FilterValue[] = organizationSortFilterValues;
  sortedBy!: string;

  constructor(
    private organizationService: OrganizationService,
    private imageService: ImageService,
    private readonly configService: ConfigService,
    private _snackBar: MatSnackBar
  ) {
    this.appVersion = this.configService.config.appVersion;
  }

  ngOnInit() {
    // set default selection
    this.sortedBy = organizationSortFilterValues[0].value as string;

    // update the total number of challenges in database with empty query
    this.organizationService
      .listOrganizations({})
      .subscribe((page) => (this.totalOrgCount = page.totalElements));

    const defaultQuery: OrganizationSearchQuery = {
      pageNumber: this.pageNumber,
      pageSize: this.pageSize,
      searchTerms: this.searchTermValue,
      sort: this.sortedBy,
    } as OrganizationSearchQuery;
    this.query.next(defaultQuery);
  }

  ngAfterContentInit(): void {
    this.searchTerms
      .pipe(debounceTime(400), distinctUntilChanged(), takeUntil(this.destroy))
      .subscribe((search) => {
        const newQuery = assign(this.query.getValue(), {
          searchTerms: search,
        });
        this.query.next(newQuery);
      });

    this.query
      .pipe(
        tap((query) => console.log('Query: ', query)),
        switchMap((query) => {
          const page$ = this.organizationService.listOrganizations(query);

          const avatarUrls$ = page$.pipe(
            map((page) => page.organizations),
            switchMap((orgs) =>
              forkJoinConcurrent(
                orgs.map((org) => {
                  if (org.avatarKey && org.avatarKey.length > 0) {
                    return this.imageService.getImage({
                      objectKey: org.avatarKey,
                    } as ImageQuery);
                  } else {
                    return of(undefined);
                  }
                }),
                10
              )
            )
          );

          return combineLatest({ page: page$, avatarUrls: avatarUrls$ });
        }),
        tap(({ page, avatarUrls }) => {
          console.log('List of orgs: ', page.organizations);
          console.log('List of avatar URLs:', avatarUrls);
        }),
        catchError((err) => {
          if (err.message) {
            this.openSnackBar(
              'Unable to get the organizations. Please refresh the page and try again.'
            );
          }
          return throwError(() => new Error(err.message));
        })
      )
      .subscribe((page) => {
        // update organizations and total number of results
        // this.searchResultsCount = page.totalElements;
        // this.organizations = page.organizations;
      });
  }

  ngOnDestroy(): void {
    this.destroy.next();
    this.destroy.complete();
  }

  onSearchChange(): void {
    // update searchTerms to trigger the query' searchTerm
    this.searchTerms.next(this.searchTermValue);
  }

  onCheckboxChange(selected: string[], queryName: string): void {
    const newQuery = assign(this.query.getValue(), {
      [queryName]: selected,
    });
    this.query.next(newQuery);
  }

  onSortChange(): void {
    const newQuery = assign(this.query.getValue(), {
      sort: this.sortedBy,
    });
    this.query.next(newQuery);
  }

  onPageChange(event: any) {
    const newQuery = assign(this.query.getValue(), {
      pageNumber: event.page,
      pageSize: event.rows,
    });
    this.query.next(newQuery);
  }

  openSnackBar(message: string) {
    this._snackBar.open(message, undefined, {
      duration: 30000,
    });
  }
}
