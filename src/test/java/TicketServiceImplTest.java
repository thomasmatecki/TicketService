import org.junit.Test;

import static org.junit.Assert.*;

public class TicketServiceImplTest {

  private TicketService ticketService = new TicketServiceImpl(10);

  @Test
  public void numSeatsAvailable() {
    assertEquals(ticketService.numSeatsAvailable(), 10);
  }

  @Test
  public void findAndHoldSeats() {
  }

  @Test
  public void reserveSeats() {
  }
}