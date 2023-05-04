import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  Image,
  ImageService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { Observable } from 'rxjs';

@Component({
  selector: 'openchallenges-footer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent implements OnInit {
  @Input() version = 'x.y.z';

  public logo$: Observable<Image> | undefined;

  constructor(private imageService: ImageService) {}

  ngOnInit() {
    this.logo$ = this.imageService.getImage({
      objectKey: 'openchallenges-white.svg',
    });
  }
}
