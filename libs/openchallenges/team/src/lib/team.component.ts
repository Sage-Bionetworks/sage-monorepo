import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import {
  Image,
  ImageService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
import { FooterComponent } from '@sagebionetworks/openchallenges/ui';
import { Observable } from 'rxjs';

@Component({
  selector: 'openchallenges-team',
  standalone: true,
  imports: [CommonModule, RouterModule, FooterComponent],
  templateUrl: './team.component.html',
  styleUrls: ['./team.component.scss'],
})
export class TeamComponent implements OnInit {
  public appVersion: string;
  public dataUpdatedOn: string;
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
    this.dataUpdatedOn = this.configService.config.dataUpdatedOn;
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
