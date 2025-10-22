-- Insert example prompt records
INSERT INTO api.example_prompt (id, question, source, active, created_at) VALUES
  ('01111111-1111-1111-1111-111111111111', 'Do mitochondria play a role in remodelling lace plant leaves during programmed cell death?', 'pubmedqa', TRUE, '2025-08-01 09:00:00+00'),
  ('02222222-2222-2222-2222-222222222222', 'Do mutations causing low HDL-C promote increased carotid intima-media thickness?', 'pubmedqa', TRUE, '2025-08-01 09:15:00+00'),
  ('03333333-3333-3333-3333-333333333333', '30-Day and 1-year mortality in emergency general surgery laparotomies: an area of concern and need for improvement?', 'pubmedqa', TRUE, '2025-08-01 09:30:00+00'),
  ('04444444-4444-4444-4444-444444444444', 'Differentiation of nonalcoholic from alcoholic steatohepatitis: are routine laboratory markers useful?', 'pubmedqa', TRUE, '2025-08-01 09:45:00+00'),
  ('05555555-5555-5555-5555-555555555555', 'Can tailored interventions increase mammography use among HMO women?', 'pubmedqa', TRUE, '2025-08-01 10:00:00+00'),
  ('06666666-6666-6666-6666-666666666666', 'Are the long-term results of the transanal pull-through equal to those of the transabdominal pull-through?', 'pubmedqa', TRUE, '2025-08-01 10:15:00+00'),
  ('07777777-7777-7777-7777-777777777777', 'Is there still a need for living-related liver transplantation in children?', 'pubmedqa', TRUE, '2025-08-01 10:30:00+00'),
  ('08888888-8888-8888-8888-888888888888', 'Therapeutic anticoagulation in the trauma patient: is it safe?', 'pubmedqa', TRUE, '2025-08-01 10:45:00+00'),
  ('09999999-9999-9999-9999-999999999999', 'Acute respiratory distress syndrome in children with malignancy--can we predict outcome?', 'pubmedqa', TRUE, '2025-08-01 11:00:00+00'),
  ('0aaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Double balloon enteroscopy: is it efficacious and safe in a community setting?', 'pubmedqa', TRUE, '2025-08-01 11:15:00+00'),
  ('0bbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Would you like a BigMac with extra pickles?', 'bixbench', FALSE, '2025-08-01 11:30:00+00');