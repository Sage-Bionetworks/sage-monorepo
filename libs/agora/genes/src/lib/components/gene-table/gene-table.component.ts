import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Dataversion, DataversionService } from '@sagebionetworks/agora/api-client-angular';
import { Observable } from 'rxjs';
import { SafeUrl } from '@angular/platform-browser';
import { PathSanitizer } from '@sagebionetworks/agora/util';
import { ConfigService } from '@sagebionetworks/agora/config';
// import { NavigationLink } from '../../models/navigation-link';

@Component({
  selector: 'agora-gene-table',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './gene-table.component.html',
  styleUrls: ['./gene-table.component.scss'],
})
export class GeneTableComponent {
  // configService = inject(ConfigService);
  // dataVersionService = inject(DataversionService);
  // sanitizer = inject(PathSanitizer);
  // footerLogoPath!: SafeUrl;
  // dataVersion$!: Observable<Dataversion>;
}
