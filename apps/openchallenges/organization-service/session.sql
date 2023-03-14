-- @block list organizations

SELECT * FROM organization_service.organization;

-- @block list challenge contributions

SELECT * FROM organization_service.challenge_contribution WHERE role='sponsor';
