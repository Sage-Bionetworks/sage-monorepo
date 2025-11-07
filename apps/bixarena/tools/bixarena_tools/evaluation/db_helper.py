"""
Database helper for BixArena PostgreSQL operations.

Provides connection management and data access functions for battles and evaluations.
"""

import os
from contextlib import contextmanager
from typing import Any

import psycopg
from psycopg.rows import dict_row


class DatabaseConfig:
    """Database connection configuration."""

    def __init__(
        self,
        host: str | None = None,
        port: int | None = None,
        database: str | None = None,
        user: str | None = None,
        password: str | None = None,
    ):
        # Validate required fields
        missing_fields = []
        if not host:
            missing_fields.append("POSTGRES_HOST")
        if not port:
            missing_fields.append("POSTGRES_PORT")
        if not database:
            missing_fields.append("POSTGRES_DB")
        if not user:
            missing_fields.append("POSTGRES_USER")
        if not password:
            missing_fields.append("POSTGRES_PASSWORD")

        if missing_fields:
            raise ValueError(
                f"Missing required environment variables: {', '.join(missing_fields)}"
            )

        self.host = host
        self.port = port
        self.database = database
        self.user = user
        self.password = password

    @classmethod
    def from_env(cls) -> "DatabaseConfig":
        """Create config from environment variables."""
        return cls(
            host=os.getenv("POSTGRES_HOST", "localhost"),
            port=int(os.getenv("POSTGRES_PORT", "21000")),
            database=os.getenv("POSTGRES_DB", "bixarena"),
            user=os.getenv("POSTGRES_USER", "postgres"),
            password=os.getenv("POSTGRES_PASSWORD", "changeme"),
        )


@contextmanager
def get_db_connection(config: DatabaseConfig | None = None):
    """
    Get a database connection context manager.

    Args:
        config: Database configuration (uses default if None)

    Yields:
        Database connection
    """
    if config is None:
        config = DatabaseConfig()

    conn = psycopg.connect(
        host=config.host,
        port=config.port,
        dbname=config.database,
        user=config.user,
        password=config.password,
        row_factory=dict_row,
    )
    try:
        yield conn
        conn.commit()
    except Exception:
        conn.rollback()
        raise
    finally:
        conn.close()


def fetch_active_models(conn) -> list[dict[str, Any]]:
    """
    Fetch all active models from the database.

    Args:
        conn: Database connection

    Returns:
        List of model dictionaries with all model fields
    """
    with conn.cursor() as cur:
        cur.execute(
            """
            SELECT *
            FROM api.model
            WHERE active = true
            ORDER BY name
            """
        )
        return cur.fetchall()


def fetch_battle_evaluations(conn) -> list[dict[str, Any]]:
    """
    Fetch battle evaluations with model names for ranking.

    Args:
        conn: Database connection

    Returns:
        List of dicts with model1_name, model2_name, outcome
    """
    with conn.cursor() as cur:
        cur.execute(
            """
            SELECT m1.name as model1_name,
                   m2.name as model2_name,
                   eval.outcome
            FROM api.battle_evaluation eval
            JOIN api.battle b ON eval.battle_id = b.id
            JOIN api.model m1 ON b.model1_id = m1.id
            JOIN api.model m2 ON b.model2_id = m2.id
            """
        )
        return cur.fetchall()


def insert_battle_evaluation(conn, battle_id: str, outcome: str) -> dict[str, Any]:
    """
    Insert a battle evaluation into the database.

    Args:
        conn: Database connection
        battle_id: UUID of the battle
        outcome: Outcome value ('model1', 'model2', or 'tie')

    Returns:
        Dictionary with the inserted evaluation data

    Raises:
        ValueError: If outcome is not valid
    """
    if outcome not in ("model1", "model2", "tie"):
        raise ValueError(f"Invalid outcome: {outcome}. Must be model1, model2, or tie")

    with conn.cursor() as cur:
        cur.execute(
            """
            INSERT INTO api.battle_evaluation (battle_id, outcome)
            VALUES (%s, %s)
            ON CONFLICT (battle_id) DO UPDATE
            SET outcome = EXCLUDED.outcome
            RETURNING id, battle_id, outcome, created_at
            """,
            (battle_id, outcome),
        )
        result = cur.fetchone()
        return result if result else {}


def insert_battle_evaluations_batch(conn, evaluations: list[dict[str, str]]) -> int:
    """
    Insert multiple battle evaluations in a batch.

    Args:
        conn: Database connection
        evaluations: List of dicts with 'battle_id' and 'outcome' keys

    Returns:
        Number of evaluations inserted

    Raises:
        ValueError: If any outcome is not valid
    """
    if not evaluations:
        return 0

    # Validate all outcomes first
    for eval_data in evaluations:
        if eval_data["outcome"] not in ("model1", "model2", "tie"):
            raise ValueError(
                f"Invalid outcome: {eval_data['outcome']}. "
                "Must be model1, model2, or tie"
            )

    with conn.cursor() as cur:
        # Use executemany for batch insert
        cur.executemany(
            """
            INSERT INTO api.battle_evaluation (battle_id, outcome)
            VALUES (%s, %s)
            ON CONFLICT (battle_id) DO UPDATE
            SET outcome = EXCLUDED.outcome
            """,
            [(e["battle_id"], e["outcome"]) for e in evaluations],
        )

        return cur.rowcount


