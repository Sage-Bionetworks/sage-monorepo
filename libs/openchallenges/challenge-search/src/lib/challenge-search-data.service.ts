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
  private platformSearchTerms: BehaviorSubject<string> =
    new BehaviorSubject<string>('');

  private organizationSearchTerms: BehaviorSubject<string> =
    new BehaviorSubject<string>('');

  constructor(
    private challengePlatformService: ChallengePlatformService,
    private organizationService: OrganizationService,
    private imageService: ImageService
  ) {}

  setPlatformSearchTerms(searchTerms: string) {
    this.platformSearchTerms.next(searchTerms);
  }

  setOriganizationSearchTerms(searchTerms: string) {
    this.organizationSearchTerms.next(searchTerms);
  }

  getPlatforms(
    platformQuery: ChallengePlatformSearchQuery
  ): Observable<Filter[]> {
    return this.platformSearchTerms.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      switchMap((searchTerm: string) => {
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
}
