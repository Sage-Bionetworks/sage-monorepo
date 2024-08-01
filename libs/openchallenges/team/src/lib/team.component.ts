import { CommonModule } from '@angular/common';
import { Component, OnInit, Renderer2 } from '@angular/core';
import { RouterModule } from '@angular/router';
import {
  Image,
  ImageAspectRatio,
  ImageHeight,
  ImageService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
import { FooterComponent } from '@sagebionetworks/openchallenges/ui';
import { SeoService } from '@sagebionetworks/shared/util';
import { Observable } from 'rxjs';
import { getSeoData } from './team-seo-data';

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
  public privacyPolicyUrl: string;
  public termsOfUseUrl: string;
  public apiDocsUrl: string;

  public avatarImageHeight: ImageHeight = ImageHeight._500px;
  public avatarImageAspectRatio: ImageAspectRatio = ImageAspectRatio._11;

  public ocLogo$: Observable<Image> | undefined;
  public sageLogo$: Observable<Image> | undefined;

  public thomasAvatar$: Observable<Image> | undefined;
  public rongAvatar$: Observable<Image> | undefined;
  public verenaAvatar$: Observable<Image> | undefined;
  public mariaAvatar$: Observable<Image> | undefined;
  public gaiaAvatar$: Observable<Image> | undefined;
  public jakeAvatar$: Observable<Image> | undefined;

  constructor(
    private readonly configService: ConfigService,
    private imageService: ImageService,
    private seoService: SeoService,
    private renderer2: Renderer2,
  ) {
    this.appVersion = this.configService.config.appVersion;
    this.dataUpdatedOn = this.configService.config.dataUpdatedOn;
    this.privacyPolicyUrl = this.configService.config.privacyPolicyUrl;
    this.termsOfUseUrl = this.configService.config.termsOfUseUrl;
    this.apiDocsUrl = this.configService.config.apiDocsUrl;
    this.seoService.setData(getSeoData(), this.renderer2);
  }

  ngOnInit() {
    this.ocLogo$ = this.imageService.getImage({
      objectKey: 'openchallenges-icon.svg',
    });
    this.sageLogo$ = this.imageService.getImage({
      objectKey: 'logo/SageBionetworks-Logo-FullColor.svg',
    });

    this.thomasAvatar$ = this.getAvatarImage('team/thomas_schaffter_v3.jpg');
    this.verenaAvatar$ = this.getAvatarImage('team/verena_chung.jpg');
    this.rongAvatar$ = this.getAvatarImage('team/rong_chai.jpg');
    this.mariaAvatar$ = this.getAvatarImage('team/maria.png');
    this.gaiaAvatar$ = this.getAvatarImage('team/gaia_andreoletti.jpg');
    this.jakeAvatar$ = this.getAvatarImage('team/jake_albrecht.jpg');
  }

  private getAvatarImage(objectKey: string): Observable<Image> {
    return this.imageService.getImage({
      objectKey,
      height: ImageHeight._500px,
      aspectRatio: ImageAspectRatio._11,
    });
  }
}
