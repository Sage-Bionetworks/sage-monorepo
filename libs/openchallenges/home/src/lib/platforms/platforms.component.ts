import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Image, ImageService } from '@sagebionetworks/openchallenges/api-client-angular';
import { Observable } from 'rxjs';

@Component({
  selector: 'openchallenges-platforms',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './platforms.component.html',
  styleUrls: ['./platforms.component.scss'],
})
export class PlatformsComponent implements OnInit {
  public platforms$: Observable<Image> | undefined;

  constructor(private imageService: ImageService) {}

  ngOnInit() {
    this.platforms$ = this.imageService.getImage({
      objectKey: 'logo/platforms-v4.png',
    });
  }
}
