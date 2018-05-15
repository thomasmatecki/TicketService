import java.time.Instant;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.function.BinaryOperator;

public class TicketServiceImpl implements TicketService {

  private int numSeatsAvailable = 374;




  /**
   * @param numSeats
   */
  public TicketServiceImpl(int numSeats) {
    this.numSeatsAvailable = numSeats;
  }

  /**
   * @return
   */
  public int numSeatsAvailable() {
    return this.numSeatsAvailable;
  }

  /**
   * @param numSeats      the number of seats to find and hold
   * @param customerEmail unique identifier for the customer
   * @return
   */
  public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {

    numSeatsAvailable -= numSeats;


    return new SeatHold(numSeats);

  }

  /**
   * @param seatHoldId    the seat hold identifier
   * @param customerEmail the email address of the customer to which the
   *                      seat hold is assigned
   * @return
   */
  public String reserveSeats(int seatHoldId, String customerEmail) {
    return null;
  }
}
