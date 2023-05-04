import { Component } from '@angular/core';
import {
  Image,
  ImageService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
import { Observable } from 'rxjs';

@Component({
  selector: 'openchallenges-team',
  templateUrl: './team.component.html',
  styleUrls: ['./team.component.scss'],
})
export class TeamComponent {
  public appVersion: string;
  public logo$: Observable<Image> | undefined;
  public thomas$: Observable<Image> | undefined;
  public rong$: Observable<Image> | undefined;
  public verena$: Observable<Image> | undefined;
  public maria$: Observable<Image> | undefined;
  public jake$: Observable<Image> | undefined;
  public amy$: Observable<Image> | undefined;

  constructor(
    private readonly configService: ConfigService,
    private imageService: ImageService
  ) {
    this.appVersion = this.configService.config.appVersion;
  }

  ngOnInit() {
    this.logo$ = this.imageService.getImage({
      objectKey: 'openchallenges-icon.svg',
    });
    this.thomas$ = this.imageService.getImage({
      objectKey: 'team/thomas.png',
    });
    this.rong$ = this.imageService.getImage({
      objectKey: 'team/rong.png',
    });
    this.verena$ = this.imageService.getImage({
      objectKey: 'team/verena.png',
    });
    this.maria$ = this.imageService.getImage({
      objectKey: 'team/maria.png',
    });
    this.jake$ = this.imageService.getImage({
      objectKey: 'team/jake.png',
    });
    this.amy$ = this.imageService.getImage({
      objectKey: 'team/amy.png',
    });
  }
}
