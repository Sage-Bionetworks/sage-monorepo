import { Component, input, viewChild } from '@angular/core';
import { ButtonModule, ButtonProps } from 'primeng/button';
import { Tooltip, TooltipModule } from 'primeng/tooltip';
import { SvgIconComponent } from '../svg-icon/svg-icon.component';

@Component({
  selector: 'explorers-tooltip-button',
  imports: [Tooltip, TooltipModule, SvgIconComponent, ButtonModule],
  templateUrl: './tooltip-button.component.html',
  styleUrls: ['./tooltip-button.component.scss'],
})
export class TooltipButtonComponent {
  tooltip = viewChild(Tooltip);

  tooltipText = input.required<string>();
  tooltipPosition = input<string>('right');
  onClick = input.required<() => void>();

  buttonLabel = input<string>();
  buttonSvgIconConfig = input<Partial<SvgIconComponent>>();
  buttonAriaDescribedBy = input<string>();
  buttonProps = input<ButtonProps>();

  showTooltip() {
    this.tooltip()?.show();
  }

  hideTooltip() {
    this.tooltip()?.hide();
  }
}
