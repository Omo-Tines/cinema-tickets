package uk.gov.dwp.uc.pairtest.TicketTypes;

public interface Ticket {
    // get total price of tickets
    public int getTotalPrice();

    // get total seats/tickets
    public int getTotalTickets();

    // increase ticket Qantity
    public void increaseTicketQuantity(int quantity);

}
