import { Component, OnInit } from '@angular/core';
import {
  Dataset,
  StorageService,
} from '@sagebionetworks/schematic/api-client-angular';
import { map, Observable } from 'rxjs';

@Component({
  selector: 'sagebionetworks-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {
  title = 'schematic-app';

  datasets$!: Observable<Dataset[]>;
  columnNames: string[] = ['name'];

  constructor(private storageService: StorageService) {}

  ngOnInit(): void {
    this.datasets$ = this.storageService
      .listStorageProjectDatasets('syn26251192')
      .pipe(map((page) => page.datasets));
  }
}
