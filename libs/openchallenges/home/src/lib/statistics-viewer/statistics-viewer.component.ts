import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  ChallengeService,
  Image,
  ImageHeight,
  ImageService,
  OrganizationService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { CountUpModule } from 'ngx-countup';
import { Observable, map } from 'rxjs';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'openchallenges-statistics-viewer',
  standalone: true,
  imports: [CommonModule, RouterModule, CountUpModule],
  templateUrl: './statistics-viewer.component.html',
  styleUrls: ['./statistics-viewer.component.scss'],
})
export class StatisticsViewerComponent implements OnInit {
  reanimateOnClick = false;
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
      })
      .pipe(map((page) => page.totalElements));

    this.orgCount$ = this.organizationService
      .listOrganizations({
        pageNumber: 1,
        pageSize: 1,
      })
      .pipe(map((page) => page.totalElements));
  }
}
