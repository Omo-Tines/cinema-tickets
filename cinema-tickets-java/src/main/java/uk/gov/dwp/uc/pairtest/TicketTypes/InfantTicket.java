package uk.gov.dwp.uc.pairtest.TicketTypes;

public class InfantTicket implements Ticket {
    private static final int price = 0;
    private int noOfTickets;

    public InfantTicket(int noOfTickets) {
        this.noOfTickets = noOfTickets;
    }

    @Override
    // get total price of tickets
    public int getTotalPrice() {
        return price * noOfTickets; // infant tickets are free
    }

    @Override
    // get total seats of tickets
    public int getTotalTickets() {
        return noOfTickets; // infant tickets do not take up a seat
    }

    @Override
    // increase ticket quantity
    public void increaseTicketQuantity(int quantity) {
        noOfTickets += quantity;
    }
}
