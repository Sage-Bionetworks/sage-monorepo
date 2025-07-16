import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'decodeGreekEntity',
  standalone: true,
})
export class DecodeGreekEntityPipe implements PipeTransform {
  private readonly greekToUnicodeMap: { [key: string]: string } = {
    '&Alpha;': '\u0391',
    '&alpha;': '\u03B1',
    '&Beta;': '\u0392',
    '&beta;': '\u03B2',
    '&Gamma;': '\u0393',
    '&gamma;': '\u03B3',
    '&Delta;': '\u0394',
    '&delta;': '\u03B4',
    '&Tau;': '\u03A4',
    '&tau;': '\u03C4',
  };

  transform(value: string | null | undefined): string {
    if (!value) {
      return '';
    }

    let clean = value;
    for (const [entity, unicode] of Object.entries(this.greekToUnicodeMap)) {
      clean = clean.replace(new RegExp(entity, 'g'), unicode);
    }

    return clean;
  }
}
