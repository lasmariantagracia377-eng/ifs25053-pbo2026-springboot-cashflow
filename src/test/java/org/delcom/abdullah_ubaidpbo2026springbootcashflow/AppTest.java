package org.delcom.abdullah_ubaidpbo2026springbootcashflow;

import org.delcom.abdullah_ubaidpbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.abdullah_ubaidpbo2026springbootcashflow.domain.repository.ITransactionRepository;
import org.delcom.abdullah_ubaidpbo2026springbootcashflow.usecase.FinanceUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/** Smoke test: memastikan wiring bean Clean Architecture dan alur saldo berjalan. */
@SpringBootTest
class AppTest {

    @Autowired
    private ITransactionRepository repository;

    @Autowired
    private FinanceUseCase useCase;

    @Autowired
    private FinancePresenter presenter;

    @Test
    void contextLoads() {
        assertNotNull(repository);
        assertNotNull(useCase);
        assertNotNull(presenter);
    }

    @Test
    void balanceFollowsIncomeAndExpense() throws Exception {
        double before = useCase.getBalance();
        useCase.addIncome("Gaji", 5_000_000);
        useCase.addExpense("Makan", 25_000);
        assertEquals(before + 4_975_000, useCase.getBalance(), 0.001);
        assertEquals("Rp 1,000,000.00", presenter.formatMoney(1_000_000));
    }
}
