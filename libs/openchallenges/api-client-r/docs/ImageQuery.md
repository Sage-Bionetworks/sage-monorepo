# openapi::ImageQuery

An image query.

## Properties

| Name            | Type                                        | Description                         | Notes                                       |
| --------------- | ------------------------------------------- | ----------------------------------- | ------------------------------------------- |
| **objectKey**   | **character**                               | The unique identifier of the image. | [Pattern: ^[a-zA-Z0-9/_-]+.[a-zA-Z0-9/_-]+] |
| **height**      | [**ImageHeight**](ImageHeight.md)           |                                     | [optional] [Enum: ]                         |
| **aspectRatio** | [**ImageAspectRatio**](ImageAspectRatio.md) |                                     | [optional] [Enum: ]                         |
