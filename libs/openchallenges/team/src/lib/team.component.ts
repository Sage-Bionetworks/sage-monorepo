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
    this.logo$ = this.getTeamImage('openchallenges-icon.svg');
    this.thomas$ = this.getTeamImage('team/tschaffter.jpeg', '500px', '1_1');
    this.rong$ = this.getTeamImage('team/rchai.jpeg', '500px', '1_1');
    this.verena$ = this.getTeamImage('team/vchung.png', '500px', '1_1');
    this.maria$ = this.getTeamImage('team/mdiaz.png', '500px', '1_1');
    this.gaia$ = this.getTeamImage('team/gandreoletti.jpeg', '500px', '1_1');
    this.jake$ = this.getTeamImage('team/jalbrecht.jpeg', '500px', '1_1');
    this.sage$ = this.getTeamImage('logo/sage-bionetworks-alt.svg');
  }

  private getTeamImage(
    path: string,
    height: ImageHeight = 'original',
    ratio: ImageAspectRatio = 'original'
  ): Observable<Image> {
    return this.imageService.getImage({
      objectKey: path,
      height,
      aspectRatio: ratio,
    });
  }
}
