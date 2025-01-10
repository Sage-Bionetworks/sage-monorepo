import { CommonModule } from '@angular/common';
import { Component, OnInit, Renderer2 } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { FooterComponent } from '@sagebionetworks/model-ad/ui';
import { ConfigService } from '@sagebionetworks/model-ad/config';
import { SeoService } from '@sagebionetworks/shared/util';
import { getSeoData } from './seo-data';
import { Gene, GeneService } from '@sagebionetworks/model-ad/api-client-angular';
import { Observable, map } from 'rxjs';

@Component({
  selector: 'model-ad-not-found',
  imports: [CommonModule, RouterModule, MatCardModule, MatButtonModule, FooterComponent],
  templateUrl: './not-found.component.html',
  styleUrls: ['./not-found.component.scss'],
})
export class NotFoundComponent implements OnInit {
  public appVersion: string;
  public dataUpdatedOn: string;
  public privacyPolicyUrl: string;
  public termsOfUseUrl: string;
  public apiDocsUrl: string;

  genes$!: Observable<Gene[]>;

  constructor(
    private readonly configService: ConfigService,
    private geneService: GeneService,
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
    this.genes$ = this.geneService.listGenes().pipe(map((page) => page.genes));
  }
}
