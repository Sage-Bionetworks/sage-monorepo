-- Rename the model.license value 'commercial' to 'proprietary'.
--
-- 'commercial' is orthogonal to 'open-source' (e.g. Llama and Mistral are
-- open-source AND used commercially), so the original pair conflated two
-- dimensions. 'proprietary' cleanly opposes 'open-source' on the binary the
-- column actually expresses: are the model weights freely available, or
-- controlled by the originator? This matches the convention used by
-- Hugging Face, LMArena, and provider-side documentation.

ALTER TABLE api.model DROP CONSTRAINT model_license_check;

UPDATE api.model SET license = 'proprietary' WHERE license = 'commercial';

ALTER TABLE api.model
  ADD CONSTRAINT model_license_check
  CHECK (license IN ('open-source', 'proprietary'));
