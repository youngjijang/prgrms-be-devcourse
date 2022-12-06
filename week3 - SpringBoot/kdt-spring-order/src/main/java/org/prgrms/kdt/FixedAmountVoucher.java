package org.prgrms.kdt;

import java.util.UUID;

public class FixedAmountVoucher implements Voucher{

    private final UUID voucherId;
    private final long amount;

    public FixedAmountVoucher(UUID voucherId, long amout) {
        this.voucherId = voucherId;
        this.amount = amout;
    }

    @Override
    public UUID getVoucherId() {
        return voucherId;
    }

    public long discount (long beforeDiscount){
        return beforeDiscount - amount;
    }
}
