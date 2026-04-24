import { Drug } from '@sagebionetworks/agora/api-client';
import { drugMock } from '@sagebionetworks/agora/testing';
import { render, screen } from '@testing-library/angular';
import { DrugDetailsNominationDetailsComponent } from './drug-details-nomination-details.component';

async function setup(drugOverrides: Partial<Drug> = {}) {
  return render(DrugDetailsNominationDetailsComponent, {
    componentInputs: {
      drug: { ...drugMock, ...drugOverrides },
    },
  });
}

describe('DrugDetailsNominationDetailsComponent', () => {
  it('should display the evidence heading with drug name', async () => {
    await setup();
    expect(
      screen.getByText(`Evidence Supporting the Nomination of ${drugMock.common_name}`),
    ).toBeInTheDocument();
  });

  it('should display introductory text', async () => {
    await setup();
    expect(
      screen.getByText('This drug has been nominated as a potential therapeutic for AD.'),
    ).toBeInTheDocument();
  });

  it('should display program name', async () => {
    await setup();
    expect(screen.getAllByText('ACTDRx AD').length).toBeGreaterThanOrEqual(1);
  });

  it('should display contact PI', async () => {
    await setup();
    expect(screen.getAllByText('Contact PI: Marina Sirota').length).toBeGreaterThanOrEqual(1);
  });

  it('should display nomination label without combined drug when combined_with_common_name is null', async () => {
    await setup();
    expect(screen.getByText(`Why was ${drugMock.common_name} nominated?`)).toBeInTheDocument();
  });

  it('should display nomination label with combined drug when combined_with_common_name is set', async () => {
    await setup();
    expect(screen.getByText(/Why was Letrozole \+.*Irinotecan nominated\?/)).toBeInTheDocument();
  });

  it('should display evidence text', async () => {
    await setup();
    expect(screen.getByText(drugMock.drug_nominations[0].evidence as string)).toBeInTheDocument();
  });

  it('should display data used', async () => {
    await setup();
    expect(screen.getAllByText('Data used').length).toBeGreaterThanOrEqual(1);
    expect(
      screen.getAllByText(drugMock.drug_nominations[0].data_used as string).length,
    ).toBeGreaterThanOrEqual(1);
  });

  it('should display mechanism of action in AD', async () => {
    await setup();
    expect(screen.getAllByText('Mechanism of action in AD').length).toBeGreaterThanOrEqual(1);
  });

  it('should display initial date of nomination', async () => {
    await setup();
    expect(screen.getAllByText('Initial date of nomination').length).toBeGreaterThanOrEqual(1);
    expect(screen.getAllByText('2025').length).toBeGreaterThanOrEqual(1);
  });

  it('should display planned validation', async () => {
    await setup();
    expect(screen.getAllByText('Planned validation').length).toBeGreaterThanOrEqual(1);
  });

  it('should display validation results', async () => {
    await setup();
    expect(screen.getAllByText('Validation results').length).toBeGreaterThanOrEqual(1);
  });

  it('should display funding', async () => {
    await setup();
    expect(screen.getAllByText('Funding').length).toBeGreaterThanOrEqual(1);
    expect(
      screen.getAllByText(drugMock.drug_nominations[0].grant_number as string).length,
    ).toBeGreaterThanOrEqual(1);
  });

  it('should not display additional evidence when null', async () => {
    await setup();
    expect(screen.queryByText('Additional evidence')).not.toBeInTheDocument();
  });

  it('should display additional evidence when present', async () => {
    await setup({
      drug_nominations: [
        { ...drugMock.drug_nominations[0], additional_evidence: 'Some extra evidence' },
      ],
    });
    expect(screen.getByText('Additional evidence')).toBeInTheDocument();
    expect(screen.getByText('Some extra evidence')).toBeInTheDocument();
  });

  it('should display related publication as a link that opens in a new tab when reference is present', async () => {
    await setup();
    expect(screen.getAllByText('Related publication').length).toBeGreaterThanOrEqual(1);
    const reference = drugMock.drug_nominations[0].reference as string;
    const links = screen.getAllByRole('link', { name: reference });
    expect(links.length).toBeGreaterThanOrEqual(1);
    expect(links[0]).toHaveAttribute('href', reference);
    expect(links[0]).toHaveAttribute('target', '_blank');
    expect(links[0]).toHaveAttribute('rel', 'noopener noreferrer');
  });

  it('should not display related publication when reference is absent', async () => {
    await setup({
      drug_nominations: [{ ...drugMock.drug_nominations[0], reference: '' }],
    });
    expect(screen.queryByText('Related publication')).not.toBeInTheDocument();
  });
});
