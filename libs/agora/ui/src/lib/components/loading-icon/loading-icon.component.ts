// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

// -------------------------------------------------------------------------- //
// Component
// -------------------------------------------------------------------------- //
@Component({
  selector: 'agora-loading-icon',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './loading-icon.component.html',
  styleUrls: ['./loading-icon.component.scss'],
})
export class LoadingIconComponent {}
