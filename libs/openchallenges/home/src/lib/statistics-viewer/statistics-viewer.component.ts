import { Component, OnInit } from '@angular/core';
import {
  ChallengeSearchQuery,
  ChallengeService,
  Image,
  ImageHeight,
  ImageService,
  OrganizationSearchQuery,
  OrganizationService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { Observable, map } from 'rxjs';

@Component({
  selector: 'openchallenges-statistics-viewer',
  templateUrl: './statistics-viewer.component.html',
  styleUrls: ['./statistics-viewer.component.scss'],
})
export class StatisticsViewerComponent implements OnInit {
  challengeImg$: Observable<Image> | undefined;
  orgImg$: Observable<Image> | undefined;
  userImg$: Observable<Image> | undefined;

  private imgHeight = ImageHeight._140px;

  challengeCount$: Observable<number> | undefined;
  orgCount$: Observable<number> | undefined;

  constructor(
    private imageService: ImageService,
    private challengeService: ChallengeService,
    private organizationService: OrganizationService
  ) {}

  ngOnInit() {
    this.challengeImg$ = this.imageService.getImage({
      objectKey: 'home-challenges.svg',
      height: this.imgHeight,
    });
    this.orgImg$ = this.imageService.getImage({
      objectKey: 'home-hosts.svg',
      height: this.imgHeight,
    });
    this.userImg$ = this.imageService.getImage({
      objectKey: 'home-users.svg',
      height: this.imgHeight,
    });

    this.challengeCount$ = this.challengeService
      .listChallenges({
        pageNumber: 1,
        pageSize: 1,
      } as ChallengeSearchQuery)
      .pipe(map((page) => page.totalElements));

    this.orgCount$ = this.organizationService
      .listOrganizations({
        pageNumber: 1,
        pageSize: 1,
      } as OrganizationSearchQuery)
      .pipe(map((page) => page.totalElements));
  }
}
