import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  Image,
  ImageHeight,
  ImageService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { Observable } from 'rxjs';

@Component({
  selector: 'openchallenges-sponsor-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sponsor-list.component.html',
  styleUrls: ['./sponsor-list.component.scss'],
})
export class SponsorListComponent implements OnInit {
  itcr$: Observable<Image> | undefined;
  aws$: Observable<Image> | undefined;
  sage$: Observable<Image> | undefined;
  readonly height = ImageHeight._140px;

  constructor(private imageService: ImageService) {}

  ngOnInit() {
    this.itcr$ = this.imageService.getImage({
      objectKey: 'logo/nci-itcr-alt.svg',
      height: this.height,
    });
    this.sage$ = this.imageService.getImage({
      objectKey: 'logo/sage-bionetworks-alt.svg',
      height: this.height,
    });
  }
}
