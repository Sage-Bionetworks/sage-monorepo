import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';

@Component({
  selector: 'openchallenges-discord-button',
  standalone: true,
  imports: [CommonModule, MatButtonModule],
  templateUrl: './discord-button.component.html',
  styleUrls: ['./discord-button.component.scss'],
})
export class DiscordButtonComponent {
  @Input({ required: false }) label = 'Join us on Discord';
  @Input({ required: false }) href = 'https://discord.gg/6PGt7nkcwG';
}
