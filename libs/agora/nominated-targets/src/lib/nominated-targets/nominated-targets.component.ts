import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';
import { Gene, GeneService, TargetNomination } from '@sagebionetworks/agora/api-client';
import { DEFAULT_HERO_BACKGROUND_IMAGE_PATH } from '@sagebionetworks/agora/config';
import { GeneTableComponent } from '@sagebionetworks/agora/genes';
import { GeneTableColumn } from '@sagebionetworks/agora/models';
import { LoggerService } from '@sagebionetworks/explorers/services';
import { ModalLinkComponent, SvgIconComponent } from '@sagebionetworks/explorers/util';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'agora-nominated-targets',
  imports: [
    RouterLink,
    SvgIconComponent,
    ModalLinkComponent,
    GeneTableComponent,
    ButtonModule,
    FontAwesomeModule,
  ],
  templateUrl: './nominated-targets.component.html',
  styleUrls: ['./nominated-targets.component.scss'],
})
export class NominatedTargetsComponent implements OnInit {
  private readonly destroyRef = inject(DestroyRef);
  private readonly logger = inject(LoggerService);

  apiService = inject(GeneService);

  readonly heroBackgroundImagePath = DEFAULT_HERO_BACKGROUND_IMAGE_PATH;
  magnifyingIcon = faMagnifyingGlass;
  genes: Gene[] = [];
  searchTerm = '';
  nominations: number[] = [];
  columns: GeneTableColumn[] = [
    { field: 'hgnc_symbol', header: 'Gene Symbol', selected: true },
    { field: 'total_nominations', header: 'Nominations', selected: true },
    {
      field: 'initial_nomination_display_value',
      header: 'Year First Nominated',
      selected: true,
    },
    {
      field: 'teams_display_value',
      header: 'Nominating Teams',
      selected: true,
    },
    { field: 'study_display_value', header: 'Cohort Study', selected: true },
    {
      field: 'programs_display_value',
      header: 'Program',
      selected: false,
    },
    {
      field: 'input_data_display_value',
      header: 'Input Data',
      selected: false,
    },
    {
      field: 'pharos_class_display_value',
      header: 'Pharos Class',
      selected: false,
    },
  ];

  ngOnInit() {
    this.logger.log('NominatedTargetsComponent: Loading nominated genes');

    this.apiService
      .getNominatedGenes()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((response) => {
        if (!response.items) return;
        const genes = response.items;

        genes.forEach((de: Gene) => {
          let teamsArray: string[] = [];
          let studyArray: string[] = [];
          let programsArray: string[] = [];
          let inputDataArray: string[] = [];
          let initialNominationArray: number[] = [];

          if (de.total_nominations) {
            if (!this.nominations.includes(de.total_nominations)) {
              this.nominations.push(de.total_nominations);
              this.nominations.sort();
            }
          }

          // Handle TargetNomination fields
          // First map all entries nested in the data to a new array
          if (de.target_nominations?.length) {
            teamsArray = de.target_nominations.map((nt: TargetNomination) => nt.team);
            studyArray = this.removeNullAndEmptyStrings(
              de.target_nominations.map((nt: TargetNomination) => nt.study),
            );
            programsArray = de.target_nominations.map((nt: TargetNomination) => nt.source);
            inputDataArray = de.target_nominations.map((nt: TargetNomination) => nt.input_data);

            initialNominationArray = de.target_nominations
              .map((nt: TargetNomination) => nt.initial_nomination)
              .filter((item) => item !== undefined);
          }

          // Check if there are any strings with commas inside,
          // if there are separate those into new split strings
          teamsArray = this.commaFlattenArray(teamsArray);
          studyArray = this.commaFlattenArray(studyArray);
          programsArray = this.commaFlattenArray(programsArray);
          inputDataArray = this.commaFlattenArray(inputDataArray);

          // Populate targetNomination display fields
          de.teams_display_value = this.getCommaSeparatedStringOfUniqueSortedValues(teamsArray);
          de.study_display_value = this.getCommaSeparatedStringOfUniqueSortedValues(studyArray);
          de.programs_display_value =
            this.getCommaSeparatedStringOfUniqueSortedValues(programsArray);
          de.input_data_display_value =
            this.getCommaSeparatedStringOfUniqueSortedValues(inputDataArray);

          de.initial_nomination_display_value = initialNominationArray.length
            ? Math.min(...initialNominationArray)
            : undefined;

          // Populate Druggability display fields
          if (de.druggability) {
            de.pharos_class_display_value = de.druggability.pharos_class;
          }
        });

        this.genes = genes;
      });
  }

  removeNullAndEmptyStrings(items: (string | null)[]) {
    return items.filter((item) => Boolean(item)) as string[];
  }

  getUnique(value: string, index: number, self: any) {
    return self.indexOf(value) === index;
  }

  commaFlattenArray(array: string[]): string[] {
    const finalArray: string[] = [];
    array.forEach((t) => {
      const i = t.indexOf(', ');
      if (i > -1) {
        const tmpArray = t.split(', ');
        tmpArray.forEach((val) => finalArray.push(val));
      } else {
        finalArray.push(t);
      }
    });
    return finalArray;
  }

  getCommaSeparatedStringOfUniqueSortedValues(inputArray: string[]) {
    let display_value = '';
    if (inputArray.length) {
      display_value = inputArray
        .filter(this.getUnique)
        .sort((a: string, b: string) => a.localeCompare(b))
        .join(', ');
    }
    return display_value;
  }

  onSearch(event: any) {
    this.searchTerm = event.target.value || '';
  }
}
