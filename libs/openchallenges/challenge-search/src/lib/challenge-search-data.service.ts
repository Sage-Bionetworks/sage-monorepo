import { Injectable } from '@angular/core';
import { BehaviorSubject, forkJoin, iif, Observable, of } from 'rxjs';
import {
  catchError,
  debounceTime,
  distinctUntilChanged,
  map,
  switchMap,
} from 'rxjs/operators';
import {
  ChallengePlatformSearchQuery,
  ChallengePlatformService,
  ChallengePlatformSort,
  EdamConceptSearchQuery,
  EdamConceptService,
  EdamSection,
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

@Injectable({
  providedIn: 'root',
})
export class ChallengeSearchDataService {
  private edamConceptSearchTerms: BehaviorSubject<EdamConceptSearchQuery> =
    new BehaviorSubject<EdamConceptSearchQuery>({});

  private organizationSearchTerms: BehaviorSubject<string> =
    new BehaviorSubject<string>('');

  private platformSearchTerms: BehaviorSubject<string> =
    new BehaviorSubject<string>('');

  constructor(
    private challengePlatformService: ChallengePlatformService,
    private edamConceptService: EdamConceptService,
    private imageService: ImageService,
    private organizationService: OrganizationService
  ) {}

  setEdamConceptSearchTerms(searchQuery: EdamConceptSearchQuery) {
    this.edamConceptSearchTerms.next(searchQuery);
  }

  setOriganizationSearchTerms(searchTerms: string) {
    this.organizationSearchTerms.next(searchTerms);
  }

  setPlatformSearchTerms(searchTerms: string) {
    this.platformSearchTerms.next(searchTerms);
  }

  getPlatforms(
    platformQuery: ChallengePlatformSearchQuery
  ): Observable<Filter[]> {
    return this.platformSearchTerms.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      switchMap((searchTerm: string) => {
        const sortedBy: ChallengePlatformSort = 'name';
        const platformQuery: ChallengePlatformSearchQuery = {
          searchTerms: searchTerm,
          sort: sortedBy,
        };
        // const sortedBy: ChallengePlatformSort = 'name';
        // const platformQuery: ChallengePlatformSearchQuery = {
        //   searchTerms: searchTerm,
        //   sort: sortedBy,
        // };
        return this.challengePlatformService.listChallengePlatforms(
          platformQuery
        );
      }),
      map((page) =>
        page.challengePlatforms.map((platform) => ({
          value: platform.slug,
          label: platform.name,
          active: false,
        }))
      )
    );
  }

  searchEdamConcepts(
    // sections?: EdamSection,
    pageSize?: number,
    pageNumber?: number
  ): Observable<Filter[]> {
    return this.edamConceptSearchTerms.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      switchMap((searchQuery: EdamConceptSearchQuery) => {
        searchQuery.pageSize = pageSize ?? 10;
        searchQuery.pageNumber = pageNumber ?? 0;
        searchQuery.sections = [EdamSection.Data];
        return this.edamConceptService.listEdamConcepts(searchQuery);
      }),
      map((page) =>
        page.edamConcepts.map((edamConcept) => ({
          value: edamConcept.id,
          label: edamConcept.preferredLabel,
          active: false,
        }))
      )
    );
  }

  searchOriganizations(): Observable<Filter[]> {
    return this.organizationSearchTerms.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      switchMap((searchTerm: string) => {
        const sortBy: OrganizationSort = 'name';
        const orgQuery: OrganizationSearchQuery = {
          searchTerms: searchTerm,
          sort: sortBy,
        };
        return this.organizationService.listOrganizations(orgQuery);
      }),
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
      map(({ orgs, avatarUrls }) =>
        orgs.map((org, index) => ({
          value: org.id,
          label: org.name,
          avatarUrl: avatarUrls[index]?.url,
          active: false,
        }))
      )
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

  searchPlatforms(): Observable<Filter[]> {
    return this.platformSearchTerms.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      switchMap((searchTerm: string) => {
        const sortedBy: ChallengePlatformSort = 'name';
        const platformQuery: ChallengePlatformSearchQuery = {
          searchTerms: searchTerm,
          sort: sortedBy,
        };
        return this.challengePlatformService.listChallengePlatforms(
          platformQuery
        );
      }),
      map((page) =>
        page.challengePlatforms.map((platform) => ({
          value: platform.slug,
          label: platform.name,
          active: false,
        }))
      )
    );
  }
}
