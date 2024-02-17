import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  Image,
  ImageService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { Observable } from 'rxjs';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'openchallenges-footer',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent implements OnInit {
  public sageLogo$: Observable<Image> | undefined;

  @Input({ required: true }) appVersion = '';
  @Input({ required: true }) dataUpdatedOn = '';
  @Input({ required: true }) privacyPolicyUrl = '';
  @Input({ required: true }) termsOfUseUrl = '';
  @Input({ required: true }) apiDocsUrl = '';

  constructor(private imageService: ImageService) {}

  ngOnInit() {
    this.sageLogo$ = this.imageService.getImage({
      objectKey: 'logo/sage-bionetworks-alt-white.svg',
    });
  }
}
