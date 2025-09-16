import { ResourceCardsComponent } from '@sagebionetworks/explorers/ui';
import { Model } from '@sagebionetworks/model-ad/api-client-angular';
import { modelMock } from '@sagebionetworks/model-ad/testing';
import { render, screen } from '@testing-library/angular';
import { ModelDetailsOmicsComponent } from './model-details-omics.component';

async function setup(model: Model = modelMock) {
  return render(ModelDetailsOmicsComponent, {
    imports: [ResourceCardsComponent],
    componentInputs: {
      model: model,
    },
  });
}

describe('ModelDetailsOmicsComponent', () => {
  const diseaseCorrelationPath = `comparison/correlation?name=${modelMock.name}`;
  const geneExpressionPath = `comparison/expression?name=${modelMock.name}`;

  it('should display section header', async () => {
    await setup();
    const sectionTitle = screen.getByText('Available Data');
    expect(sectionTitle).toBeInTheDocument();
  });

  it('should display gene expression resource card when available', async () => {
    await setup({ ...modelMock, gene_expression: geneExpressionPath, disease_correlation: null });
    expect(screen.getByText('Gene Expression')).toBeInTheDocument();
    expect(screen.queryByText('Disease Correlation')).not.toBeInTheDocument();
  });

  it('should display disease correlation resource card when available', async () => {
    await setup({
      ...modelMock,
      gene_expression: null,
      disease_correlation: diseaseCorrelationPath,
    });
    expect(screen.queryByText('Gene Expression')).not.toBeInTheDocument();
    expect(screen.getByText('Disease Correlation')).toBeInTheDocument();
  });

  it('should display both gene comparison and disease correlation when both cards available', async () => {
    await setup({
      ...modelMock,
      gene_expression: geneExpressionPath,
      disease_correlation: diseaseCorrelationPath,
    });
    expect(screen.getByText('Gene Expression')).toBeInTheDocument();
    expect(screen.getByText('Disease Correlation')).toBeInTheDocument();
  });
});
