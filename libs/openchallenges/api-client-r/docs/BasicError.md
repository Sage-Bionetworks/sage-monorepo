# openapi::BasicError

Problem details (tools.ietf.org/html/rfc7807)

## Properties

| Name       | Type          | Description                                                             | Notes      |
| ---------- | ------------- | ----------------------------------------------------------------------- | ---------- |
| **title**  | **character** | A human readable documentation for the problem type                     |
| **status** | **integer**   | The HTTP status code                                                    |
| **detail** | **character** | A human readable explanation specific to this occurrence of the problem | [optional] |
| **type**   | **character** | An absolute URI that identifies the problem type                        | [optional] |
