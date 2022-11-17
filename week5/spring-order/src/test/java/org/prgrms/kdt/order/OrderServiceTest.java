package org.prgrms.kdt.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.prgrms.kdt.voucher.FixedAmountVoucher;
import org.prgrms.kdt.voucher.MemoryVoucherRepository;
import org.prgrms.kdt.voucher.VoucherRepository;
import org.prgrms.kdt.voucher.VoucherService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
class OrderServiceTest {

    class OrderRepositoryStub implements OrderRepository{

        @Override
        public Order insert(Order order) {
            return null;
        }
    }


    // 상태에 집중을 한 테스트
    @Test
    @DisplayName("order가 생성되야아한다. (stub)")
    void TestCreateOrder() {
        // Given - 사용할 객체 만들기
        var voucherRepository = new MemoryVoucherRepository();
        var fixedAmountVoucher = new FixedAmountVoucher(UUID.randomUUID(),100);
        voucherRepository.insert(fixedAmountVoucher);
        var sut = new OrderService(new VoucherService(voucherRepository),new OrderRepositoryStub());

        // When
        var order = sut.createOrder(UUID.randomUUID(), List.of(new OrderItem(UUID.randomUUID(), 200, 1)),fixedAmountVoucher.getVoucherId());

        //Then
        assertThat(order.totalAmount(), is(100L));
        assertThat(order.getVoucher().isEmpty(), is(false));
        assertThat(order.getVoucher().get().getVoucherId(), is(fixedAmountVoucher.getVoucherId()));
        assertThat(order.getOrderStatus(), is(OrderStatus.ACCEPTED));

    }

    @Test
    @DisplayName("order가 생성되야아한다. (mock)")
    void TestCreateOrderByMock() {
        // Given - 이러한 상황을 만듬
        var voucherServiceMock = mock(VoucherService.class);
        var orderRepositoryMock = mock(OrderRepository.class);
        var fixedAmountVoucher = new FixedAmountVoucher(UUID.randomUUID(),100);

        when(voucherServiceMock.getVoucher(fixedAmountVoucher.getVoucherId())).thenReturn(fixedAmountVoucher);// 해당부분만 동작한다.
        var sut = new OrderService(voucherServiceMock,orderRepositoryMock);

        // When - order만들기
        var order = sut.createOrder(UUID.randomUUID(), List.of(new OrderItem(UUID.randomUUID(), 200, 1)),
                fixedAmountVoucher.getVoucherId());

        // Then - order에 대한 상태검사, orderservice안에서 사용되는 mock들이 어떠한 행동을 하는지 검증
        assertThat(order.totalAmount(), is(100L));
        assertThat(order.getVoucher().isEmpty(), is(false));

        var inOrder = inOrder(voucherServiceMock, orderRepositoryMock);//mock에 대한 순서를 보장할수있다.
        // 행위에 대한 assert가 아니라 어떠한 메소드가 호출됬는지 verify

        inOrder.verify(voucherServiceMock).getVoucher(fixedAmountVoucher.getVoucherId());
        inOrder.verify(orderRepositoryMock).insert(order);
        inOrder.verify(voucherServiceMock).useVoucher(fixedAmountVoucher);
    }
}