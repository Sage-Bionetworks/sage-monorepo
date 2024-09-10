# openapi::Organization

An organization

## Properties

| Name               | Type          | Description                              | Notes                                                                     |
| ------------------ | ------------- | ---------------------------------------- | ------------------------------------------------------------------------- |
| **id**             | **integer**   | The unique identifier of an organization |
| **name**           | **character** |                                          |
| **email**          | **character** | An email address.                        |
| **login**          | **character** | The login of an organization             | [Pattern: ^[a-z0-9]+(?:-[a-z0-9]+)\*$] [Max. length: 64] [Min. length: 2] |
| **description**    | **character** |                                          |
| **avatarKey**      | **character** |                                          | [optional]                                                                |
| **websiteUrl**     | **character** |                                          |
| **challengeCount** | **integer**   |                                          | [optional] [Min: 0]                                                       |
| **createdAt**      | **character** |                                          |
| **updatedAt**      | **character** |                                          |
| **acronym**        | **character** |                                          | [optional]                                                                |
