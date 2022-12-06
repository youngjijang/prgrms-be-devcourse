package org.prgrms.kdt;

import java.util.Optional;
import java.util.UUID;

public interface VoucherRepository { // repo는 저장 방식이 바뀔수 있기때문에 보통 interface로 도출
    Optional<Voucher> findById(UUID voucherId);// 바우처가 없을 수 도 있으니까

}
