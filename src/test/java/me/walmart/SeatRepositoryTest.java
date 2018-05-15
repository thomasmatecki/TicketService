package me.walmart;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SeatRepositoryTest {

  private SeatRepository tenSeatRepository;

  @Before
  public void runBeforeTestMethod() {
    tenSeatRepository = new SeatRepository(10);
  }

  @Test
  public void getOccupiedSeats() throws NotEnoughSeatsException {

    assertTrue(tenSeatRepository.getOccupiedSeats().isEmpty());

    SeatHold firstFiveSeats = tenSeatRepository.requestHold(5);

    assertEquals(
        firstFiveSeats.getSeatCount(),
        5
    );

    SeatHold secondFiveSeats = tenSeatRepository.requestHold(5);

    assertEquals(
        tenSeatRepository.getOccupiedSeats().cardinality(),
        10
    );

  }

  @Test
  public void requestHold() {

    try {
      tenSeatRepository.requestHold(11);
      fail();
    } catch (NotEnoughSeatsException ignored) {

    }

    try {
      SeatHold firstFiveSeats = tenSeatRepository.requestHold(5);
      SeatHold secondFiveSeats = tenSeatRepository.requestHold(5);

      tenSeatRepository.requestHold(1);
      fail();

    } catch (NotEnoughSeatsException ignored) {

    }

  }

  @Test
  public void availableSeatCount() throws NotEnoughSeatsException {

    assertEquals(tenSeatRepository.availableSeatCount(), 10);

    SeatHold firstFiveSeats = tenSeatRepository.requestHold(5);


    assertEquals(
        5,
        tenSeatRepository.availableSeatCount()
    );

    SeatHold secondFiveSeats = tenSeatRepository.requestHold(5);

    assertEquals(
        0,
        tenSeatRepository.availableSeatCount()
    );
  }
}