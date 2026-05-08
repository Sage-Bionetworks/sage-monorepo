import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SimpleTableCell, SimpleTableColumn } from '@sagebionetworks/explorers/models';
import { TooltipModule } from 'primeng/tooltip';
import { SvgImageComponent } from '../svg-image/svg-image.component';

@Component({
  selector: 'explorers-simple-table',
  imports: [RouterLink, TooltipModule, SvgImageComponent],
  templateUrl: './simple-table.component.html',
  styleUrls: ['./simple-table.component.scss'],
})
export class SimpleTableComponent {
  columns = input<SimpleTableColumn[]>();
  rows = input.required<SimpleTableCell[][]>();
  columnGap = input<string>();
}
