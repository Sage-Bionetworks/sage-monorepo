import { CommonModule } from '@angular/common';
import { Component, input } from '@angular/core';

export type ContainerSize = 'sm' | 'sm-md' | 'md' | 'lg';

@Component({
  selector: 'explorers-section',
  imports: [CommonModule],
  templateUrl: './section.component.html',
  styleUrls: ['./section.component.scss'],
  standalone: true,
})
export class SectionComponent {
  containerSize = input<ContainerSize>('md');
}
