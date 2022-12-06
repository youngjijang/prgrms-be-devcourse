package org.prgrms.kdt.voucher;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.security.auth.login.Configuration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@Repository
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class MemoryVoucherRepository implements VoucherRepository, InitializingBean, DisposableBean
{

    // hashMap을 이용해서 저장
    // thread safe 하게 하기 위해 concurrent
    private  final Map<UUID, Voucher> storage = new ConcurrentHashMap<>();
    @Override
    public Optional<Voucher> findById(UUID voucherId) {
        return Optional.ofNullable(storage.get(voucherId));
    }

    @Override
    public Voucher insert(Voucher voucher) {
        storage.put(voucher.getVoucherId(),voucher);
        return voucher;
    }

    @PostConstruct
    public void postConstruct(){
        System.out.println("postConstruct called!");
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("afterPropertiesSet called!");
    }


    @Override
    public void destroy() throws Exception {
        System.out.println("destroy called!");
    }
}
