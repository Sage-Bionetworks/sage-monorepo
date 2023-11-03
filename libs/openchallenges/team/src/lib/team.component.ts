import { CommonModule } from '@angular/common';
import { Component, OnInit, Renderer2 } from '@angular/core';
import { RouterModule } from '@angular/router';
import {
  Image,
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
  public logo$: Observable<Image> | undefined;
  public thomas$: Observable<Image> | undefined;
  public rong$: Observable<Image> | undefined;
  public verena$: Observable<Image> | undefined;
  public maria$: Observable<Image> | undefined;
  public gaia$: Observable<Image> | undefined;
  public jake$: Observable<Image> | undefined;
  public sage$: Observable<Image> | undefined;

  constructor(
    private readonly configService: ConfigService,
    private imageService: ImageService,
    private seoService: SeoService,
    private renderer2: Renderer2
  ) {
    this.appVersion = this.configService.config.appVersion;
    this.dataUpdatedOn = this.configService.config.dataUpdatedOn;
    this.privacyPolicyUrl = this.configService.config.privacyPolicyUrl;
    this.termsOfUseUrl = this.configService.config.termsOfUseUrl;
    this.apiDocsUrl = this.configService.config.apiDocsUrl;
    this.seoService.setData(getSeoData(), this.renderer2);
  }

  ngOnInit() {
    this.logo$ = this.imageService.getImage({
      objectKey: 'openchallenges-icon.svg',
    });
    this.thomas$ = this.imageService.getImage({
      objectKey: 'team/tschaffter.jpeg',
    });
    this.rong$ = this.imageService.getImage({
      objectKey: 'team/rchai.jpeg',
    });
    this.verena$ = this.imageService.getImage({
      objectKey: 'team/vchung.png',
    });
    this.maria$ = this.imageService.getImage({
      objectKey: 'team/mdiaz.png',
    });
    this.gaia$ = this.imageService.getImage({
      objectKey: 'team/gandreoletti.jpeg',
    });
    this.jake$ = this.imageService.getImage({
      objectKey: 'team/jalbrecht.jpeg',
    });
    this.sage$ = this.imageService.getImage({
      objectKey: 'logo/sage-bionetworks-alt.svg',
    });
  }
}
