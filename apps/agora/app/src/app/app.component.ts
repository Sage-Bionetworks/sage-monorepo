import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import {
  DataVersion,
  DataVersionService,
} from '@sagebionetworks/agora/api-client-angular';
import { Observable } from 'rxjs';
import { NxWelcomeComponent } from './nx-welcome.component';

@Component({
  standalone: true,
  imports: [CommonModule, NxWelcomeComponent, RouterModule],
  selector: 'agora-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit {
  title = 'agora-app';

  dataVersion$!: Observable<DataVersion>;

  constructor(private dataVersionService: DataVersionService) {}

  ngOnInit(): void {
    this.dataVersion$ = this.dataVersionService.getDataVersion();
  }
}
