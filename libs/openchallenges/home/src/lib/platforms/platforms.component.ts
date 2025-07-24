import { Component, inject, OnInit } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { Image, ImageService } from '@sagebionetworks/openchallenges/api-client-angular';
import { Observable } from 'rxjs';

@Component({
  selector: 'openchallenges-platforms',
  imports: [AsyncPipe],
  templateUrl: './platforms.component.html',
  styleUrls: ['./platforms.component.scss'],
})
export class PlatformsComponent implements OnInit {
  private readonly imageService = inject(ImageService);

  public platforms$: Observable<Image> | undefined;

  ngOnInit() {
    this.platforms$ = this.imageService.getImage({
      objectKey: 'logo/platforms-v4.png',
    });
  }
}
