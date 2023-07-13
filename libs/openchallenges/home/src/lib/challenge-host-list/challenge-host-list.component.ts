import { Component, OnInit } from '@angular/core';
import {
  ImageAspectRatio,
  ImageHeight,
  ImageQuery,
  ImageService,
  Organization,
  OrganizationSearchQuery,
  OrganizationService,
  Image,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { OrganizationCard } from '@sagebionetworks/openchallenges/ui';
import { forkJoinConcurrent } from '@sagebionetworks/openchallenges/util';
import { forkJoin, Observable, of, throwError } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

@Component({
  selector: 'openchallenges-challenge-host-list',
  templateUrl: './challenge-host-list.component.html',
  styleUrls: ['./challenge-host-list.component.scss'],
})
export class ChallengeHostListComponent implements OnInit {
  organizationCards$!: Observable<OrganizationCard[]>;

  
  constructor(
    private organizationService: OrganizationService,
    private imageService: ImageService
  ) {}

  ngOnInit() {
    const query: OrganizationSearchQuery = {
      pageNumber: 0,
      pageSize: 4,
      searchTerms: '',
      sort: 'challenge_count',
    } as OrganizationSearchQuery;

    const orgPage$ = this.organizationService.listOrganizations(query).pipe(
      catchError((err) => {
        return throwError(() => new Error(err.message));
      })
    );

    this.organizationCards$ = orgPage$.pipe(
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
  }

  // TODO Avoid duplicated code (see org search component)
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

  // TODO Avoid duplicated code (see org search component)
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
