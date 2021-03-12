from sqlalchemy.dialects.postgresql import ENUM

direction_enum = ENUM('Amp', 'Del', name='direction_enum')

ethnicity_enum = ENUM('not hispanic or latino',
                      'hispanic or latino', name='ethnicity_enum')

gender_enum = ENUM('male', 'female', name='gender_enum')

race_enum = ENUM('white', 'asian', 'american indian or alaska native',
                 'black or african american', 'native hawaiian or other pacific islander', name='race_enum')

status_enum = ENUM('Wt', 'Mut', name='status_enum')

unit_enum = ENUM('Count', 'Fraction', 'Per Megabase',
                 'Score', 'Year', name='unit_enum')

qtl_enum = ENUM('sQTL', 'eQTL')

ecaviar_pp_enum = ENUM('C1', 'C2')

coloc_plot_type_enum = ENUM('3 Level Plot', 'Expanded Region')
