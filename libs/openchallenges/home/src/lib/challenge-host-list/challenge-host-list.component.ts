import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
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
import {
  OrganizationCard,
  OrganizationCardComponent,
} from '@sagebionetworks/openchallenges/ui';
import { forkJoinConcurrent } from '@sagebionetworks/openchallenges/util';
import { forkJoin, Observable, of, throwError } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

@Component({
  selector: 'openchallenges-challenge-host-list',
  standalone: true,
  imports: [CommonModule, MatButtonModule, OrganizationCardComponent],
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
      categories: ['featured'],
      searchTerms: '',
      sort: 'challenge_count',
    };

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
    );
  }

  // TODO Avoid duplicated code (see org search component)
  private getOrganizationAvatarUrl(org: Organization): Observable<Image> {
    return this.imageService
      .getImage({
        objectKey: org.avatarKey,
        height: ImageHeight._140px,
        aspectRatio: ImageAspectRatio._11,
      } as ImageQuery)
      .pipe(
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
