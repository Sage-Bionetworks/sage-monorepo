import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NavigationLink } from '@sagebionetworks/explorers/models';

@Component({
  selector: 'explorers-footer',
  imports: [CommonModule, RouterModule],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent {
  @Input() footerLogoPath = '';
  @Input() footerLinks: NavigationLink[] = [];
  @Input() siteVersion = '';
  @Input() dataVersion = '';
}
