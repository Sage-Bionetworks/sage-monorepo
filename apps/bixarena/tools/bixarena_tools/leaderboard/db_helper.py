"""
Database helper for BixArena PostgreSQL operations.

Provides connection management and data access functions for battles and evaluations.
"""

import os
from contextlib import contextmanager
from typing import Any

import psycopg
from psycopg.rows import dict_row


@contextmanager
def get_db_connection():
    """
    Get a database connection context manager using environment variables.

    Yields:
        Database connection

    Raises:
        ValueError: If required environment variables are missing
    """
    # Get environment variables
    host = os.getenv("POSTGRES_HOST")
    port = os.getenv("POSTGRES_PORT")
    database = os.getenv("POSTGRES_DB")
    user = os.getenv("POSTGRES_USER")
    password = os.getenv("POSTGRES_PASSWORD")

    # Validate required fields
    missing = [
        var
        for var, val in {
            "POSTGRES_HOST": host,
            "POSTGRES_PORT": port,
            "POSTGRES_DB": database,
            "POSTGRES_USER": user,
            "POSTGRES_PASSWORD": password,
        }.items()
        if not val
    ]

    if missing:
        raise ValueError(
            f"Missing required environment variables: {', '.join(missing)}"
        )

    conn = psycopg.connect(
        host=host,
        port=int(port),  # type: ignore[arg-type]
        dbname=database,
        user=user,
        password=password,
        row_factory=dict_row,  # type: ignore[arg-type]
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
        leaderboard_id: UUID string
        snapshot_identifier: unique identifier for this snapshot
        description: optional description

    Returns:
        UUID string of the created snapshot
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


def update_leaderboard_snapshot(
    conn, snapshot_identifier: str, **updates
) -> dict | None:
    """
    Update properties of a leaderboard snapshot.

    Args:
        conn: Database connection
        snapshot_identifier: Snapshot identifier to update
        **updates: Fields to update (e.g., visibility='public', description='...')

    Returns:
        Updated snapshot dict if found, None otherwise
    """
    if not updates:
        raise ValueError("No fields provided to update")

    # Build SET clause dynamically
    set_clauses = [f"{field} = %({field})s" for field in updates]
    set_clause = ", ".join(set_clauses)

    with conn.cursor() as cur:
        # Update the snapshot
        cur.execute(
            f"""
            UPDATE api.leaderboard_snapshot
            SET {set_clause}
            WHERE snapshot_identifier = %(snapshot_identifier)s
            RETURNING id
            """,
            {"snapshot_identifier": snapshot_identifier, **updates},
        )
        result = cur.fetchone()

        if not result:
            return None

        # Fetch and return the updated snapshot with additional details
        cur.execute(
            """
            SELECT
                s.id,
                s.leaderboard_id,
                s.snapshot_identifier,
                s.description,
                s.visibility,
                s.created_at,
                l.name as leaderboard_name,
                l.slug as leaderboard_slug,
                COUNT(e.id) as entry_count
            FROM api.leaderboard_snapshot s
            JOIN api.leaderboard l ON s.leaderboard_id = l.id
            LEFT JOIN api.leaderboard_entry e ON s.id = e.snapshot_id
            WHERE s.id = %(id)s
            GROUP BY s.id, s.leaderboard_id, s.snapshot_identifier,
                     s.description, s.visibility, s.created_at,
                     l.name, l.slug
            """,
            {"id": result["id"]},
        )
        snapshot = cur.fetchone()
        conn.commit()
        return snapshot


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
