import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ModalLinkComponent, SvgIconComponent } from '@sagebionetworks/agora/ui';

@Component({
  selector: 'agora-nominated-targets',
  standalone: true,
  imports: [CommonModule, SvgIconComponent, ModalLinkComponent, RouterLink],
  templateUrl: './nominated-targets.component.html',
  styleUrls: ['./nominated-targets.component.scss'],
})
export class NominatedTargetsComponent {
  // genes: Gene[] = [];
  // searchTerm = '';
  // nominations: number[] = [];
  // columns: GeneTableColumn[] = [
  //   { field: 'hgnc_symbol', header: 'Gene Symbol', selected: true },
  //   { field: 'total_nominations', header: 'Nominations', selected: true },
  //   {
  //     field: 'initial_nomination_display_value',
  //     header: 'Year First Nominated',
  //     selected: true,
  //   },
  //   {
  //     field: 'teams_display_value',
  //     header: 'Nominating Teams',
  //     selected: true,
  //   },
  //   { field: 'study_display_value', header: 'Cohort Study', selected: true },
  //   {
  //     field: 'programs_display_value',
  //     header: 'Program',
  //     selected: false,
  //   },
  //   {
  //     field: 'input_data_display_value',
  //     header: 'Input Data',
  //     selected: false,
  //   },
  //   {
  //     field: 'pharos_class_display_value',
  //     header: 'Pharos Class',
  //     selected: false,
  //   },
  //   {
  //     field: 'sm_druggability_display_value',
  //     header: 'Small Molecule Druggability',
  //     selected: false,
  //   },
  //   {
  //     field: 'safety_rating_display_value',
  //     header: 'Safety Rating',
  //     selected: false,
  //   },
  //   {
  //     field: 'ab_modality_display_value',
  //     header: 'Antibody Modality',
  //     selected: false,
  //   },
  // ];
  // constructor(private apiService: ApiService) {}
  // ngOnInit() {
  //   this.apiService.getNominatedGenes().subscribe((response: GenesResponse) => {
  //     const genes = response.items;
  //     genes.forEach((de: Gene) => {
  //       let teamsArray: string[] = [];
  //       let studyArray: string[] = [];
  //       let programsArray: string[] = [];
  //       let inputDataArray: string[] = [];
  //       let initialNominationArray: number[] = [];
  //       if (de.total_nominations) {
  //         if (!this.nominations.includes(de.total_nominations)) {
  //           this.nominations.push(de.total_nominations);
  //           this.nominations.sort();
  //         }
  //       }
  //       // Handle TargetNomination fields
  //       // First map all entries nested in the data to a new array
  //       if (de.target_nominations?.length) {
  //         teamsArray = de.target_nominations.map((nt: TargetNomination) => nt.team);
  //         studyArray = this.removeNullAndEmptyStrings(
  //           de.target_nominations.map((nt: TargetNomination) => nt.study)
  //         );
  //         programsArray = de.target_nominations.map(
  //           (nt: TargetNomination) => nt.source
  //         );
  //         inputDataArray = de.target_nominations.map(
  //           (nt: TargetNomination) => nt.input_data
  //         );
  //         initialNominationArray = de.target_nominations
  //           .map((nt: TargetNomination) => nt.initial_nomination)
  //           .filter((item) => item !== undefined);
  //       }
  //       // Check if there are any strings with commas inside,
  //       // if there are separate those into new split strings
  //       teamsArray = this.commaFlattenArray(teamsArray);
  //       studyArray = this.commaFlattenArray(studyArray);
  //       programsArray = this.commaFlattenArray(programsArray);
  //       inputDataArray = this.commaFlattenArray(inputDataArray);
  //       // Populate targetNomination display fields
  //       de.teams_display_value =
  //         this.getCommaSeparatedStringOfUniqueSortedValues(teamsArray);
  //       de.study_display_value =
  //         this.getCommaSeparatedStringOfUniqueSortedValues(studyArray);
  //       de.programs_display_value =
  //         this.getCommaSeparatedStringOfUniqueSortedValues(programsArray);
  //       de.input_data_display_value =
  //         this.getCommaSeparatedStringOfUniqueSortedValues(inputDataArray);
  //       de.initial_nomination_display_value = initialNominationArray.length
  //         ? Math.min(...initialNominationArray)
  //         : undefined;
  //       // Populate Druggability display fields
  //       if (de.druggability && de.druggability.length) {
  //         de.pharos_class_display_value = de.druggability[0].pharos_class
  //           ? de.druggability[0].pharos_class
  //           : 'No value';
  //         de.sm_druggability_display_value =
  //           de.druggability[0].sm_druggability_bucket +
  //           ': ' +
  //           de.druggability[0].classification;
  //         de.safety_rating_display_value =
  //           de.druggability[0].safety_bucket +
  //           ': ' +
  //           de.druggability[0].safety_bucket_definition;
  //         de.ab_modality_display_value =
  //           de.druggability[0].abability_bucket +
  //           ': ' +
  //           de.druggability[0].abability_bucket_definition;
  //       } else {
  //         de.pharos_class_display_value = 'No value';
  //         de.sm_druggability_display_value = 'No value';
  //         de.safety_rating_display_value = 'No value';
  //         de.ab_modality_display_value = 'No value';
  //       }
  //     });
  //     this.genes = genes;
  //   });
  // }
  // removeNullAndEmptyStrings(items: (string | null)[]) {
  //   return items.filter((item) => Boolean(item)) as string[];
  // }
  // getUnique(value: string, index: number, self: any) {
  //   return self.indexOf(value) === index;
  // }
  // commaFlattenArray(array: string[]): string[] {
  //   const finalArray: string[] = [];
  //   array.forEach((t) => {
  //     const i = t.indexOf(', ');
  //     if (i > -1) {
  //       const tmpArray = t.split(', ');
  //       tmpArray.forEach((val) => finalArray.push(val));
  //     } else {
  //       finalArray.push(t);
  //     }
  //   });
  //   return finalArray;
  // }
  // getCommaSeparatedStringOfUniqueSortedValues(inputArray: string[]) {
  //   let display_value = '';
  //   if (inputArray.length) {
  //     display_value = inputArray
  //       .filter(this.getUnique)
  //       .sort((a: string, b: string) => a.localeCompare(b))
  //       .join(', ');
  //   }
  //   return display_value;
  // }
  // onSearch(event: any) {
  //   this.searchTerm = event.target.value || '';
  // }
}
