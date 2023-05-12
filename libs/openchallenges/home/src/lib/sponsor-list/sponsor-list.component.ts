import { Component, OnInit } from '@angular/core';
import {
  Image,
  ImageService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { Observable } from 'rxjs';

@Component({
  selector: 'openchallenges-sponsor-list',
  templateUrl: './sponsor-list.component.html',
  styleUrls: ['./sponsor-list.component.scss'],
})
export class SponsorListComponent implements OnInit {
  public itcr$: Observable<Image> | undefined;
  public aws$: Observable<Image> | undefined;
  public sage$: Observable<Image> | undefined;

  constructor(private imageService: ImageService) {}

  ngOnInit() {
    this.itcr$ = this.imageService.getImage({
      objectKey: 'logo/nci-itcr.png',
    });
    this.aws$ = this.imageService.getImage({
      objectKey: 'logo/aws.svg',
    });
    this.sage$ = this.imageService.getImage({
      objectKey: 'logo/sage-bionetworks.png',
    });
  }
}
