import { ResourceCardsComponent } from '@sagebionetworks/explorers/ui';
import { MouseModel } from '@sagebionetworks/model-ad/api-client';
import { mouseModelMock } from '@sagebionetworks/model-ad/testing';
import { render, screen } from '@testing-library/angular';
import { ModelDetailsOmicsComponent } from './model-details-omics.component';

async function setup(model: MouseModel = mouseModelMock) {
  return render(ModelDetailsOmicsComponent, {
    imports: [ResourceCardsComponent],
    componentInputs: {
      model: model,
    },
  });
}

describe('ModelDetailsOmicsComponent', () => {
  const diseaseCorrelationPath = `comparison/correlation?name=${mouseModelMock.name}`;
  const transcriptomicsPath = `comparison/expression?name=${mouseModelMock.name}`;
  const proteomicsPath = `comparison/expression?categories=PROTEIN&name=${mouseModelMock.name}`;

  it('should display section header', async () => {
    await setup();
    const sectionTitle = screen.getByText('Available Data');
    expect(sectionTitle).toBeInTheDocument();
  });

  it('should display transcriptomics resource card when available', async () => {
    await setup({
      ...mouseModelMock,
      transcriptomics: transcriptomicsPath,
      disease_correlation: null,
      proteomics: null,
    });
    expect(screen.getByText('Transcriptomics')).toBeInTheDocument();
    expect(screen.queryByText('Disease Correlation')).not.toBeInTheDocument();
    expect(screen.queryByText('Proteomics')).not.toBeInTheDocument();
  });

  it('should display disease correlation resource card when available', async () => {
    await setup({
      ...mouseModelMock,
      transcriptomics: null,
      disease_correlation: diseaseCorrelationPath,
      proteomics: null,
    });
    expect(screen.queryByText('Transcriptomics')).not.toBeInTheDocument();
    expect(screen.getByText('Disease Correlation')).toBeInTheDocument();
    expect(screen.queryByText('Proteomics')).not.toBeInTheDocument();
  });

  it('should display proteomics resource card when available', async () => {
    await setup({
      ...mouseModelMock,
      transcriptomics: null,
      disease_correlation: null,
      proteomics: proteomicsPath,
    });
    expect(screen.queryByText('Transcriptomics')).not.toBeInTheDocument();
    expect(screen.queryByText('Disease Correlation')).not.toBeInTheDocument();
    expect(screen.getByText('Proteomics')).toBeInTheDocument();
  });

  it('should display all resource cards when transcriptomics, disease correlation, and proteomics available', async () => {
    await setup({
      ...mouseModelMock,
      transcriptomics: transcriptomicsPath,
      disease_correlation: diseaseCorrelationPath,
      proteomics: proteomicsPath,
    });
    expect(screen.getByText('Transcriptomics')).toBeInTheDocument();
    expect(screen.getByText('Disease Correlation')).toBeInTheDocument();
    expect(screen.getByText('Proteomics')).toBeInTheDocument();
  });
});
