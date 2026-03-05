"""Custom FastAPI application that wraps the generated routers with JWT security.

This file is NOT generated and will not be overwritten by OpenAPI Generator.
It imports the generated routers and adds JWT validation as a dependency
on protected endpoints while leaving health-check endpoints public.
"""

from fastapi import Depends, FastAPI

from bixarena_ai_service.apis.battle_validation_api import (
    router as BattleValidationApiRouter,
)
from bixarena_ai_service.apis.health_check_api import router as HealthCheckApiRouter
from bixarena_ai_service.apis.prompt_validation_api import (
    router as PromptValidationApiRouter,
)
from bixarena_ai_service.security.jwt_validator import validate_jwt

app = FastAPI(
    title="BixArena AI Service",
    description="Advance bioinformatics by evaluating and ranking AI agents.",
    version="1.0.0",
)

# Health check is public (no JWT required)
app.include_router(HealthCheckApiRouter)

# Validation endpoints require a valid JWT
app.include_router(PromptValidationApiRouter, dependencies=[Depends(validate_jwt)])
app.include_router(BattleValidationApiRouter, dependencies=[Depends(validate_jwt)])
