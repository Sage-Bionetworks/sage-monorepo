import { CommonModule } from '@angular/common';
import { Component, OnInit, Renderer2 } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { ConfigService } from '@sagebionetworks/agora/config';
import { SeoService } from '@sagebionetworks/shared/util';
import { DataversionService, Dataversion } from '@sagebionetworks/agora/api-client-angular';
import { getSeoData } from './seo-data';
import { Observable } from 'rxjs';
// import { SynapseApiService } from '@sagebionetworks/agora/services';
// import { SynapseWiki } from '@sagebionetworks/agora/models';
// import { OrgSagebionetworksRepoModelWikiWikiPage } from '@sagebionetworks/synapse/api-client-angular';

@Component({
  selector: 'agora-not-found',
  imports: [CommonModule, RouterModule, MatCardModule, MatButtonModule],
  templateUrl: './not-found.component.html',
  styleUrls: ['./not-found.component.scss'],
})
export class NotFoundComponent implements OnInit {
  public appVersion: string;
  public apiDocsUrl: string;

  dataversion$!: Observable<Dataversion>;
  // wiki$!: Observable<SynapseWiki>;
  // wikiAlternative$!: Observable<OrgSagebionetworksRepoModelWikiWikiPage>;

  constructor(
    private readonly configService: ConfigService,
    private dataversionService: DataversionService,
    private seoService: SeoService,
    private renderer2: Renderer2,
    // private synapseApiService: SynapseApiService,
  ) {
    this.appVersion = this.configService.config.appVersion;
    this.apiDocsUrl = this.configService.config.apiDocsUrl;

    this.seoService.setData(getSeoData(), this.renderer2);
  }

  ngOnInit(): void {
    this.dataversion$ = this.dataversionService.getDataversion();

    // const ownerId = 'syn25913473';
    // const wikiId = '612058';
    // this.wiki$ = this.synapseApiService.getWiki(ownerId, wikiId);
    // this.wikiAlternative$ = this.synapseApiService.getWikiAlternative(ownerId, wikiId);
  }
}
