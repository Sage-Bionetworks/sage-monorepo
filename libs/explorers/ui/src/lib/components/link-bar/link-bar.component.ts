import { Component, inject, input, ViewEncapsulation } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'explorers-link-bar',
  imports: [ButtonModule, RouterLink, SvgIconComponent],
  templateUrl: './link-bar.component.html',
  styleUrls: ['./link-bar.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class LinkBarComponent {
  link = input.required<string>();
  altText = input<string | undefined>();

  private router = inject(Router);

  navigate(): void {
    this.router.navigate([this.link()]);
  }
}
