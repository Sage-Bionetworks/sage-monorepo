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
  public triforceImg$: Observable<Image> | undefined;

  constructor(
    private readonly configService: ConfigService,
    private imageService: ImageService
  ) {
    this.appVersion = this.configService.config.appVersion;
  }

  ngOnInit() {
    this.triforceImg$ = this.imageService.getImage('triforce.png');
  }
}