def insert_battles_batch(conn, battles: list[dict[str, Any]]) -> int:
    """
    Insert multiple battles in a batch.

    Args:
        conn: Database connection
        battles: List of battle dictionaries with required fields

    Returns:
        Number of battles inserted
    """
    if not battles:
        return 0

    with conn.cursor() as cur:
        cur.executemany(
            """
            INSERT INTO api.battle (
                id, title, user_id, model1_id, model2_id, created_at, ended_at
            )
            VALUES (%s, %s, %s, %s, %s, %s, %s)
            """,
            [
                (
                    b["id"],
                    b.get("title"),
                    b["user_id"],
                    b["model1_id"],
                    b["model2_id"],
                    b.get("created_at"),
                    b.get("ended_at"),
                )
                for b in battles
            ],
        )

        return cur.rowcount


def fetch_leaderboards(conn) -> list[dict[str, Any]]:
    """
    Fetch all leaderboards from the database.

    Args:
        conn: Database connection

    Returns:
        List of leaderboard dictionaries
    """
    with conn.cursor() as cur:
        cur.execute(
            """
            SELECT *
            FROM api.leaderboard
            ORDER BY slug
            """
        )
        return cur.fetchall()


def fetch_leaderboard_ids(conn) -> list[str]:
    """
    Fetch all available leaderboard IDs from the database.

    Note: In this context, "ID" actually refers to the slug such as 'overall'.

    Args:
        conn: Database connection

    Returns:
        List of leaderboard slugs (human-readable IDs)
    """
    with conn.cursor() as cur:
        cur.execute(
            """
            SELECT slug
            FROM api.leaderboard
            ORDER BY slug
            """
        )
        return [row["slug"] for row in cur.fetchall()]


def insert_leaderboard_snapshot(
    conn, leaderboard_id: str, snapshot_identifier: str, description: str = None
) -> str:
    """
    Insert a new leaderboard snapshot.

    Args:
        conn: Database connection
        leaderboard_id: UUID of the leaderboard
        snapshot_identifier: Unique identifier for this snapshot
        description: Optional description

    Returns:
        UUID of the created snapshot as a string
    """
    with conn.cursor() as cur:
        cur.execute(
            """
            INSERT INTO api.leaderboard_snapshot
                (leaderboard_id, snapshot_identifier, description)
            VALUES (%(leaderboard_id)s, %(snapshot_identifier)s, %(description)s)
            RETURNING id
            """,
            {
                "leaderboard_id": leaderboard_id,
                "snapshot_identifier": snapshot_identifier,
                "description": description,
            },
        )
        snapshot_id = cur.fetchone()["id"]
        conn.commit()
        return str(snapshot_id)


def insert_leaderboard_entries(
    conn, leaderboard_id: str, snapshot_id: str, entries: list[dict[str, Any]]
) -> int:
    """
    Insert leaderboard entries for a snapshot.

    Args:
        conn: Database connection
        leaderboard_id: UUID of the leaderboard
        snapshot_id: UUID of the snapshot
        entries: List of entry dicts with keys: modelId, btScore, voteCount,
                 rank, bootstrapQ025, bootstrapQ975

    Returns:
        Number of entries inserted
    """
    if not entries:
        return 0

    with conn.cursor() as cur:
        # Prepare batch insert data
        insert_data = [
            {
                "leaderboard_id": leaderboard_id,
                "model_id": entry["modelId"],
                "snapshot_id": snapshot_id,
                "bt_score": entry["btScore"],
                "vote_count": entry["voteCount"],
                "rank": entry["rank"],
                "bootstrap_q025": entry["bootstrapQ025"],
                "bootstrap_q975": entry["bootstrapQ975"],
            }
            for entry in entries
        ]

        # Batch insert
        cur.executemany(
            """
            INSERT INTO api.leaderboard_entry
                (leaderboard_id, model_id, snapshot_id, bt_score,
                 vote_count, rank, bootstrap_q025, bootstrap_q975)
            VALUES (%(leaderboard_id)s, %(model_id)s, %(snapshot_id)s,
                    %(bt_score)s, %(vote_count)s, %(rank)s,
                    %(bootstrap_q025)s, %(bootstrap_q975)s)
            """,
            insert_data,
        )

        row_count = cur.rowcount
        conn.commit()
        return row_count
