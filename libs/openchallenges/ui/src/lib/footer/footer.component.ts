import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  Image,
  ImageService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
import { Observable } from 'rxjs';

@Component({
  selector: 'openchallenges-footer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent {
  @Input() version = 'x.y.z';

  public logo$: Observable<Image> | undefined;

  constructor(
    private readonly configService: ConfigService,
    private imageService: ImageService
  ) {}

  ngOnInit() {
    this.logo$ = this.imageService.getImage({
      objectKey: 'openchallenges-white.svg',
    });
  }
}
