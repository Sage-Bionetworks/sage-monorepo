import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { Gene } from '@sagebionetworks/agora/api-client-angular';
import { ascending } from 'd3';

@Component({
  selector: 'agora-gene-hero',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './gene-hero.component.html',
  styleUrls: ['./gene-hero.component.scss'],
})
export class GeneHeroComponent implements OnInit {
  @Input() gene: Gene | undefined;

  alias = '';

  ngOnInit() {
    this.alias = this.getAlias();
  }

  showNominationsOrTEP() {
    if (!this.gene) return false;
    return this.gene.total_nominations || this.gene.is_adi || this.gene.is_tep;
  }

  getNominationText() {
    if (!this.gene) return '';
    let result = '';
    if (this.gene.total_nominations) {
      result += 'Nominated Target';
    }
    if (this.gene.is_adi || this.gene.is_tep) {
      result += this.gene.total_nominations ? ', ' : '';
      return (result += 'Selected for Target Enabling Resource Development');
    }
    return result;
  }

  getSummary(body = false): string {
    if (this.gene?.summary) {
      let finalString = '';
      const parenthesisArr = this.gene.summary.split(/\(([^)]+)\)/g);
      if (parenthesisArr.length) {
        parenthesisArr.forEach((p, i, a) => {
          // Add the parenthesis back
          let auxString = '';
          if (i > 0) {
            auxString += i % 2 === 1 ? '(' : ')';
          }
          if (i < a.length - 1) {
            // Replace brackets with a space except the last one
            finalString += auxString + p.replace(/\[[^)]*\]/g, ' ');
          } else {
            finalString += auxString + p;
          }
        });
      }
      if (!finalString) {
        finalString = this.gene.summary;
      }
      const bracketsArr = finalString.split(/\[([^)]+)\]/g);
      if (bracketsArr.length && bracketsArr.length > 1) {
        // We have brackets so get the description and ref back
        if (body) {
          // Replace the spaces before and where the brackets were
          // with nothing
          return bracketsArr[0].replace(/  {2}/g, '');
        } else {
          // Return the last bracket string
          if (bracketsArr[1].includes(',')) {
            bracketsArr[1] = bracketsArr[1].split(',')[0];
          }
          return bracketsArr[1];
        }
      } else {
        // We dont have brackets so just get the description back
        if (body) {
          return finalString;
        } else {
          return '';
        }
      }
    } else {
      // If we don't have a summary, return a placeholder description and an empty ref
      if (body) {
        return '';
      } else {
        return '';
      }
    }
  }

  getAlias(): string {
    if (this.gene?.alias && this.gene.alias.length > 0) {
      return this.gene.alias.join(', ');
    }
    return '';
  }

  getBiodomains(): string {
    if (!this.gene || !this.gene.bio_domains) return '';
    const biodomains = this.gene.bio_domains.gene_biodomains
      .filter((b) => b.pct_linking_terms > 0)
      .map((b) => b.biodomain)
      .sort(ascending);
    return biodomains.join(', ');
  }

  getEnsemblUrl() {
    if (!this.gene?.ensembl_info) return '';
    return this.gene?.ensembl_info.ensembl_permalink;
  }

  getPossibleReplacementsURL() {
    let url = 'https://useast.ensembl.org/Homo_sapiens/Gene/Idhistory?g=';
    return (url += this.gene?.ensembl_gene_id);
  }
}
