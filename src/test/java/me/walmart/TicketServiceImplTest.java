package me.walmart;

import org.junit.Before;
import org.junit.Test;

public class TicketServiceImplTest {

  private SeatRepository tenSeatRepository;
  private TicketService tenSeatService;

  @Before
  public void runBeforeTestMethod() {
    this.tenSeatRepository = new SeatRepository(10);

    this.tenSeatService = new TicketServiceImpl(tenSeatRepository);

    tenSeatRepository.setHoldSeconds(60);
  }


  @Test
  public void numSeatsAvailable() {


  }

  @Test
  public void findAndHoldSeats() {


  }

  @Test
  public void reserveSeats() throws InterruptedException {

    for (int i = 0; i < 10; i++) {

      SeatHold hold = this.tenSeatService.findAndHoldSeats(1, "test1@gmail.com");

      if (hold.getId() % 2 == 0) {
        tenSeatService.reserveSeats(hold.getId(), "test1@gmail.com");
      }

    }

    Thread.sleep(2000);

    SeatHold hold = this.tenSeatService.findAndHoldSeats(5, "test2@gmail.com");
  }
}