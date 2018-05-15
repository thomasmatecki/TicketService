
package me.walmart;

import java.util.HashMap;
import java.util.Map;

public class TicketServiceImpl implements TicketService {

  private SeatRepository seatRepository;

  private Map<Integer, String> holdIdentities = new HashMap<>();

  TicketServiceImpl(SeatRepository seatRepository) {

    this.seatRepository = seatRepository;

  }

  public int numSeatsAvailable() {
    return seatRepository.availableSeatCount();
  }

  public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {

    try {
      SeatHold hold = seatRepository.requestHold(numSeats);
      holdIdentities.put(hold.getId(), customerEmail);

      return hold;
    } catch (NotEnoughSeatsException nns) {
      System.out.println(nns.getMessage());
      return null;
    }
  }

  public String reserveSeats(int seatHoldId, String customerEmail) {

    if (!customerEmail.equals(holdIdentities.get(seatHoldId))) {
      return String.format("Invalid Email for Hold Number: %05d", seatHoldId);
    }

    try {
      SeatHold hold = seatRepository.reserve(seatHoldId);


      return String.format("Seat reserved! Confirmation Number: %05d", hold.getId());

    } catch (HoldNotFoundException | HoldExpiredException e) {

      return e.getMessage();

    }
  }
}
