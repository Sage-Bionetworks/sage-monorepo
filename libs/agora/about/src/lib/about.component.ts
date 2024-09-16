import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { WikiComponent } from 'libs/agora/wiki/src/lib/wiki.component';

@Component({
  selector: 'agora-about',
  standalone: true,
  imports: [CommonModule, WikiComponent],
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.scss'],
})
export class AboutComponent {
  wikiId = '612058';
  className = 'about-page-content';
}
