package uk.gov.dwp.uc.pairtest.TicketTypes;

public class ChildTicket implements Ticket {
    private static final int price = 15;
    private int noOfTickets;

    public ChildTicket(int noOfTickets) {
        this.noOfTickets = noOfTickets;
    }

    @Override
    // get total price of tickets
    public int getTotalPrice() {
        return price * noOfTickets;
    }

    @Override
    // get total seats of tickets
    public int getTotalTickets() {
        return noOfTickets;
    }

    @Override
    // increase ticket quantity
    public void increaseTicketQuantity(int quantity) {
        noOfTickets += quantity;
    }

}
