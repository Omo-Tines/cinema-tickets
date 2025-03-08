package uk.gov.dwp.uc.pairtest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketType;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class TicketServiceImplTest {

    @Mock
    private TicketPaymentService ticketPaymentService;

    @Mock
    private SeatReservationService seatReservationService;

    private TicketServiceImpl ticketService;

    @Before
    public void setup() {
        ticketService = new TicketServiceImpl(ticketPaymentService, seatReservationService);
    }

    @Test
    public void testPurchaseTicketsWithValidAdultAndChild() throws InvalidPurchaseException {
        // Arrange
        Long accountId = 1L;
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketType.ADULT, 1);
        TicketTypeRequest childRequest = new TicketTypeRequest(TicketType.CHILD, 1);

        // Act
        ticketService.purchaseTickets(accountId, adultRequest, childRequest);

        // Assert
        verify(ticketPaymentService).makePayment(accountId, 40); // 25 + 15
        verify(seatReservationService).reserveSeat(accountId, 2); // 1 adult + 1 child
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testPurchaseTicketsWithoutAdult() throws InvalidPurchaseException {
        // Arrange
        Long accountId = 1L;
        TicketTypeRequest childRequest = new TicketTypeRequest(TicketType.CHILD, 1);

        // Act
        ticketService.purchaseTickets(accountId, childRequest);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testPurchaseTicketsWithInvalidAccountId() throws InvalidPurchaseException {
        // Arrange
        Long invalidAccountId = 0L;
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketType.ADULT, 1);

        // Act
        ticketService.purchaseTickets(invalidAccountId, adultRequest);
    }
}