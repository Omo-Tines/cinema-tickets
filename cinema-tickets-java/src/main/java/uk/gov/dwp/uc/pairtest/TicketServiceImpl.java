package uk.gov.dwp.uc.pairtest;

import org.junit.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import java.util.Arrays;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */
    /*
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
     * 
     * Payment and Reservation:
     * 
     * Payment must be processed using the TicketPaymentService, and it will always
     * succeed once requested.
     * Seat reservations must be made for Adult and Child tickets (not Infants, as
     * they do not require seats).
     */
    private TicketPaymentService PService;
    private SeatReservationService SeatRes;

    public TicketServiceImpl(TicketPaymentService PService, SeatReservationService SeatRes)
            throws InvalidPurchaseException {
        this.PService = PService;
        this.SeatRes = SeatRes;
    }

    // private static final int infant_ticket_price = 0;
    private static final int child_ticket_price = 15;
    private static final int adult_ticket_price = 25;
    private static final int maxTicket = 25;
    // private static final int infantPrice = 0;

    int noOfinfant = 0;
    int noOfchild = 0;
    int noOfadult = 0;
    int adultPrice = 0;
    int childPrice = 0;

    int totalTicket;
    int totalPrice;
    int totalSeats;

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)
            throws InvalidPurchaseException {
        if (accountId <= 0) {
            throw new InvalidPurchaseException("ID must be above zero (0)");
        }

        noOfinfant = 0;
        noOfchild = 0;
        noOfadult = 0;

        /* using a loop and a stream */
        /*
         * i chose this approach because of the readability of
         * for loops and the usability of streams
         * fun fact i discovered streams recently so i have been trying to learn its
         * implementation
         */

        /* streams aid in ensuring the array is only accesssed and not edited */

        Arrays.stream(ticketTypeRequests)
                .forEach(this::validTicket);

        checkBusinessRules(accountId);
    }

    private void validTicket(TicketTypeRequest req) {
        switch (req.getTicketType()) {
            case ADULT:
                noOfadult += req.getNoOfTickets();
                adultPrice = noOfadult * adult_ticket_price;
                break;
            case CHILD:
                noOfchild = req.getNoOfTickets();
                childPrice = noOfchild * child_ticket_price;
                break;
            case INFANT:
                noOfinfant = req.getNoOfTickets();
                break;
        }
    }

    private void checkBusinessRules(Long accountId) throws InvalidPurchaseException {
        if ((noOfinfant > 0 || noOfchild > 0) && noOfadult <= 0) {
            throw new InvalidPurchaseException("Infants and Children cannot watch movies without an Adult");
        }

        if (totalTicket > maxTicket) {
            throw new InvalidPurchaseException("Maximum ticket count exceeded (25)");
        }

        // Calculate total tickets and seats
        totalTicket = noOfinfant + noOfchild + noOfadult;
        totalPrice = adultPrice + childPrice;
        totalSeats = noOfadult + noOfchild;

        // Process payment and seat reservation
        PService.makePayment(accountId, totalPrice);
        SeatRes.reserveSeat(accountId, totalSeats);
    }
}
