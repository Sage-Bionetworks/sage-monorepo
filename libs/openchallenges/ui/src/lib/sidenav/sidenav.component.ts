import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatListModule } from '@angular/material/list';
import { NavbarSection } from '../navbar/navbar-section';
import { DiscordButtonComponent } from '../discord-button/discord-button.component';

@Component({
  selector: 'openchallenges-sidenav',
  standalone: true,
  imports: [CommonModule, RouterModule, MatListModule, DiscordButtonComponent],
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.scss'],
})
export class SidenavComponent {
  private _sections: { [key: string]: NavbarSection } = {};
  sectionsKeys: string[] = [];
  @Output() sidenavClose = new EventEmitter();

  @Input({ required: true })
  public set sections(s: { [key: string]: NavbarSection }) {
    this._sections = s;
    this.sectionsKeys = Object.keys(s);
  }

  public get sections(): { [key: string]: NavbarSection } {
    return this._sections;
  }

  public onSidenavClose = () => {
    this.sidenavClose.emit();
  };
}
