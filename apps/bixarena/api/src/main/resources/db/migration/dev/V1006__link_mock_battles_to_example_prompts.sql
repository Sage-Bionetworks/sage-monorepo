-- ============================================================================
-- DEV ENVIRONMENT ONLY: Link Mock Battles to Curated Example Prompts
-- ============================================================================
-- This migration:
--   1. Seeds categorizations for 10 curated example prompts so the home page
--      and battle helix render category chips.
--   2. Links a weighted slice of biomedical-validated, completed mock battles
--      to those prompts so the trending strip on the home page has a usable
--      distribution. Roughly 193 battles get linked, leaving the rest as
--      free-form (example_prompt_id IS NULL).
--
-- Without this, the home trending section hides because no battles carry a
-- non-null example_prompt_id, and category chips never render.
-- ============================================================================

-- Step 1: Insert one categorization per prompt and point the prompt's
-- effective_categorization_id at it.
WITH assigned (prompt_id, category) AS (
  VALUES
    ('01111111-1111-1111-1111-111111111111'::uuid, 'cancer-biology'),
    ('02222222-2222-2222-2222-222222222222'::uuid, 'cancer-biology'),
    ('03333333-3333-3333-3333-333333333333'::uuid, 'bioinformatics'),
    ('04444444-4444-4444-4444-444444444444'::uuid, 'physiology'),
    ('05555555-5555-5555-5555-555555555555'::uuid, 'physiology'),
    ('06666666-6666-6666-6666-666666666666'::uuid, 'cancer-biology'),
    ('07777777-7777-7777-7777-777777777777'::uuid, 'pharmacology-and-toxicology'),
    ('08888888-8888-8888-8888-888888888888'::uuid, 'molecular-biology'),
    ('09999999-9999-9999-9999-999999999999'::uuid, 'bioinformatics'),
    ('0aaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'::uuid, 'pathology')
),
inserted_cat AS (
  INSERT INTO api.example_prompt_categorization (prompt_id, method, status, category)
  SELECT prompt_id, 'manual-seed', 'matched', category FROM assigned
  RETURNING id, prompt_id
)
UPDATE api.example_prompt ep
SET effective_categorization_id = inserted_cat.id
FROM inserted_cat
WHERE ep.id = inserted_cat.prompt_id;


-- Step 2: Link mock battles to prompts with a weighted distribution.
-- Cumulative weights run 50 → 193. Battles ranked beyond 193 stay free-form.
WITH ranked_battles AS (
  SELECT b.id, ROW_NUMBER() OVER (ORDER BY b.id) AS rn
  FROM api.battle b
  JOIN api.battle_validation bv
    ON bv.id = b.effective_validation_id AND bv.is_biomedical = true
  WHERE b.ended_at IS NOT NULL
),
prompt_buckets (prompt_id, lo, hi) AS (
  VALUES
    ('01111111-1111-1111-1111-111111111111'::uuid,   0,  50),
    ('02222222-2222-2222-2222-222222222222'::uuid,  50,  80),
    ('03333333-3333-3333-3333-333333333333'::uuid,  80, 105),
    ('04444444-4444-4444-4444-444444444444'::uuid, 105, 125),
    ('05555555-5555-5555-5555-555555555555'::uuid, 125, 143),
    ('06666666-6666-6666-6666-666666666666'::uuid, 143, 158),
    ('07777777-7777-7777-7777-777777777777'::uuid, 158, 170),
    ('08888888-8888-8888-8888-888888888888'::uuid, 170, 180),
    ('09999999-9999-9999-9999-999999999999'::uuid, 180, 188),
    ('0aaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'::uuid, 188, 193)
)
UPDATE api.battle b
SET example_prompt_id = pb.prompt_id
FROM ranked_battles rb
JOIN prompt_buckets pb ON rb.rn > pb.lo AND rb.rn <= pb.hi
WHERE b.id = rb.id;
