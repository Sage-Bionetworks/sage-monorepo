import { CommonModule } from '@angular/common';
import { Component, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'agora-loading-icon',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './loading-icon.component.html',
  styleUrls: ['./loading-icon.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class LoadingIconComponent {}
