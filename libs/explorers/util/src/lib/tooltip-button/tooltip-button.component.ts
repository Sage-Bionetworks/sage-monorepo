import { Component, input, viewChild } from '@angular/core';
import { Tooltip, TooltipModule } from 'primeng/tooltip';
import { SvgIconComponent } from '../svg-icon/svg-icon.component';

@Component({
  selector: 'explorers-tooltip-button',
  imports: [Tooltip, TooltipModule, SvgIconComponent],
  templateUrl: './tooltip-button.component.html',
  styleUrls: ['./tooltip-button.component.scss'],
})
export class TooltipButtonComponent {
  tooltip = viewChild(Tooltip);

  tooltipText = input.required<string>();
  onClick = input.required<() => void>();

  buttonLabel = input<string>();
  buttonSvgIconConfig = input<Partial<SvgIconComponent>>();
  buttonAriaDescribedBy = input<string>();
  buttonAriaLabel = input<string>();

  showTooltip() {
    this.tooltip()?.show();
  }

  hideTooltip() {
    this.tooltip()?.hide();
  }
}
