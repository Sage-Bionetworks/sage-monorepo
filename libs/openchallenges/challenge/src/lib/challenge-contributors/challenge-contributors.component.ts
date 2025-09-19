import { Component, inject, Input, OnInit } from '@angular/core';
import { Observable, catchError, forkJoin, iif, map, of, shareReplay, switchMap, take } from 'rxjs';
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
import { OrganizationCard, OrganizationCardComponent } from '@sagebionetworks/openchallenges/ui';
import { forkJoinConcurrent } from '@sagebionetworks/openchallenges/util';
import { orderBy } from 'lodash';

type ContributionCardBundle = {
  card: OrganizationCard;
  role: ChallengeContributionRole;
};

@Component({
  selector: 'openchallenges-challenge-contributors',
  imports: [OrganizationCardComponent],
  templateUrl: './challenge-contributors.component.html',
  styleUrls: ['./challenge-contributors.component.scss'],
})
export class ChallengeContributorsComponent implements OnInit {
  private readonly challengeContributionService = inject(ChallengeContributionService);
  private readonly organizationService = inject(OrganizationService);
  private readonly imageService = inject(ImageService);

  @Input({ required: true }) challenge!: Challenge;

  organizerCardBundles: ContributionCardBundle[] = [];
  dataContributorCardBundles: ContributionCardBundle[] = [];
  sponsorCardBundles: ContributionCardBundle[] = [];

  ngOnInit() {
    // Get all the contributions
    const contribs$: Observable<ChallengeContribution[]> = this.challengeContributionService
      .listChallengeContributions(this.challenge.id)
      .pipe(
        map((page) => page.challengeContributions),
        shareReplay(1),
        take(1),
      );

    // Get a list of unique orgs to minimize outbound requests
    const orgs$: Observable<Organization[]> = contribs$.pipe(
      map((contribs) => [...new Set(contribs.map((c) => c.organizationId))]),
      switchMap((orgIds) =>
        forkJoinConcurrent(
          orgIds.map((orgId) => this.organizationService.getOrganization(orgId.toString())),
          Infinity,
        ),
      ),
      shareReplay(1),
      take(1),
    );

    // Get the logo of the orgs
    const orgLogos$ = orgs$.pipe(
      switchMap((orgs) =>
        forkJoinConcurrent(
          orgs.map((org) => this.getOrganizationAvatarUrl(org)),
          Infinity,
        ),
      ),
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
          const orgIndex = data.orgs.map((org) => org.id).indexOf(contrib.organizationId);
          const org = orgIndex >= 0 ? data.orgs[orgIndex] : undefined;
          const orgLogo = orgIndex >= 0 ? data.orgLogos[orgIndex] : undefined;
          if (org !== undefined) {
            contributionCardBundles.push({
              card: this.getOrganizationCard(org, orgLogo),
              role: contrib.role,
            });
          }
        }
        return contributionCardBundles;
      }),
      shareReplay(1),
      take(1),
    );

    // Get the organizer card bundles
    this.getContributionCardBundlesByRole(
      contributionCardBundles$,
      ChallengeContributionRole.ChallengeOrganizer,
    ).subscribe((bundles) => (this.organizerCardBundles = bundles));

    // Get the data contributor card bundles
    this.getContributionCardBundlesByRole(
      contributionCardBundles$,
      ChallengeContributionRole.DataContributor,
    ).subscribe((bundles) => (this.dataContributorCardBundles = bundles));

    // Get the sponsor card bundles
    this.getContributionCardBundlesByRole(
      contributionCardBundles$,
      ChallengeContributionRole.Sponsor,
    ).subscribe((bundles) => (this.sponsorCardBundles = bundles));
  }

  private getContributionCardBundlesByRole(
    bundles$: Observable<ContributionCardBundle[]>,
    role: ChallengeContributionRole,
  ): Observable<ContributionCardBundle[]> {
    return bundles$.pipe(
      map((bundles) => bundles.filter((b) => b.role === role)),
      // order orgs by the number of challenges they have contributed to
      map((bundles) => orderBy(bundles, ['card.challengeCount'], ['desc'])),
    );
  }

  // TODO Avoid duplicated code (see org search component)
  private getOrganizationAvatarUrl(org: Organization): Observable<Image | undefined> {
    return iif(
      () => !!org.avatarKey,
      this.imageService.getImage({
        objectKey: org.avatarKey,
        height: ImageHeight._140px,
        aspectRatio: ImageAspectRatio._11,
      } as ImageQuery),
      of(undefined),
    ).pipe(
      catchError(() => {
        console.error('Unable to get the image url. Please check the logs of the image service.');
        return of(undefined);
      }),
    );
  }

  // TODO Avoid duplicated code (see org search component)
  private getOrganizationCard(org: Organization, avatarUrl: Image | undefined): OrganizationCard {
    return {
      shortName: org.shortName,
      avatarUrl: avatarUrl?.url,
      challengeCount: org.challengeCount,
      login: org.login,
      name: org.name,
    } as OrganizationCard;
  }
}
