import { Component, input, model, ViewEncapsulation } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SelectButtonModule } from 'primeng/selectbutton';
import { SvgImageComponent } from '../svg-image/svg-image.component';

export interface ToggleCardOption {
  label: string;
  value: string;
  imagePath?: string;
  imageAltText?: string;
}

@Component({
  selector: 'explorers-toggle-card',
  imports: [FormsModule, SelectButtonModule, SvgImageComponent],
  templateUrl: './toggle-card.component.html',
  styleUrls: ['./toggle-card.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ToggleCardComponent {
  options = input.required<ToggleCardOption[]>();
  value = model<string>();
  ariaLabel = input<string>();
}
