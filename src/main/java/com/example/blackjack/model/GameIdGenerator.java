package com.example.blackjack.model;

import java.util.UUID;

import org.springframework.stereotype.Component;

/** Generates identifiers for games. */
@Component
class GameIdGenerator {
   /**
    * Returns a new identifier.
    *
    * @return a {@code String} representation of a type 4 (pseudo randomly generated) UUID. e.g. {@code d8558043-3cbd-4edb-9f86-75ec5a32047f}
    */
   String generate() {
      return UUID.randomUUID().toString();
   }
}
