import { Component, OnInit } from '@angular/core';
import {
  Image,
  ImageService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { Observable } from 'rxjs';

@Component({
  selector: 'openchallenges-challenge-registration',
  templateUrl: './challenge-registration.component.html',
  styleUrls: ['./challenge-registration.component.scss'],
})
export class ChallengeRegistrationComponent implements OnInit {
  public share$: Observable<Image> | undefined;

  constructor(private imageService: ImageService) {}

  ngOnInit() {
    this.share$ = this.imageService.getImage({
      objectKey: 'home-cta.svg',
    });
  }
}
