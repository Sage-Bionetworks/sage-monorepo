import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { SafeUrl } from '@angular/platform-browser';
import { PathSanitizer } from '@sagebionetworks/agora/util';

@Component({
  selector: 'agora-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  backgroundBox1Path!: SafeUrl;
  backgroundBox2Path!: SafeUrl;
  backgroundBox3Path!: SafeUrl;

  geneComparisonIconPath!: SafeUrl;
  nominatedTargetsIconPath!: SafeUrl;
  arrowPath!: SafeUrl;

  constructor(private sanitizer: PathSanitizer) {
    this.loadBackgroundImages();
    this.loadIcons();
  }

  loadBackgroundImages() {
    this.backgroundBox1Path = this.sanitizer.sanitize('/agora-assets/images/background1.svg');
    this.backgroundBox2Path = this.sanitizer.sanitize('/agora-assets/images/background2.svg');
    this.backgroundBox3Path = this.sanitizer.sanitize('/agora-assets/images/background3.svg');
  }

  loadIcons() {
    this.geneComparisonIconPath = this.sanitizer.sanitize(
      '/agora-assets/images/gene-comparison-icon.svg',
    );
    this.nominatedTargetsIconPath = this.sanitizer.sanitize(
      '/agora-assets/images/nominated-targets-icon.svg',
    );
    this.arrowPath = this.sanitizer.sanitize('/agora-assets/images/card-arrow.svg');
  }
}
