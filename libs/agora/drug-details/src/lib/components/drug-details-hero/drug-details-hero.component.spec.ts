import { drugMock } from '@sagebionetworks/agora/testing';
import { render, screen } from '@testing-library/angular';
import { DrugDetailsHeroComponent } from './drug-details-hero.component';

async function setup() {
  return render(DrugDetailsHeroComponent, {
    imports: [],
    componentInputs: {
      drug: drugMock,
    },
  });
}

describe('DrugDetailsHeroComponent', () => {
  it('should display drug name', async () => {
    await setup();
    expect(
      screen.getByRole('heading', { level: 1, name: drugMock.common_name }),
    ).toBeInTheDocument();
  });

  it('should display chembl id', async () => {
    await setup();
    expect(screen.getByText(`ChEMBL ID: ${drugMock.chembl_id}`)).toBeInTheDocument();
  });

  it('should display drug bank id if it exists', async () => {
    await setup();
    expect(screen.getByText(`DrugBank ID: ${drugMock.drug_bank_id}`)).toBeInTheDocument();
  });

  it('should not display drug bank id if it does not exist', async () => {
    const drugWithoutDrugBankId = { ...drugMock, drug_bank_id: undefined };
    await render(DrugDetailsHeroComponent, {
      imports: [],
      componentInputs: {
        drug: drugWithoutDrugBankId,
      },
    });
    expect(screen.queryByText(/DrugBank ID:/)).not.toBeInTheDocument();
  });
});
