import {
  AfterContentInit,
  Component,
  inject,
  Input,
  OnDestroy,
  OnInit,
  Renderer2,
  ViewChild,
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
  ChallengeParticipationRole,
  OrganizationCategory,
} from '@sagebionetworks/openchallenges/api-client';
import { ConfigService } from '@sagebionetworks/openchallenges/web/angular/config';
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
  challengeParticipationRolesFilterPanel,
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
import { RouterModule } from '@angular/router';
import { Location } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { DividerModule } from 'primeng/divider';
import { DropdownModule } from 'primeng/dropdown';
import { InputGroupModule } from 'primeng/inputgroup';
import { InputGroupAddonModule } from 'primeng/inputgroupaddon';
import { InputTextModule } from 'primeng/inputtext';
import { PanelModule } from 'primeng/panel';
import { RadioButtonModule } from 'primeng/radiobutton';
import { SeoService } from '@sagebionetworks/shared/util';
import { getSeoData } from './org-search-seo-data';
import { HttpParams } from '@angular/common/http';
import { assign } from 'lodash';

@Component({
  selector: 'openchallenges-org-search',
  imports: [
    DividerModule,
    DropdownModule,
    InputGroupModule,
    InputGroupAddonModule,
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
  private readonly organizationService = inject(OrganizationService);
  private readonly imageService = inject(ImageService);
  private readonly configService = inject(ConfigService);
  private readonly seoService = inject(SeoService);
  private readonly renderer2 = inject(Renderer2);
  private readonly _snackBar = inject(MatSnackBar);
  private readonly _location = inject(Location);

  public appVersion: string;
  public dataUpdatedOn: string;
  public privacyPolicyUrl: string;
  public termsOfUseUrl: string;
  public apiDocsUrl: string;

  private readonly query: BehaviorSubject<OrganizationSearchQuery> =
    new BehaviorSubject<OrganizationSearchQuery>({});

  // set a default behaviorSubject to trigger searchTearm's changes
  private readonly orgSearchTerms: BehaviorSubject<string> = new BehaviorSubject<string>('');

  private readonly destroy = new Subject<void>();

  // use @Input to retrieve the route param
  @Input({ required: false }) categories!: OrganizationCategory[];
  @Input({ required: false }) challengeParticipationRoles!: ChallengeParticipationRole[];
  @Input({ required: false }) pageNumber!: number;
  @Input({ required: false }) searchTerms!: string;
  @Input({ required: false }) sort!: OrganizationSort;

  searchResultsCount!: number;
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
  @ViewChild('paginator', { static: false }) paginator!: PaginatorComponent;

  // define filters
  sortFilters: Filter[] = organizationSortFilter;

  // checkbox filters
  challengeParticipationRolesFilter = challengeParticipationRolesFilterPanel;
  categoriesFilter = organizationCategoriesFilterPanel;

  // define selected filter values
  selectedChallengeParticipationRoles!: ChallengeParticipationRole[];
  selectedCategories!: OrganizationCategory[];

  constructor() {
    this.appVersion = this.configService.config.app.version;
    this.dataUpdatedOn = this.configService.config.data.updatedOn;
    this.privacyPolicyUrl = this.configService.config.urls.privacyPolicy;
    this.termsOfUseUrl = this.configService.config.urls.termsOfUse;
    this.apiDocsUrl = this.configService.config.api.docs.url;
    this.seoService.setData(getSeoData(), this.renderer2);
  }

  ngOnInit() {
    this.updateSelectedValues();
    this.updateQuery();

    // update the total number of orgs in database
    this.organizationService
      .listOrganizations({})
      .subscribe((page) => (this.totalOrgCount = page.totalElements));
  }

  ngAfterContentInit(): void {
    this.orgSearchTerms
      .pipe(debounceTime(400), distinctUntilChanged(), takeUntil(this.destroy))
      .subscribe((searched) => {
        this.onParamChange({ searchTerms: searched });
      });

    const orgPage$ = this.query.pipe(
      tap((query) => console.log('List organization query: ', query)),
      switchMap((query: OrganizationSearchQuery) =>
        this.organizationService.listOrganizations(query),
      ),
      tap((page) => console.log('List of organizations: ', page.organizations)),
      catchError((err) => {
        if (err.message) {
          this.openSnackBar(
            'Unable to get the organizations. Please refresh the page and try again.',
          );
        }
        return throwError(() => new Error(err.message));
      }),
      shareReplay(1),
    );

    const organizationCards$ = orgPage$.pipe(
      map((page) => page.organizations),
      switchMap((orgs) =>
        forkJoin({
          orgs: of(orgs),
          avatarUrls: forkJoinConcurrent(
            orgs.map((org) => this.getOrganizationAvatarUrl(org)),
            Infinity,
          ),
        }),
      ),
      switchMap(({ orgs, avatarUrls }) =>
        of(orgs.map((org, index) => this.getOrganizationCard(org, avatarUrls[index]))),
      ),
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
    this.orgSearchTerms.next(this.searchedTerms);
  }

  private updateSelectedValues() {
    // update selected filter values based on params in url
    this.selectedChallengeParticipationRoles = this.splitParam(this.challengeParticipationRoles);
    this.selectedCategories = this.splitParam(this.categories);
    this.searchedTerms = this.searchTerms || '';
    this.selectedPageNumber = +this.pageNumber || this.defaultPageNumber;
    this.selectedPageSize = this.defaultPageSize; // no available pageSize options for users
    this.sortedBy = this.sort || this.defaultSortedBy;
  }

  private updateQuery() {
    const defaultQuery: OrganizationSearchQuery = {
      pageNumber: this.selectedPageNumber,
      pageSize: this.selectedPageSize,
      sort: this.sortedBy,
      searchTerms: this.searchedTerms,
      challengeParticipationRoles: this.selectedChallengeParticipationRoles,
      categories: this.selectedCategories,
    };

    this.query.next(defaultQuery);
  }

  private getOrganizationAvatarUrl(org: Organization): Observable<Image> {
    return iif(
      () => !!org.avatarKey,
      this.imageService.getImage({
        objectKey: org.avatarKey,
        height: ImageHeight._140px,
        aspectRatio: ImageAspectRatio._11,
      } as ImageQuery),
      of({ url: '' }),
    ).pipe(
      catchError(() => {
        console.error('Unable to get the image url. Please check the logs of the image service.');
        return of({ url: '' });
      }),
    );
  }

  private getOrganizationCard(org: Organization, avatarUrl: Image): OrganizationCard {
    return {
      shortName: org.shortName,
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
      this.paginator.resetPageNumber();
    }
    // update params of URL
    const currentParams = new HttpParams({
      fromString: this._location.path().split('?')[1] ?? '',
    });
    const params = Object.entries(filteredQuery)
      .map(([key, value]) => {
        // avoid adding pageNumber and pageSize to the params if they are default
        if (
          (key === 'pageNumber' && value === this.defaultPageNumber) ||
          (key === 'pageSize' && value === this.defaultPageSize)
        ) {
          return [key, ''];
        }
        return [key, this.collapseParam(value as FilterValue)];
      })
      .reduce(
        // update with new param, or delete the param if empty string
        (params, [key, value]) => (value !== '' ? params.set(key, value) : params.delete(key)),
        currentParams,
      );
    this._location.replaceState(location.pathname, params.toString());

    // update query to trigger API call
    const newQuery = assign(this.query.getValue(), filteredQuery);
    this.query.next(newQuery);
  }

  splitParam(activeParam: any, by = ','): any[] {
    return activeParam ? activeParam.split(by) : [];
  }

  collapseParam(selectedParam: FilterValue | FilterValue[], by = ','): string {
    return Array.isArray(selectedParam)
      ? selectedParam.map((item) => item?.toString()).join(by)
      : ((selectedParam as string) ?? '');
  }

  openSnackBar(message: string) {
    this._snackBar.open(message, undefined, {
      duration: 30000,
    });
  }
}
