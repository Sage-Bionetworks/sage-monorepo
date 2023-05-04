import { Component, OnInit } from '@angular/core';
import {
  Image,
  ImageService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import {
  Registry,
  RegistryService,
} from '@sagebionetworks/openchallenges/api-client-angular-deprecated';
import { Observable } from 'rxjs';

@Component({
  selector: 'openchallenges-statistics-viewer',
  templateUrl: './statistics-viewer.component.html',
  styleUrls: ['./statistics-viewer.component.scss'],
})
export class StatisticsViewerComponent implements OnInit {
  public challenges$: Observable<Image> | undefined;
  public orgs$: Observable<Image> | undefined;
  public users$: Observable<Image> | undefined;
  registry$!: Observable<Registry>;

  constructor(
    private registryService: RegistryService,
    private imageService: ImageService
  ) {
    this.registry$ = this.registryService.getRegistry();
  }

  ngOnInit() {
    this.challenges$ = this.imageService.getImage({
      objectKey: 'home-challenges.svg',
    });
    this.orgs$ = this.imageService.getImage({
      objectKey: 'home-hosts.svg',
    });
    this.users$ = this.imageService.getImage({
      objectKey: 'home-users.svg',
    });
  }
}
