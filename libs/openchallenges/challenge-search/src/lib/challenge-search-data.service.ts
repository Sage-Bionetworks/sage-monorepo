import { inject, Injectable } from '@angular/core';
import { BehaviorSubject, forkJoin, iif, Observable, of } from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, map, switchMap } from 'rxjs/operators';
import {
  ChallengePlatformSearchQuery,
  ChallengePlatformService,
  ChallengePlatformSort,
  EdamConceptSearchQuery,
  EdamConceptService,
  EdamConceptSort,
  Image,
  ImageAspectRatio,
  ImageHeight,
  ImageQuery,
  ImageService,
  Organization,
  OrganizationSearchQuery,
  OrganizationService,
  OrganizationSort,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { forkJoinConcurrent } from '@sagebionetworks/openchallenges/util';
import { Filter } from '@sagebionetworks/openchallenges/ui';
import { ChallengeSearchDropdown } from './challenge-search-dropdown';

@Injectable({
  providedIn: 'root',
})
export class ChallengeSearchDataService {
  private readonly challengePlatformService = inject(ChallengePlatformService);
  private readonly edamConceptService = inject(EdamConceptService);
  private readonly imageService = inject(ImageService);
  private readonly organizationService = inject(OrganizationService);

  private edamConceptSearchQuery: BehaviorSubject<EdamConceptSearchQuery> =
    new BehaviorSubject<EdamConceptSearchQuery>({
      sort: EdamConceptSort.PreferredLabel,
    });

  private organizationSearchQuery: BehaviorSubject<OrganizationSearchQuery> =
    new BehaviorSubject<OrganizationSearchQuery>({
      sort: OrganizationSort.Name,
    });

  private platformSearchQuery: BehaviorSubject<ChallengePlatformSearchQuery> =
    new BehaviorSubject<ChallengePlatformSearchQuery>({
      sort: ChallengePlatformSort.Name,
    });

  setEdamConceptSearchQuery(searchQuery: EdamConceptSearchQuery) {
    const currentState = this.edamConceptSearchQuery.getValue();
    this.edamConceptSearchQuery.next({ ...currentState, ...searchQuery });
  }

  setOriganizationSearchQuery(searchQuery: OrganizationSearchQuery) {
    const currentState = this.organizationSearchQuery.getValue();
    this.organizationSearchQuery.next({ ...currentState, ...searchQuery });
  }

  setPlatformSearchQuery(searchQuery: ChallengePlatformSearchQuery) {
    const currentState = this.platformSearchQuery.getValue();
    this.platformSearchQuery.next({ ...currentState, ...searchQuery });
  }

  getEdamConcepts(newQuery: EdamConceptSearchQuery): Observable<Filter[]> {
    return this.edamConceptSearchQuery.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      switchMap((searchQuery: EdamConceptSearchQuery) =>
        // use the properties from new query to overwrite the ones from old query
        this.edamConceptService.listEdamConcepts({
          ...searchQuery,
          ...newQuery,
        }),
      ),
      map((page) =>
        page.edamConcepts.map((edamConcept) => ({
          value: edamConcept.id,
          label: edamConcept.preferredLabel,
          active: false,
        })),
      ),
    );
  }

  getOriganizations(newQuery: OrganizationSearchQuery): Observable<Filter[]> {
    return this.organizationSearchQuery.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      switchMap((searchQuery: OrganizationSearchQuery) =>
        this.organizationService.listOrganizations({
          ...searchQuery,
          ...newQuery,
        }),
      ),
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
      map(({ orgs, avatarUrls }) =>
        orgs.map((org, index) => ({
          value: org.id,
          label: org.name,
          avatarUrl: avatarUrls[index]?.url,
          active: false,
        })),
      ),
    );
  }

  private getOrganizationAvatarUrl(org: Organization): Observable<Image> {
    return iif(
      () => !!org.avatarKey,
      this.imageService.getImage({
        objectKey: org.avatarKey,
        height: ImageHeight._32px,
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

  getPlatforms(newQuery: ChallengePlatformSearchQuery): Observable<Filter[]> {
    return this.platformSearchQuery.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      switchMap((searchQuery: ChallengePlatformSearchQuery) => {
        return this.challengePlatformService.listChallengePlatforms({
          ...searchQuery,
          ...newQuery,
        });
      }),
      map((page) =>
        page.challengePlatforms.map((platform) => ({
          value: platform.slug,
          label: platform.name,
          active: false,
        })),
      ),
    );
  }

  setSearchQuery(dropdown: ChallengeSearchDropdown, searchQuery = {}) {
    const setQueryMethods = {
      inputDataTypes: () => this.setEdamConceptSearchQuery(searchQuery),
      operations: () => this.setEdamConceptSearchQuery(searchQuery),
      organizations: () => this.setOriganizationSearchQuery(searchQuery),
      platforms: () => this.setPlatformSearchQuery(searchQuery),
    };

    return setQueryMethods[dropdown]();
  }

  fetchData(dropdown: ChallengeSearchDropdown, searchQuery = {}) {
    const fetchDataMethods = {
      inputDataTypes: () => this.getEdamConcepts(searchQuery),
      operations: () => this.getEdamConcepts(searchQuery),
      organizations: () => this.getOriganizations(searchQuery),
      platforms: () => this.getPlatforms(searchQuery),
    };

    return fetchDataMethods[dropdown]();
  }
}
