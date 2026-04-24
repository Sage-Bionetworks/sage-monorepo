# CategorizationStatus

Terminal outcome of a categorization run. - `matched`: the classifier picked at least one category. - `abstained`: the classifier ran successfully but declared no category fits. The row is persisted for audit; the effective categorization is not auto-promoted. - `failed`: the classifier could not run (AI service or LLM error). The row is persisted so admins can identify and retry; the effective categorization is not auto-promoted.

## Enum

- `MATCHED` (value: `'matched'`)

- `ABSTAINED` (value: `'abstained'`)

- `FAILED` (value: `'failed'`)

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
