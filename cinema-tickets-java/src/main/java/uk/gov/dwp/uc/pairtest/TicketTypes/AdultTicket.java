package uk.gov.dwp.uc.pairtest.TicketTypes;

// Adult ticket class
public class AdultTicket implements Ticket {
    private static final int price = 25;
    private int noOfTickets;

    public AdultTicket(int noOfTickets) {
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
