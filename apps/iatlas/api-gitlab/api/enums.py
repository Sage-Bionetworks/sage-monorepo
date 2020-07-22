from sqlalchemy.dialects.postgresql import ENUM

direction_enum = ENUM('Amp', 'Del', name='direction_enum')

status_enum = ENUM('Wt', 'Mut', name='status_enum')

unit_enum = ENUM('Count', 'Fraction', 'Per Megabase',
                 'Score', 'Year', name='unit_enum')
