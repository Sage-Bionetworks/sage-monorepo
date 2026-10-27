import { DefaultUrlSerializer, PRIMARY_OUTLET, UrlTree } from '@angular/router';
import { CustomUrlSerializer } from './app.custom-uri-serializer';

const MODEL_WITH_PARENTHESES = 'AD Diagnosis x Sex (females only)';
const ENCODED_MODEL_WITH_PARENTHESES = encodeURIComponent(MODEL_WITH_PARENTHESES);

function getPrimaryPath(tree: UrlTree): string {
  return (
    tree.root.children[PRIMARY_OUTLET]?.segments.map((segment) => segment.path).join('/') ?? ''
  );
}

describe('CustomUrlSerializer', () => {
  const serializer = new CustomUrlSerializer();

  it('should unwrap the legacy gene details outlet', () => {
    const tree = serializer.parse('/genes/(genes-router:gene-details/ENSG00000135940)');
    expect(getPrimaryPath(tree)).toBe('genes/genes-router:gene-details/ENSG00000135940');
  });

  it('should unwrap the legacy genes list outlet', () => {
    const tree = serializer.parse('/genes/(genes-router:genes-list)');
    expect(getPrimaryPath(tree)).toBe('genes/genes-router:genes-list');
  });

  it('should unwrap a legacy outlet without changing the query string', () => {
    const tree = serializer.parse(
      `/genes/(genes-router:gene-details/ENSG00000135940)?model=${ENCODED_MODEL_WITH_PARENTHESES}`,
    );
    expect(getPrimaryPath(tree)).toBe('genes/genes-router:gene-details/ENSG00000135940');
    expect(tree.queryParams['model']).toBe(MODEL_WITH_PARENTHESES);
  });

  it('should keep parentheses in query parameter values', () => {
    const tree = serializer.parse(
      `/genes/ENSG00000178209/evidence/rna?model=${ENCODED_MODEL_WITH_PARENTHESES}`,
    );
    expect(getPrimaryPath(tree)).toBe('genes/ENSG00000178209/evidence/rna');
    expect(tree.queryParams['model']).toBe(MODEL_WITH_PARENTHESES);
  });

  it('should keep parentheses in hash fragments', () => {
    const tree = serializer.parse('/genes/ENSG00000178209/evidence/rna#section-(one)');
    expect(tree.fragment).toBe('section-(one)');
  });

  it('should not rewrite parentheses outside the legacy outlet', () => {
    const url = '/genes/foo(bar)';
    expect(serializer.serialize(serializer.parse(url))).toBe(
      new DefaultUrlSerializer().serialize(new DefaultUrlSerializer().parse(url)),
    );
  });
});
