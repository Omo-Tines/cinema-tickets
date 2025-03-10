package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TicketServiceImplTest {
    private TicketServiceImpl ticketService;
    private TicketTypeRequest[] ticketRequest;
    private SeatReservationService seatRes;
    private TicketPaymentService ticketPay;

    @BeforeEach
    public void setUp() {
        // mock the ticket payment service
        ticketPay = mock(TicketPaymentService.class);
        // mock the seat reservation service
        seatRes = mock(SeatReservationService.class);
        // create a new ticket service
        ticketService = new TicketServiceImpl(ticketPay, seatRes);
    }

    @Test
    // check if the account id is less than zero
    public void testPurchaseFailsIfAccountIdLessThanZero() {
        ticketRequest = new TicketTypeRequest[] {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2)
        };

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(-1L, ticketRequest));
        assertEquals("ID must be above zero (0) and not null", exception.getMessage());
    }

    @Test
    // check if the account id is zero
    public void testPurchaseFailsIfAccountIdEqualToZero() {
        ticketRequest = new TicketTypeRequest[] {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2)
        };

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(0L, ticketRequest));
        assertEquals("ID must be above zero (0) and not null", exception.getMessage());
    }

    @Test
    // check if the account id is null
    public void testPurchaseFailsIfAccountIdIsNull() {
        ticketRequest = new TicketTypeRequest[] {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2)
        };

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(null, ticketRequest));
        assertEquals("ID must be above zero (0) and not null", exception.getMessage());
    }

    @Test
    // check if the account id is greater than zero
    public void testPurchasePassesIfAccountIdGreaterThanZero() {
        ticketRequest = new TicketTypeRequest[] {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2)
        };
        // check if the purchase passes
        ticketService.purchaseTickets(50L, ticketRequest);
    }

    @Test
    // check if the ticket type request is empty
    public void testPurchaseFailsIfTicketTypeRequestIsEmpty() {
        ticketRequest = new TicketTypeRequest[] {};

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(50L, ticketRequest));
        assertEquals("Ticket requests cannot be null or empty", exception.getMessage());
    }

    @Test
    // check if the ticket type request is null
    public void testPurchaseFailsIfTicketTypeRequestIsNull() {
        ticketRequest = null;

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(50L, ticketRequest));
        assertEquals("Ticket requests cannot be null or empty", exception.getMessage());
    }

    @Test
    // check if the ticket type request item is null
    public void testPurchaseFailsIfTicketTypeRequestItemIsNull() {
        ticketRequest = new TicketTypeRequest[] {
                null
        };

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(50L, ticketRequest));
        assertEquals("Ticket type or number of tickets is invalid", exception.getMessage());
    }

    @Test
    // check if the number of tickets is negative
    public void testPurchaseFailsIfNumberOfTicketsIsNegative() {
        ticketRequest = new TicketTypeRequest[] {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, -1),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, -1),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, -1)
        };

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(50L, ticketRequest));
        assertEquals("Ticket type or number of tickets is invalid", exception.getMessage());
    }

    @Test
    // check if the infant request ticket with no adult
    public void testPurchaseFailsIfInfantRequestTicketWithNoAdult() {
        ticketRequest = new TicketTypeRequest[] {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 3),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 0)
        };

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(50L, ticketRequest));
        assertEquals("Infants and Children cannot watch movies without an Adult", exception.getMessage());
    }

    @Test
    // check if the children request ticket with no adult
    public void testPurchaseFailsIfChildRequestTicketWithNoAdult() {
        ticketRequest = new TicketTypeRequest[] {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 0),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3)
        };

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(50L, ticketRequest));
        assertEquals("Infants and Children cannot watch movies without an Adult", exception.getMessage());
    }

    @Test
    // check if no tickets were purchased
    public void testPurchaseFailsIfNoTicketsWerePurchased() {
        ticketRequest = new TicketTypeRequest[] {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 0),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 0)
        };

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(50L, ticketRequest));
        assertEquals("No tickets were purchased", exception.getMessage());
    }

    @Test
    // check if the maximum number of tickets was exceeded
    public void testPurchaseFailsIfMaximumNumberOfTicketsWasExceeded() {
        ticketRequest = new TicketTypeRequest[] {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 25),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 0),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2)
        };

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(50L, ticketRequest));
        assertEquals("Maximum ticket count exceeded (25)", exception.getMessage());
    }

}
