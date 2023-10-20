import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';

@Component({
  selector: 'openchallenges-button-github',
  standalone: true,
  imports: [CommonModule, MatButtonModule],
  templateUrl: './button-github.component.html',
  styleUrls: ['./button-github.component.scss'],
})
export class ButtonGithubComponent {
  @Input({ required: true }) label = 'GitHub';
  @Input({ required: true }) href = 'https://github.com';
}
