import { Component, Input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'openchallenges-button-github',
  standalone: true,
  imports: [MatButtonModule],
  templateUrl: './button-github.component.html',
  styleUrls: ['./button-github.component.scss'],
})
export class ButtonGithubComponent {
  @Input({ required: true }) label = 'GitHub';
  @Input({ required: true }) href = 'https://github.com';
}
