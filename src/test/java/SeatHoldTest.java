import org.junit.Test;

import static org.junit.Assert.*;

public class SeatHoldTest {

  private SeatHold holdOneSeat = new SeatHold(1);

  @Test
  public void getId() {
    assertEquals(holdOneSeat.getId(), 1);
  }
}