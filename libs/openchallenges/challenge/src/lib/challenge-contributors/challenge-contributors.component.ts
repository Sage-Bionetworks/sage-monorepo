import { Component, Input, OnInit } from '@angular/core';
import {
  Challenge,
  ChallengeContributionService,
  Image,
  ImageAspectRatio,
  ImageHeight,
  ImageQuery,
  ImageService,
  Organization,
  OrganizationService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { OrganizationCard } from '@sagebionetworks/openchallenges/ui';
import { forkJoinConcurrent } from '@sagebionetworks/openchallenges/util';
import { Observable, forkJoin, map, of, switchMap } from 'rxjs';

@Component({
  selector: 'openchallenges-challenge-contributors',
  templateUrl: './challenge-contributors.component.html',
  styleUrls: ['./challenge-contributors.component.scss'],
})
export class ChallengeContributorsComponent implements OnInit {
  @Input() challenge!: Challenge;
  organizationCards!: OrganizationCard[];
  constructor(
    private challengeContributionService: ChallengeContributionService,
    private organizationService: OrganizationService,
    private imageService: ImageService
  ) {}

  ngOnInit() {
    this.challengeContributionService
      .listChallengeContributions(this.challenge.id)
      .pipe(
        switchMap((page) =>
          forkJoinConcurrent(
            page.challengeContributions.map((contribution) =>
              this.organizationService.getOrganization(
                contribution.organizationId.toString()
              )
            ),
            Infinity
          )
        ),
        map((orgs: Organization[]) => this.sortOrgs(orgs)),
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
      )
      .subscribe((orgCards) => (this.organizationCards = orgCards));
  }

  private sortOrgs(orgs: Organization[]): Organization[] {
    return orgs.sort((a, b) => (a.name || '').localeCompare(b.name || ''));
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
