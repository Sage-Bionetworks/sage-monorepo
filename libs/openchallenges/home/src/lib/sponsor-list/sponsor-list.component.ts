import { AsyncPipe } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import {
  Image,
  ImageHeight,
  ImageService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { Observable } from 'rxjs';

@Component({
  selector: 'openchallenges-sponsor-list',
  imports: [AsyncPipe],
  templateUrl: './sponsor-list.component.html',
  styleUrls: ['./sponsor-list.component.scss'],
})
export class SponsorListComponent implements OnInit {
  private readonly imageService = inject(ImageService);

  itcrImage$: Observable<Image> | undefined;
  readonly height = ImageHeight._140px;

  ngOnInit() {
    this.itcrImage$ = this.imageService.getImage({
      objectKey: 'logo/nci-itcr-alt.svg',
      height: this.height,
    });
  }
}
