import { Component, inject, OnInit, PLATFORM_ID } from '@angular/core';
import {
  ChallengePlatformService,
  ChallengeService,
  Image,
  ImageHeight,
  ImageService,
  OrganizationService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { Observable, map } from 'rxjs';
import { Router, RouterModule } from '@angular/router';
import { AsyncPipe, isPlatformServer } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgxTypedJsModule } from 'ngx-typed-js';
import { InputTextModule } from 'primeng/inputtext';
import { CountUpModule } from 'ngx-countup';

@Component({
  selector: 'openchallenges-challenge-search',
  imports: [AsyncPipe, FormsModule, NgxTypedJsModule, InputTextModule, CountUpModule, RouterModule],
  templateUrl: './challenge-search.component.html',
  styleUrls: ['./challenge-search.component.scss'],
})
export class ChallengeSearchComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly imageService = inject(ImageService);
  private readonly challengeService = inject(ChallengeService);
  private readonly challengePlatformService = inject(ChallengePlatformService);
  private readonly organizationService = inject(OrganizationService);
  private readonly platformId: Record<string, any> = inject(PLATFORM_ID);

  private imgHeight = ImageHeight._140px;
  public isPlatformServer = false;
  public searchOC$: Observable<Image> | undefined;
  searchTerms!: string | undefined;
  challengeCount$: Observable<number> | undefined;
  orgCount$: Observable<number> | undefined;
  platformCount$: Observable<number> | undefined;
  reanimateOnClick = false;
  challengeImg$: Observable<Image> | undefined;
  orgImg$: Observable<Image> | undefined;
  userImg$: Observable<Image> | undefined;

  constructor() {
    this.isPlatformServer = isPlatformServer(this.platformId);
  }

  ngOnInit() {
    this.searchOC$ = this.imageService.getImage({
      objectKey: 'home-search.svg',
    });
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
    this.platformCount$ = this.challengePlatformService
      .listChallengePlatforms({
        pageNumber: 1,
        pageSize: 1,
      })
      .pipe(map((page) => page.totalElements));
  }

  onSearch(): void {
    this.router.navigateByUrl('/challenge?searchTerms=' + this.searchTerms);
  }
}
