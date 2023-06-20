import {
  Component,
  Input,
  // OnInit
} from '@angular/core';
import {
  Challenge,
  // ChallengeContribution,
  // ChallengeContributionService,
  // Image,
  // ImageAspectRatio,
  // ImageHeight,
  // ImageQuery,
  // ImageService,
  // Organization,
  // OrganizationService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import {
  MOCK_CHALLENGE_SPONSORS,
  MOCK_ORGANIZATION_CARDS,
  // OrganizationCard,
} from '@sagebionetworks/openchallenges/ui';
// import { forkJoinConcurrent } from '@sagebionetworks/openchallenges/util';
// import { Observable, forkJoin, map, of, switchMap } from 'rxjs';

@Component({
  selector: 'openchallenges-challenge-sponsors',
  templateUrl: './challenge-sponsors.component.html',
  styleUrls: ['./challenge-sponsors.component.scss'],
})
export class ChallengeSponsorsComponent {
  @Input() challenge!: Challenge;
  sponsors = MOCK_CHALLENGE_SPONSORS;
  organizationCard = MOCK_ORGANIZATION_CARDS[0];

  // contributionCards!: OrganizationCard[];

  // constructor(
  //   private challengeContributionService: ChallengeContributionService,
  //   private organizationService: OrganizationService,
  //   private imageService: ImageService
  // ) {}

  // ngOnInit() {
  // TODO: need backend support to return org's login
  // from ChallengeContributionService
  //   const orgPage$ = this.challengeContributionService
  //     .listChallengeContributions(this.challenge.id)
  //     .pipe(
  //       map((contributionPage) => contributionPage.challengeContributions),
  //       switchMap((contributions) =>
  //         forkJoin(
  //           contributions.map((contribution) =>
  //             this.organizationService.listOrganizations(
  //               contribution.organizationId
  //             )
  //           )
  //         )
  //       )
  //     );
  //   orgPage$.pipe(
  //     map((page) => page.organizations),
  //     switchMap((orgs) =>
  //       forkJoin({
  //         orgs: of(orgs),
  //         avatarUrls: forkJoinConcurrent(
  //           orgs.map((org) => this.getOrganizationAvatarUrl(org)),
  //           Infinity
  //         ) as unknown as Observable<(Image | undefined)[]>,
  //       })
  //     ),
  //     switchMap(({ orgs, avatarUrls }) =>
  //       of(
  //         orgs.map((org, index) =>
  //           this.getOrganizationCard(org, avatarUrls[index])
  //         )
  //       )
  //     )
  //   ).subscribe(organizationCards =>
  //     this.contributionCards = organizationCards;
  //   )
  // }
  // private getOrganizationAvatarUrl(
  //   org: Organization
  // ): Observable<Image | undefined> {
  //   if (org.avatarKey && org.avatarKey.length > 0) {
  //     return this.imageService.getImage({
  //       objectKey: org.avatarKey,
  //       height: ImageHeight._140px,
  //       aspectRatio: ImageAspectRatio._11,
  //     } as ImageQuery);
  //   } else {
  //     return of(undefined);
  //   }
  // }
  // private getOrganizationCard(
  //   org: Organization,
  //   avatarUrl: Image | undefined
  // ): OrganizationCard {
  //   return {
  //     acronym: org.acronym,
  //     avatarUrl: avatarUrl?.url,
  //     challengeCount: org.challengeCount,
  //     login: org.login,
  //     name: org.name,
  //   } as OrganizationCard;
  // }
  // }
}
