import { AsyncPipe } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';
import {
  ImageAspectRatio,
  ImageHeight,
  ImageQuery,
  ImageService,
  Organization,
  OrganizationSearchQuery,
  OrganizationService,
  Image,
  OrganizationCategory,
  OrganizationSort,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { OrganizationCard, OrganizationCardComponent } from '@sagebionetworks/openchallenges/ui';
import { forkJoinConcurrent } from '@sagebionetworks/openchallenges/util';
import { forkJoin, iif, Observable, of, throwError } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

@Component({
  selector: 'openchallenges-challenge-host-list',
  imports: [AsyncPipe, MatButtonModule, OrganizationCardComponent, RouterModule],
  templateUrl: './challenge-host-list.component.html',
  styleUrls: ['./challenge-host-list.component.scss'],
})
export class ChallengeHostListComponent implements OnInit {
  private readonly imageService = inject(ImageService);
  private readonly organizationService = inject(OrganizationService);

  organizationCards$!: Observable<OrganizationCard[]>;

  ngOnInit() {
    const query: OrganizationSearchQuery = {
      pageNumber: 0,
      pageSize: 4,
      categories: [OrganizationCategory.Featured],
      searchTerms: '',
      sort: OrganizationSort.ChallengeCount,
    };

    const orgPage$ = this.organizationService.listOrganizations(query).pipe(
      catchError((err) => {
        return throwError(() => new Error(err.message));
      }),
    );

    this.organizationCards$ = orgPage$.pipe(
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
      switchMap(({ orgs, avatarUrls }) =>
        of(orgs.map((org, index) => this.getOrganizationCard(org, avatarUrls[index]))),
      ),
    );
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
      of({ url: '' }),
    ).pipe(
      catchError(() => {
        console.error('Unable to get the image url. Please check the logs of the image service.');
        return of({ url: '' });
      }),
    );
  }

  // TODO Avoid duplicated code (see org search component)
  private getOrganizationCard(org: Organization, avatarUrl: Image): OrganizationCard {
    return {
      shortName: org.shortName,
      avatarUrl: avatarUrl?.url,
      challengeCount: org.challengeCount,
      login: org.login,
      name: org.name,
    } as OrganizationCard;
  }
}
