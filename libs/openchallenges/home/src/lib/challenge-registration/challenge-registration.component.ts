import { Component, inject, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { Image, ImageService } from '@sagebionetworks/openchallenges/api-client-angular';
import { Observable } from 'rxjs';

@Component({
  selector: 'openchallenges-challenge-registration',
  imports: [MatButtonModule],
  templateUrl: './challenge-registration.component.html',
  styleUrls: ['./challenge-registration.component.scss'],
})
export class ChallengeRegistrationComponent implements OnInit {
  private readonly imageService = inject(ImageService);

  public share$: Observable<Image> | undefined;

  ngOnInit() {
    this.share$ = this.imageService.getImage({
      objectKey: 'home-cta.svg',
    });
  }
}
