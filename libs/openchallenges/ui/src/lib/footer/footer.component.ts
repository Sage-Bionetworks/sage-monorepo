import { Component, inject, Input, OnInit } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { Image, ImageService } from '@sagebionetworks/openchallenges/api-client-angular';
import { Observable } from 'rxjs';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'openchallenges-footer',
  imports: [AsyncPipe, RouterModule],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent implements OnInit {
  private readonly imageService = inject(ImageService);

  public sageLogo$: Observable<Image> | undefined;

  @Input({ required: true }) appVersion = '';
  @Input({ required: true }) dataUpdatedOn = '';
  @Input({ required: true }) privacyPolicyUrl = '';
  @Input({ required: true }) termsOfUseUrl = '';
  @Input({ required: true }) apiDocsUrl = '';

  ngOnInit() {
    this.sageLogo$ = this.imageService.getImage({
      objectKey: 'logo/SageBionetworks-Logo-FullColor-WhiteText.svg',
    });
  }
}
