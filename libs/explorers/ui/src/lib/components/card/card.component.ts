import { CommonModule } from '@angular/common';
import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SearchResultsList } from '@sagebionetworks/explorers/models';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';
import { Observable } from 'rxjs';
import { SearchInputComponent } from '../search-input/search-input.component';
import { SvgImageComponent } from '../svg-image/svg-image.component';

@Component({
  selector: 'explorers-card',
  imports: [CommonModule, SvgImageComponent, RouterLink, SearchInputComponent, SvgIconComponent],
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss'],
})
export class CardComponent {
  title = input.required<string>();
  description = input.required<string>();
  imagePath = input.required<string>();
  imageAltText = input.required<string>();

  routerLink = input<string | undefined>();

  searchPlaceholder = input<string | undefined>();
  navigateToResult = input<(id: string) => void>();
  getSearchResultsList = input<(query: string) => Observable<SearchResultsList>>();
  checkQueryForErrors = input<(query: string) => string>(); // empty string if no error

  secondaryColor = 'inherit';

  constructor() {
    this.secondaryColor = getComputedStyle(document.documentElement).getPropertyValue(
      '--color-secondary',
    );
  }
}
