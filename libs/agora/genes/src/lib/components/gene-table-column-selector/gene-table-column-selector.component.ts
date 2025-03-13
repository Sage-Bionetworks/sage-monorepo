import { Component, EventEmitter, Input, Output } from '@angular/core';
import { TooltipModule } from 'primeng/tooltip';
import { GeneTableColumn } from '@sagebionetworks/agora/models';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { PopoverModule } from 'primeng/popover';

@Component({
  selector: 'agora-gene-table-column-selector',
  imports: [TooltipModule, FontAwesomeModule, PopoverModule],
  templateUrl: './gene-table-column-selector.component.html',
  styleUrls: ['./gene-table-column-selector.component.scss'],
})
export class GeneTableColumnSelectorComponent {
  @Input() columns: GeneTableColumn[] = [];
  @Output() columnChange = new EventEmitter<GeneTableColumn[]>();

  toggleColumn(column: GeneTableColumn) {
    column.selected = !column.selected;
    this.columnChange.emit([...this.columns]);
  }
}
