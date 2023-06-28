import { AfterContentInit, Component, OnDestroy, OnInit } from '@angular/core';
import {
  OrganizationService,
  OrganizationSearchQuery,
  ImageService,
  ImageQuery,
  Image,
  ImageHeight,
  ImageAspectRatio,
  Organization,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
import {
  FilterValue,
  OrganizationCard,
} from '@sagebionetworks/openchallenges/ui';
import { challengeContributionRolesFilter } from './org-search-filters';
import { organizationSortFilterValues } from './org-search-filters-values';
import {
  BehaviorSubject,
  Subject,
  switchMap,
  throwError,
  map,
  of,
  Observable,
  forkJoin,
  tap,
} from 'rxjs';
import {
  catchError,
  debounceTime,
  distinctUntilChanged,
  shareReplay,
  takeUntil,
} from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';
import { forkJoinConcurrent } from '@sagebionetworks/openchallenges/util';
import { ActivatedRoute, Router } from '@angular/router';

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

  searchResultsCount = 0;
  totalOrgCount = 0;
  organizationCards: OrganizationCard[] = [];

  searchedTerms!: string;
  selectedPageNumber!: number;
  selectedPageSize!: number;
  sortedBy!: string;

  // set default values
  defaultSortedBy = 'relevance';
  defaultPageNumber = 0;
  defaultPageSize = 24;

  // define filters
  sortFilters: FilterValue[] = organizationSortFilterValues;

  // checkbox filters
  contributionRolesFilter = challengeContributionRolesFilter;

  // define selected filter values
  selectedContributionRoles!: string[];

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private organizationService: OrganizationService,
    private imageService: ImageService,
    private readonly configService: ConfigService,
    private _snackBar: MatSnackBar
  ) {
    this.appVersion = this.configService.config.appVersion;
  }

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe((params) => {
      // update selected filter values based on params in url
      this.selectedContributionRoles = this.splitParam(
        params['contributionRoles']
      );

      this.searchedTerms = params['searchTerms'];
      this.selectedPageNumber = +params['pageNumber'];
      this.selectedPageSize = +params['pageSize'];
      this.sortedBy = params['sort'];

      const defaultQuery = {
        pageNumber: this.selectedPageNumber || this.defaultPageNumber,
        pageSize: this.selectedPageSize || this.defaultPageSize,
        sort: this.sortedBy || this.defaultSortedBy,
        searchTerms: this.searchedTerms,
        contributionRoles: this.selectedContributionRoles,
      } as OrganizationSearchQuery;

      this.query.next(defaultQuery);
    });

    // update the total number of orgs in database
    this.organizationService
      .listOrganizations({})
      .subscribe((page) => (this.totalOrgCount = page.totalElements));
  }

  ngAfterContentInit(): void {
    this.searchTerms
      .pipe(debounceTime(400), distinctUntilChanged(), takeUntil(this.destroy))
      .subscribe((searched) => {
        const searchedTerms = searched === '' ? undefined : searched;
        this.router.navigate([], {
          queryParamsHandling: 'merge',
          queryParams: { searchTerms: searchedTerms },
        });
      });

    const orgPage$ = this.query.pipe(
      tap((query) => console.log('List organization query: ', query)),
      switchMap((query) => this.organizationService.listOrganizations(query)),
      tap((page) => console.log('List of organizations: ', page.organizations)),
      catchError((err) => {
        if (err.message) {
          this.openSnackBar(
            'Unable to get the organizations. Please refresh the page and try again.'
          );
        }
        return throwError(() => new Error(err.message));
      }),
      shareReplay(1)
    );

    const organizationCards$ = orgPage$.pipe(
      map((page) => page.organizations),
      switchMap((orgs) =>
        forkJoin({
          orgs: of(orgs),
          avatarUrls: forkJoinConcurrent(
            orgs.map((org) => this.getOrganizationAvatarUrl(org)),
            Infinity
          ) as unknown as Observable<(Image | undefined)[]>,
        })
      ),
      switchMap(({ orgs, avatarUrls }) =>
        of(
          orgs.map((org, index) =>
            this.getOrganizationCard(org, avatarUrls[index])
          )
        )
      )
    );

    orgPage$.subscribe((page) => {
      this.searchResultsCount = page.totalElements;
    });

    organizationCards$.subscribe((organizationCards) => {
      this.organizationCards = organizationCards;
    });
  }

  ngOnDestroy(): void {
    this.destroy.next();
    this.destroy.complete();
  }

  splitParam(activeParam: string | undefined, by = ','): any[] {
    return activeParam ? activeParam.split(by) : [];
  }

  collapseParam(selectedParam: any, by = ','): string | undefined {
    return selectedParam.length === 0
      ? undefined
      : this.splitParam(selectedParam.toString()).join(by);
  }

  onSearchChange(): void {
    // update searchTerms to trigger the query' searchTerm
    this.searchTerms.next(this.searchTermValue);
  }

  onContributionRolesChange(selected: string[]): void {
    this.router.navigate([], {
      queryParamsHandling: 'merge',
      queryParams: {
        contributionRoles: this.collapseParam(selected),
      },
    });
  }

  onSortChange(): void {
    this.router.navigate([], {
      queryParamsHandling: 'merge',
      queryParams: {
        sort: this.sortedBy,
      },
    });
  }

  onPageChange(event: any) {
    this.router.navigate([], {
      queryParamsHandling: 'merge',
      queryParams: {
        pageNumber: event.page,
        pageSize: event.rows,
      },
    });
  }

  openSnackBar(message: string) {
    this._snackBar.open(message, undefined, {
      duration: 30000,
    });
  }

  private getOrganizationAvatarUrl(
    org: Organization
  ): Observable<Image | undefined> {
    if (org.avatarKey && org.avatarKey.length > 0) {
      return this.imageService.getImage({
        objectKey: org.avatarKey,
        height: ImageHeight._140px,
        aspectRatio: ImageAspectRatio._11,
      } as ImageQuery);
    } else {
      return of(undefined);
    }
  }

  private getOrganizationCard(
    org: Organization,
    avatarUrl: Image | undefined
  ): OrganizationCard {
    return {
      acronym: org.acronym,
      avatarUrl: avatarUrl?.url,
      challengeCount: org.challengeCount,
      login: org.login,
      name: org.name,
    } as OrganizationCard;
  }
}
