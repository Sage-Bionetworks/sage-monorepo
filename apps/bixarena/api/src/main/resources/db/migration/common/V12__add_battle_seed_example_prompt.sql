-- Stable link from a battle to the curated example_prompt it was started
-- from. Null for free-form battles (user typed their own question). Used by
-- the trending-prompts endpoint to count usage without text comparison.

ALTER TABLE api.battle
  ADD COLUMN seed_example_prompt_id UUID NULL
  REFERENCES api.example_prompt(id) ON DELETE SET NULL;

-- Partial index: only non-null FKs participate in trending queries.
CREATE INDEX idx_api_battle_seed_example_prompt_id
  ON api.battle(seed_example_prompt_id)
  WHERE seed_example_prompt_id IS NOT NULL;

-- One-time historical backfill: link battles whose round-1 prompt text
-- matches any example_prompt's question (after LOWER+TRIM normalization),
-- regardless of the prompt's current `active` flag.
UPDATE api.battle b
SET seed_example_prompt_id = sub.ep_id
FROM (
  SELECT br.battle_id, ep.id AS ep_id
  FROM api.battle_round br
  JOIN api.message m ON m.id = br.prompt_message_id
  JOIN api.example_prompt ep
    ON LOWER(TRIM(ep.question)) = LOWER(TRIM(m.content))
  WHERE br.round_number = 1
) sub
WHERE b.id = sub.battle_id;
