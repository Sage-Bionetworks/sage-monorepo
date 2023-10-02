import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import {
  Challenge,
  ChallengeContribution,
  ChallengeContributionService,
  Image,
  ImageAspectRatio,
  ImageHeight,
  ImageQuery,
  ImageService,
  Organization,
  OrganizationService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import {
  OrganizationCard,
  OrganizationCardComponent,
} from '@sagebionetworks/openchallenges/ui';
import { forkJoinConcurrent } from '@sagebionetworks/openchallenges/util';
import {
  Observable,
  catchError,
  forkJoin,
  iif,
  map,
  of,
  switchMap,
} from 'rxjs';

@Component({
  selector: 'openchallenges-challenge-contributors',
  standalone: true,
  imports: [CommonModule, OrganizationCardComponent],
  templateUrl: './challenge-contributors.component.html',
  styleUrls: ['./challenge-contributors.component.scss'],
})
export class ChallengeContributorsComponent implements OnInit {
  @Input() challenge!: Challenge;
  organizationCards: OrganizationCard[] = [];
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
            this.uniqueContributions(page.challengeContributions).map(
              (contribution) =>
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
      )
      .subscribe((orgCards) => (this.organizationCards = orgCards));
  }

  private uniqueContributions(
    contributions: ChallengeContribution[]
  ): ChallengeContribution[] {
    return contributions.filter(
      (b, i) =>
        contributions.findIndex(
          (a) => a.organizationId === b.organizationId
        ) === i
    );
  }

  private sortOrgs(orgs: Organization[]): Organization[] {
    return orgs.sort((a, b) => (a.name || '').localeCompare(b.name || ''));
  }

  // TODO Avoid duplicated code (see org search component)
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

  // TODO Avoid duplicated code (see org search component)
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
}
