import { CommonModule } from '@angular/common';
import { Component, OnInit, Renderer2 } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { FooterComponent } from '@sagebionetworks/agora/ui';
import { ConfigService } from '@sagebionetworks/agora/config';
import { SeoService } from '@sagebionetworks/shared/util';
import {
  DataversionService,
  Dataversion,
} from '@sagebionetworks/agora/api-client-angular';
import { getSeoData } from './seo-data';
import { Observable } from 'rxjs';

@Component({
  selector: 'agora-not-found',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    FooterComponent,
  ],
  templateUrl: './not-found.component.html',
  styleUrls: ['./not-found.component.scss'],
})
export class NotFoundComponent implements OnInit {
  public appVersion: string;
  public dataUpdatedOn: string;
  public privacyPolicyUrl: string;
  public termsOfUseUrl: string;
  public apiDocsUrl: string;

  dataversion$!: Observable<Dataversion>;

  constructor(
    private readonly configService: ConfigService,
    private dataVersionService: DataversionService,
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

  ngOnInit(): void {
    this.dataversion$ = this.dataVersionService.getDataversion();
  }
}
