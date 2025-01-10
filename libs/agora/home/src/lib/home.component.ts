import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { GeneSearchComponent } from '@sagebionetworks/agora/genes';
import { SvgImageComponent } from '@sagebionetworks/agora/ui';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'agora-home',
  imports: [CommonModule, GeneSearchComponent, SvgImageComponent, RouterLink],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {}
