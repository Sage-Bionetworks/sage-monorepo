import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

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

  constructor(private sanitizer: DomSanitizer) {
    this.loadBackgroundImages();
    this.loadIcons();
  }

  loadBackgroundImages() {
    this.backgroundBox1Path = this.sanitize('/agora-assets/images/background1.svg');
    this.backgroundBox2Path = this.sanitize('/agora-assets/images/background2.svg');
    this.backgroundBox3Path = this.sanitize('/agora-assets/images/background3.svg');
  }

  loadIcons() {
    this.geneComparisonIconPath = this.sanitize('/agora-assets/images/gene-comparison-icon.svg');
    this.nominatedTargetsIconPath = this.sanitize(
      '/agora-assets/images/nominated-targets-icon.svg',
    );
    this.arrowPath = this.sanitize('/agora-assets/images/card-arrow.svg');
  }

  sanitize(path: string) {
    return this.sanitizer.bypassSecurityTrustUrl(path);
  }
}
