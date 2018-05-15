import java.time.Instant;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

class SeatHold extends BitSet {

  /**
   * Instance Pool of Holds
   */
  private static List<SeatHold> holdList = new ArrayList<>();
  private static int nextId = 0;

  public static SeatHold request(int numSeats) {


    holdList.stream().filter(
        seatHold -> seatHold.getHoldTime().isAfter(Instant.now().minusSeconds(10))
    ).map(SeatHold::getHeldSeats).reduce();

  }

  private static int getNextId() {
    nextId += 1;
    return nextId;
  }

  private BitSet heldSeats;
  private Instant holdTime = Instant.now();
  private int id = getNextId();
  private int numSeats;

  private SeatHold(int numSeats, BitSet seats) {
    this.numSeats = numSeats;
    this.heldSeats = seats;
  }

  public BitSet or(SeatHold other) {

    BitSet seats = (BitSet) this.heldSeats.clone();
    seats.or(other.heldSeats);

    return seats;

  }


  public int getId() {
    return id;
  }

  public BitSet getHeldSeats() {
    return heldSeats;
  }


  public Instant getHoldTime() {
    return holdTime;
  }


}
