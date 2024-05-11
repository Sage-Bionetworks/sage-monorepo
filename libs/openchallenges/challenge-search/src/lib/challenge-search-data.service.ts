import { Injectable } from '@angular/core';
import { BehaviorSubject, forkJoin, iif, Observable, of } from 'rxjs';
import {
  catchError,
  debounceTime,
  distinctUntilChanged,
  take,
  map,
  switchMap,
} from 'rxjs/operators';
import {
  ChallengePlatformSearchQuery,
  ChallengePlatformService,
  EdamConceptSearchQuery,
  EdamConceptService,
  Image,
  ImageAspectRatio,
  ImageHeight,
  ImageQuery,
  ImageService,
  Organization,
  OrganizationSearchQuery,
  OrganizationService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { forkJoinConcurrent } from '@sagebionetworks/openchallenges/util';
import { Filter } from '@sagebionetworks/openchallenges/ui';

@Injectable({
  providedIn: 'root',
})
export class ChallengeSearchDataService {
  private edamConceptSearchQuery: BehaviorSubject<EdamConceptSearchQuery> =
    new BehaviorSubject<EdamConceptSearchQuery>({});

  private organizationSearchQuery: BehaviorSubject<OrganizationSearchQuery> =
    new BehaviorSubject<OrganizationSearchQuery>({});

  private platformSearchQuery: BehaviorSubject<ChallengePlatformSearchQuery> =
    new BehaviorSubject<ChallengePlatformSearchQuery>({});

  constructor(
    private challengePlatformService: ChallengePlatformService,
    private edamConceptService: EdamConceptService,
    private imageService: ImageService,
    private organizationService: OrganizationService
  ) {}

  setEdamConceptSearch(searchQuery: EdamConceptSearchQuery) {
    this.edamConceptSearchQuery.next({ ...searchQuery });
  }

  setOriganizationSearch(searchQuery: OrganizationSearchQuery) {
    this.organizationSearchQuery.next({ ...searchQuery });
  }

  setPlatformSearch(searchQuery: ChallengePlatformSearchQuery) {
    this.platformSearchQuery.next({ ...searchQuery });
  }

  getPlatforms(newQuery: ChallengePlatformSearchQuery): Observable<Filter[]> {
    return this.platformSearchQuery.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      take(1),
      switchMap((searchQuery: ChallengePlatformSearchQuery) => {
        // use newQuery properties to overwrite searchQuery ones
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
        }))
      )
    );
  }

  getEdamConcepts(newQuery: EdamConceptSearchQuery): Observable<Filter[]> {
    return this.edamConceptSearchQuery.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      take(1),
      switchMap((searchQuery: EdamConceptSearchQuery) =>
        this.edamConceptService.listEdamConcepts({
          ...searchQuery,
          ...newQuery,
        })
      ),
      map((page) =>
        page.edamConcepts.map((edamConcept) => ({
          value: edamConcept.id,
          label: edamConcept.preferredLabel,
          active: false,
        }))
      )
    );
  }

  getOriganizations(newQuery: OrganizationSearchQuery): Observable<Filter[]> {
    return this.organizationSearchQuery.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      take(1),
      switchMap((searchQuery: OrganizationSearchQuery) =>
        this.organizationService.listOrganizations({
          ...searchQuery,
          ...newQuery,
        })
      ),
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
}
