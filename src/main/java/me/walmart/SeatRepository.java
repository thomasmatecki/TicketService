package me.walmart;

import java.time.Instant;
import java.util.*;

/**
 * The seat repository maintains an append only log of all Holds that
 * have been requested and the state of those seats that have been
 * reserved. The underlying data structure for both of those is a
 * BitSet.
 */
public class SeatRepository {

  private int seatCapacity;
  private int holdSeconds = 10;

  private Map<Integer, BitSeatHold> indexedHolds = new TreeMap<>();
  private BitSet reservedSeats = new BitSet(seatCapacity);


  SeatRepository(int seatCapacity) {
    this.seatCapacity = seatCapacity;
  }

  public void setHoldSeconds(int holdSeconds) {
    this.holdSeconds = holdSeconds;
  }

  private static int nextId = 0;

  private static int getNextHoldId() {
    nextId += 1;
    return nextId;
  }

  class BitSeatHold implements me.walmart.SeatHold {

    private BitSet heldSeats;
    private Instant holdTime = Instant.now();
    private int id = getNextHoldId();

    BitSeatHold(BitSet seats) {
      this.heldSeats = seats;
    }

    public int getId() {
      return id;
    }

    @Override
    public int getSeatCount() {
      return heldSeats.cardinality();
    }

    public BitSet getHeldSeats() {
      return heldSeats;
    }

    public boolean isOlderThan(int seconds) {

      return this.holdTime.isBefore(
          Instant.now().minusSeconds(seconds)
      );
    }
  }


  /**
   * Accumulates all holds after a certain time(`holdSeconds` in the past)
   * and forms the union of occupied seats from those holds and all seats
   * that are currently reserved.
   *
   * @return A Bitset in which bits representing occupied seats are set.
   */
  public BitSet getOccupiedSeats() {

    return indexedHolds.values().stream().filter(

        seatHold -> !seatHold.isOlderThan(this.holdSeconds)

    ).map(BitSeatHold::getHeldSeats).reduce((BitSet) reservedSeats.clone(), (accum, heldSeats) -> {
      accum.or(heldSeats);
      return accum;
    });

  }


  public int availableSeatCount() {

    return seatCapacity - indexedHolds.values().stream().filter(
        seatHold -> !seatHold.isOlderThan(this.holdSeconds)
    ).map(BitSeatHold::getSeatCount).reduce(
        reservedSeats.cardinality(), (accum, heldSeatsCount) -> heldSeatsCount + accum
    );
  }

  private class NoMoreSeatsException extends Throwable {


  }

  /**
   * Recursively builds a bitset representing a collection of available
   * seats. This algorithm make attempts to generate a bitset that has
   * the least possible number of non-adjacent seats(smallest partition).
   * <p>
   * There are some optimizations that can be done here.
   *
   * @param occupiedSeats All occupied seats(at this point in time)
   * @param numSeats      The number of seat being requested.
   * @return A bitset representing a set of seats that can construct a `BitSeatHold`
   */
  private BitSet getSeats(BitSet occupiedSeats, int numSeats) throws NoMoreSeatsException {

    int i = getOccupiedSeats().nextClearBit(0);


    while (i + numSeats <= seatCapacity) {
      // Get the bits representing the next `numSeats`
      BitSet range = occupiedSeats.get(i, i + numSeats);
      if (range.isEmpty()) {

        // The candidate `range` has enough seats; use that.
        BitSet heldSeats = new BitSet(seatCapacity);
        heldSeats.flip(i, i + numSeats);

        return heldSeats;

      } else {
        // Some bit in `range` is set; Continue from the bit after that.
        i += range.nextSetBit(0) + 1;
      }
    }

    if (numSeats == 1) {
      throw new NoMoreSeatsException();
    }

    // No contiguous set of seats has been found; Partition in half and
    // recur for both partitions.
    BitSet heldSeats = this.getSeats(occupiedSeats, numSeats / 2 + numSeats % 2);
    occupiedSeats.and(heldSeats);
    heldSeats.and(this.getSeats(occupiedSeats, numSeats / 2));

    return heldSeats;
  }

  /**
   * @param numSeats The number of seats to be reserved.
   * @return A seat hold for `numSeats`
   * @throws NotEnoughSeatsException If the number of seats requested exceeds
   *                                 the number available
   */
  public me.walmart.SeatHold requestHold(int numSeats) throws NotEnoughSeatsException {

    BitSet occupiedSeats = getOccupiedSeats();
    BitSet heldSeats = null;

    if (numSeats > seatCapacity) {
      throw new NotEnoughSeatsException(numSeats);
    }

    try {
      heldSeats = this.getSeats(occupiedSeats, numSeats);
    } catch (NoMoreSeatsException e) {
      throw new NotEnoughSeatsException(numSeats);
    }

    BitSeatHold hold = new BitSeatHold(heldSeats);
    indexedHolds.put(hold.getId(), hold);

    return hold;
  }

  public BitSeatHold reserve(int seatHoldId) throws HoldNotFoundException, HoldExpiredException {

    BitSeatHold seatHold = indexedHolds.remove(seatHoldId);

    if (seatHold == null) {
      throw new HoldNotFoundException();
    }

    if (seatHold.isOlderThan(this.holdSeconds)) {
      // Either try to create a new reservation, or just throw.
      throw new HoldExpiredException();
    }

    this.reservedSeats.or(seatHold.getHeldSeats());

    return seatHold;
  }
}
