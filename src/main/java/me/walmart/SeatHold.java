package me.walmart;


public interface SeatHold {

  public int getId();

  public int getSeatCount();

  public boolean isOlderThan(int seconds);
}
