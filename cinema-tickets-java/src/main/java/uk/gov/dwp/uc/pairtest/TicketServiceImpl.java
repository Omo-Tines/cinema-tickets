package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.TicketTypes.*;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import java.util.Arrays;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     *
     * Business Constraints
     * Ticket Types and Prices:
     * Infant tickets are free (price = £0) and do not get a seat.
     * Child tickets cost £15 and receive a seat.
     * Adult tickets cost £25 and receive a seat.
     * 
     * Ticket Purchase Constraints:
     * A maximum of 25 tickets can be purchased at a time.
     * Infant tickets & Child tickets cannot be purchased without at least one Adult
     * ticket.
     * 
     * Ticket Validations:
     * Child and Infant tickets must always be accompanied by at least one Adult
     * ticket.
     * The total number of tickets in a single purchase cannot exceed 25 tickets.
     */
    private TicketPaymentService pService;
    private SeatReservationService seatRes;

    public TicketServiceImpl(TicketPaymentService pService, SeatReservationService seatRes)
            throws InvalidPurchaseException {
        this.pService = pService;
        this.seatRes = seatRes;
    }

    private static final int maxTicket = 25;
    AdultTicket adultTicket = new AdultTicket(0);
    ChildTicket childTicket = new ChildTicket(0);
    InfantTicket infantTicket = new InfantTicket(0);

    // total price of tickets
    int totalPrice;
    // total seats of tickets
    int totalSeats;

    @Override
    // purchase tickets
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)
            throws InvalidPurchaseException {
        // check if account id is valid
        if (accountId == null || accountId <= 0) {
            throw new InvalidPurchaseException("ID must be above zero (0) and not null");
        }
        // check if ticket requests are valid
        if (ticketTypeRequests == null || ticketTypeRequests.length == 0) {
            throw new InvalidPurchaseException("Ticket requests cannot be null or empty");
        }

        // use a stream to validate each ticket request
        Arrays.stream(ticketTypeRequests)
                .forEach(this::validTicket);

        // check if the purchase is valid
        int[] result = checkBusinessRules(accountId);
        totalPrice = result[0]; // total price of tickets
        totalSeats = result[1]; // total seats of tickets

        // Process payment and seat reservation
        pService.makePayment(accountId, totalPrice); // make payment
        seatRes.reserveSeat(accountId, totalSeats); // reserve seats
    }

    private void validTicket(TicketTypeRequest req) {
        if (req == null || req.getNoOfTickets() < 0) {
            throw new InvalidPurchaseException("Ticket type or number of tickets is invalid");
        }
        switch (req.getTicketType()) {
            case ADULT:
                adultTicket.increaseTicketQuantity(req.getNoOfTickets());
                break;
            case CHILD:
                childTicket.increaseTicketQuantity(req.getNoOfTickets());
                break;
            case INFANT:
                infantTicket.increaseTicketQuantity(req.getNoOfTickets());
                break;
        }

    }

    // check if business rules are valid
    private int[] checkBusinessRules(Long accountId) throws InvalidPurchaseException {
        final int noOfinfant = infantTicket.getTotalTickets();
        final int noOfchild = childTicket.getTotalTickets();
        final int noOfadult = adultTicket.getTotalTickets();

        // Calculate total tickets and seats
        final int totalTicket = noOfinfant + noOfchild + noOfadult;
        totalPrice = adultTicket.getTotalPrice() + childTicket.getTotalPrice();
        totalSeats = noOfadult + noOfchild;

        // check if infants or children are present without an adult
        if ((noOfinfant > 0 || noOfchild > 0) && noOfadult == 0) {
            throw new InvalidPurchaseException("Infants and Children cannot watch movies without an Adult");
        }
        // check if no tickets were purchased
        if (totalTicket == 0) {
            throw new InvalidPurchaseException("No tickets were purchased");
        }
        // check if the maximum number of tickets was exceeded
        if (totalTicket > maxTicket) {
            throw new InvalidPurchaseException("Maximum ticket count exceeded (25)");
        }
        // return the total price and total seats
        return new int[] { totalPrice, totalSeats };
    }
}
