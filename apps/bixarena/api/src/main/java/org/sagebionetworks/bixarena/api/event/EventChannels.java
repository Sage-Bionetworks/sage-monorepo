package org.sagebionetworks.bixarena.api.event;

/**
 * Constants for Redis Pub/Sub event channels used in BixArena.
 *
 * <p>These channels are used for cross-service communication via Redis Pub/Sub.
 */
public final class EventChannels {

  private EventChannels() {
    // Utility class, prevent instantiation
  }

  /**
   * Global event bus for BixArena cross-service communication.
   * Events published to this channel include:
   * - user.registered (from auth service)
   * - battle.created (from API service)
   * - battle.evaluated (from API service)
   */
  public static final String BIXARENA_EVENTS = "bixarena:events";
}
