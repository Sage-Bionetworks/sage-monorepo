from sqlalchemy.dialects.postgresql import ENUM

unit_enum = ENUM('Count', 'Fraction', 'Per Megabase',
                 'Score', 'Year', name='unit_enum')
