import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  Image,
  ImageService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
import { FooterComponent } from '@sagebionetworks/openchallenges/ui';
import { Observable } from 'rxjs';

@Component({
  selector: 'openchallenges-about',
  standalone: true,
  imports: [FooterComponent, CommonModule],
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.scss'],
})
export class AboutComponent implements OnInit {
  public appVersion: string;
  public logo$: Observable<Image> | undefined;
  public bars$: Observable<Image> | undefined;
  public fingerprint$: Observable<Image> | undefined;
  public chart$: Observable<Image> | undefined;
  public graph$: Observable<Image> | undefined;

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
    this.bars$ = this.imageService.getImage({
      objectKey: 'icon-bars.svg',
    });
    this.fingerprint$ = this.imageService.getImage({
      objectKey: 'icon-fingerprint.svg',
    });
    this.chart$ = this.imageService.getImage({
      objectKey: 'icon-chart.svg',
    });
    this.graph$ = this.imageService.getImage({
      objectKey: 'icon-graph.svg',
    });
  }
}
