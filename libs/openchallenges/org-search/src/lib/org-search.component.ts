import {
  AfterContentInit,
  Component,
  OnDestroy,
  OnInit,
  Renderer2,
} from '@angular/core';
import {
  OrganizationService,
  OrganizationSearchQuery,
  ImageService,
  ImageQuery,
  Image,
  ImageHeight,
  ImageAspectRatio,
  Organization,
  OrganizationSort,
  ChallengeContributionRole,
  OrganizationCategory,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
import {
  CheckboxFilterComponent,
  Filter,
  FilterValue,
  FooterComponent,
  OrganizationCard,
  OrganizationCardComponent,
  PaginatorComponent,
} from '@sagebionetworks/openchallenges/ui';
import {
  challengeContributionRolesFilterPanel,
  organizationCategoriesFilterPanel,
} from './org-search-filter-panels';
import { organizationSortFilter } from './org-search-filters';
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
  iif,
} from 'rxjs';
import {
  catchError,
  debounceTime,
  distinctUntilChanged,
  shareReplay,
  takeUntil,
} from 'rxjs/operators';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { forkJoinConcurrent } from '@sagebionetworks/openchallenges/util';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule, Location } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { DividerModule } from 'primeng/divider';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { PanelModule } from 'primeng/panel';
import { RadioButtonModule } from 'primeng/radiobutton';
import { SeoService } from '@sagebionetworks/shared/util';
import { getSeoData } from './org-search-seo-data';
import { HttpParams } from '@angular/common/http';
import { assign } from 'lodash';

@Component({
  selector: 'openchallenges-org-search',
  standalone: true,
  imports: [
    CommonModule,
    DividerModule,
    DropdownModule,
    InputTextModule,
    MatIconModule,
    MatSnackBarModule,
    RouterModule,
    FormsModule,
    PanelModule,
    RadioButtonModule,
    ReactiveFormsModule,
    FooterComponent,
    PaginatorComponent,
    OrganizationCardComponent,
    CheckboxFilterComponent,
  ],
  templateUrl: './org-search.component.html',
  styleUrls: ['./org-search.component.scss'],
})
export class OrgSearchComponent implements OnInit, AfterContentInit, OnDestroy {
  public appVersion: string;
  public dataUpdatedOn: string;
  public privacyPolicyUrl: string;
  public termsOfUseUrl: string;
  public apiDocsUrl: string;

  private query: BehaviorSubject<OrganizationSearchQuery> =
    new BehaviorSubject<OrganizationSearchQuery>({});

  // set a default behaviorSubject to trigger searchTearm's changes
  private searchTerms: BehaviorSubject<string> = new BehaviorSubject<string>(
    ''
  );

  private destroy = new Subject<void>();

  searchResultsCount = 0;
  totalOrgCount = 0;
  organizationCards: OrganizationCard[] = [];

  searchedTerms!: string;
  selectedPageNumber!: number;
  selectedPageSize!: number;
  sortedBy!: OrganizationSort;

  // set default values
  defaultSortedBy: OrganizationSort = 'challenge_count';
  defaultPageNumber = 0;
  defaultPageSize = 24;

  // define filters
  sortFilters: Filter[] = organizationSortFilter;

  // checkbox filters
  contributionRolesFilter = challengeContributionRolesFilterPanel;
  categoriesFilter = organizationCategoriesFilterPanel;

  // define selected filter values
  selectedContributionRoles!: ChallengeContributionRole[];
  selectedCategories!: OrganizationCategory[];

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private organizationService: OrganizationService,
    private imageService: ImageService,
    private readonly configService: ConfigService,
    private _snackBar: MatSnackBar,
    private seoService: SeoService,
    private renderer2: Renderer2,
    private _location: Location
  ) {
    this.appVersion = this.configService.config.appVersion;
    this.dataUpdatedOn = this.configService.config.dataUpdatedOn;
    this.privacyPolicyUrl = this.configService.config.privacyPolicyUrl;
    this.termsOfUseUrl = this.configService.config.termsOfUseUrl;
    this.apiDocsUrl = this.configService.config.apiDocsUrl;
    this.seoService.setData(getSeoData(), this.renderer2);
  }

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe((params) => {
      // update selected filter values based on params in url
      this.selectedContributionRoles = this.splitParam(
        params['contributionRoles']
      );
      this.selectedCategories = this.splitParam(params['categories']);
      this.searchedTerms = params['searchTerms'];
      this.selectedPageNumber = +params['pageNumber'] || this.defaultPageNumber;
      this.selectedPageSize = +params['pageSize'] || this.defaultPageSize;
      this.sortedBy = params['sort'] || this.defaultSortedBy;

      const defaultQuery: OrganizationSearchQuery = {
        pageNumber: this.selectedPageNumber,
        pageSize: this.selectedPageSize,
        sort: this.sortedBy,
        searchTerms: this.searchedTerms,
        challengeContributionRoles: this.selectedContributionRoles,
        categories: this.selectedCategories,
      };

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
        this.onParamChange({ searchTerms: searched });
      });

    const orgPage$ = this.query.pipe(
      tap((query) => console.log('List organization query: ', query)),
      switchMap((query: OrganizationSearchQuery) =>
        this.organizationService.listOrganizations(query)
      ),
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
          ),
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

  onSearchChange(): void {
    // update searchTerms to trigger the query' searchTerm
    this.searchTerms.next(this.searchedTerms);
  }

  private getOrganizationAvatarUrl(org: Organization): Observable<Image> {
    return iif(
      () => !!org.avatarKey,
      this.imageService.getImage({
        objectKey: org.avatarKey,
        height: ImageHeight._140px,
        aspectRatio: ImageAspectRatio._11,
      } as ImageQuery),
      of({ url: '' })
    ).pipe(
      catchError(() => {
        console.error(
          'Unable to get the image url. Please check the logs of the image service.'
        );
        return of({ url: '' });
      })
    );
  }

  private getOrganizationCard(
    org: Organization,
    avatarUrl: Image
  ): OrganizationCard {
    return {
      acronym: org.acronym,
      avatarUrl: avatarUrl?.url,
      challengeCount: org.challengeCount,
      login: org.login,
      name: org.name,
    } as OrganizationCard;
  }

  onParamChange(filteredQuery: any): void {
    // reset pagination settings when filters change
    if (!filteredQuery.pageNumber && !filteredQuery.pageSize) {
      filteredQuery.pageNumber = this.defaultPageNumber;
      filteredQuery.pageSize = this.defaultPageSize;
    }
    // update params of URL
    const currentParams = new HttpParams({
      fromString: this._location.path().split('?')[1] ?? '',
    });
    const params = Object.entries(filteredQuery)
      .map(([key, value]) => [key, this.collapseParam(value as FilterValue)])
      .reduce(
        // update with new param, or delete the param if empty string
        (params, [key, value]) =>
          value !== '' ? params.set(key, value) : params.delete(key),
        currentParams
      );
    this._location.replaceState(location.pathname, params.toString());

    // update query to trigger API call
    const newQuery = assign(this.query.getValue(), filteredQuery);
    this.query.next(newQuery);
  }

  splitParam(activeParam: string | undefined, by = ','): any[] {
    return activeParam ? activeParam.split(by) : [];
  }

  collapseParam(selectedParam: FilterValue | FilterValue[], by = ','): string {
    return Array.isArray(selectedParam)
      ? selectedParam.map((item) => item?.toString()).join(by)
      : (selectedParam as string) ?? '';
  }

  openSnackBar(message: string) {
    this._snackBar.open(message, undefined, {
      duration: 30000,
    });
  }
}
