package org.prgrms.kdt.voucher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    public Voucher getVoucher(UUID voucherId) {
        return  voucherRepository.findById(voucherId)
                .orElseThrow(() -> new RuntimeException("Can not find a voucher for " + voucherId));
    }

    public void useVoucher(Voucher voucher) {
    }
}
