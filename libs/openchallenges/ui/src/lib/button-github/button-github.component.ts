import { Component, Input } from '@angular/core';

@Component({
  selector: 'openchallenges-button-github',
  templateUrl: './button-github.component.html',
  styleUrls: ['./button-github.component.scss'],
})
export class ButtonGithubComponent {
  @Input() label = 'GitHub';
  @Input() href = 'https://github.com';
}
