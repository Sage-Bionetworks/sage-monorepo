import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import {
  Observable,
  catchError,
  forkJoin,
  iif,
  map,
  of,
  switchMap,
} from 'rxjs';
import {
  Challenge,
  ChallengeContribution,
  ChallengeContributionRole,
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

type ContributionCardBundle = {
  card: OrganizationCard;
  role: ChallengeContributionRole;
};

@Component({
  selector: 'openchallenges-challenge-contributors',
  standalone: true,
  imports: [CommonModule, OrganizationCardComponent],
  templateUrl: './challenge-contributors.component.html',
  styleUrls: ['./challenge-contributors.component.scss'],
})
export class ChallengeContributorsComponent implements OnInit {
  @Input({ required: true }) challenge!: Challenge;
  organizationCards: OrganizationCard[] = [];
  dataContributorCards: OrganizationCard[] = [];
  sponsorCards: OrganizationCard[] = [];

  organizerCardBundles$!: Observable<ContributionCardBundle[]>;
  dataContributorCardBundles$!: Observable<ContributionCardBundle[]>;
  sponsorCardBundles$!: Observable<ContributionCardBundle[]>;

  constructor(
    private challengeContributionService: ChallengeContributionService,
    private organizationService: OrganizationService,
    private imageService: ImageService
  ) {}

  ngOnInit() {
    // Get all the contributions
    const contribs$: Observable<ChallengeContribution[]> =
      this.challengeContributionService
        .listChallengeContributions(this.challenge.id)
        .pipe(map((page) => page.challengeContributions));

    // Get a list of unique orgs to minimize outbound requests
    const orgs$: Observable<Organization[]> = contribs$.pipe(
      map((contribs) => [...new Set(contribs.map((c) => c.organizationId))]),
      switchMap((orgIds) =>
        forkJoinConcurrent(
          orgIds.map((orgId) =>
            this.organizationService.getOrganization(orgId.toString())
          ),
          Infinity
        )
      )
    );

    // Get the logo of the orgs
    const orgLogos$ = orgs$.pipe(
      switchMap((orgs) =>
        forkJoinConcurrent(
          orgs.map((org) => this.getOrganizationAvatarUrl(org)),
          Infinity
        )
      )
    );

    // Creates the contribution card bundles
    const contributionCardBundles$ = forkJoin({
      contribs: contribs$,
      orgs: orgs$,
      orgLogos: orgLogos$,
    }).pipe(
      map((data) => {
        const contributionCardBundles: ContributionCardBundle[] = [];
        for (const contrib of data.contribs) {
          const orgIndex = data.orgs
            .map((org) => org.id)
            .indexOf(contrib.organizationId);
          const org = orgIndex >= 0 ? data.orgs[orgIndex] : undefined;
          const orgLogo = orgIndex >= 0 ? data.orgLogos[orgIndex] : undefined;
          if (org !== undefined && orgLogo !== undefined) {
            contributionCardBundles.push({
              card: this.getOrganizationCard(org),
              role: contrib.role,
            });
          }
        }
        return contributionCardBundles;
      })
    );

    this.organizerCardBundles$ = contributionCardBundles$.pipe(
      map((bundle) =>
        bundle.filter(
          (b) => b.role === ChallengeContributionRole.ChallengeOrganizer
        )
      )
    );

    contribs$.subscribe((contribs) =>
      console.log(`n contribs: ${contribs.length}`)
    );

    orgs$.subscribe((orgs) => console.log(`n orgs: ${orgs.length}`));

    // .pipe(
    //   switchMap((page) =>
    //     forkJoinConcurrent(
    //       page.challengeContributions.map((c) => ({
    //         organization: this.organizationService.getOrganization(
    //           c.organizationId.toString()
    //         ),
    //         role: c.role
    //       }))
    //       this.uniqueContributions(page.challengeContributions).map(
    //         (contribution) =>
    //           this.organizationService.getOrganization(
    //             contribution.organizationId.toString()
    //           )
    //       ),
    //       Infinity
    //     )
    //   )
    // );
    // this.challengeContributionService
    //   .listChallengeContributions(this.challenge.id)
    //   .pipe(
    //     switchMap((page) =>
    //       forkJoinConcurrent(
    //         this.uniqueContributions(page.challengeContributions).map(
    //           (contribution) =>
    //             this.organizationService.getOrganization(
    //               contribution.organizationId.toString()
    //             )
    //         ),
    //         Infinity
    //       )
    //     ),
    //     map((orgs: Organization[]) => this.sortOrgs(orgs)),
    //     switchMap((orgs) =>
    //       forkJoin({
    //         orgs: of(orgs),
    //         avatarUrls: forkJoinConcurrent(
    //           orgs.map((org) => this.getOrganizationAvatarUrl(org)),
    //           Infinity
    //         ),
    //       })
    //     ),
    //     switchMap(({ orgs, avatarUrls }) =>
    //       of(
    //         orgs.map((org, index) =>
    //           this.getOrganizationCard(org, avatarUrls[index])
    //         )
    //       )
    //     )
    //   )
    //   .subscribe((orgCards) => (this.organizationCards = orgCards));
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
    org: Organization
    // avatarUrl: Image
  ): OrganizationCard {
    return {
      acronym: org.acronym,
      // avatarUrl: avatarUrl?.url,
      challengeCount: org.challengeCount,
      login: org.login,
      name: org.name,
    } as OrganizationCard;
  }
}
