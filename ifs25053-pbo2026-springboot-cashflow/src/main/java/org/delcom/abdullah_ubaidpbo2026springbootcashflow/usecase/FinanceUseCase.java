package org.delcom.abdullah_ubaidpbo2026springbootcashflow.usecase;

import org.delcom.abdullah_ubaidpbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.abdullah_ubaidpbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.abdullah_ubaidpbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.abdullah_ubaidpbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.abdullah_ubaidpbo2026springbootcashflow.domain.repository.ITransactionRepository;

import java.util.List;

/**
 * Use case logika bisnis aplikasi Cashflow.
 * Tidak melakukan I/O — hanya memproses data dan melempar checked exception domain.
 */
public class FinanceUseCase {
    /** Port repository yang di-inject dari luar (Dependency Inversion). */
    private final ITransactionRepository repository;

    public FinanceUseCase(ITransactionRepository repository) {
        this.repository = repository;
    }

    /** Mengambil semua transaksi. */
    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }

    /** Menambah pemasukan setelah validasi. */
    public Transaction addIncome(String description, double amount) throws ValidationException {
        validate(description, amount);
        return repository.saveIncome(description.trim(), amount);
    }

    /** Menambah pengeluaran setelah validasi. */
    public Transaction addExpense(String description, double amount) throws ValidationException {
        validate(description, amount);
        return repository.saveExpense(description.trim(), amount);
    }

    /** Menghapus transaksi berdasarkan ID; melempar exception jika ID tidak valid / tidak ditemukan. */
    public void removeTransaction(int id) throws EntityNotFoundException {
        if (id <= 0 || !repository.deleteById(id)) {
            throw new EntityNotFoundException(id);
        }
    }

    /** Mencari transaksi yang keterangannya mengandung kata kunci (case-insensitive). */
    public List<Transaction> searchTransactions(String keyword) {
        String lower = keyword == null ? "" : keyword.trim().toLowerCase();
        return repository.findBy(t -> t.getDescription().toLowerCase().contains(lower));
    }

    /** Mengurutkan transaksi sesuai {@link SortOption}. */
    public List<Transaction> sortTransactions(SortOption option) {
        return repository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }

    /** Total seluruh pemasukan. */
    public double getTotalIncome() {
        return repository.findBy(Transaction::isIncome).stream()
                .mapToDouble(Transaction::getAmount).sum();
    }

    /** Total seluruh pengeluaran. */
    public double getTotalExpense() {
        return repository.findBy(Transaction::isExpense).stream()
                .mapToDouble(Transaction::getAmount).sum();
    }

    /** Saldo = total pemasukan − total pengeluaran. */
    public double getBalance() {
        return getTotalIncome() - getTotalExpense();
    }

    private void validate(String description, double amount) throws ValidationException {
        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Keterangan transaksi tidak boleh kosong");
        }
        if (Double.isNaN(amount) || Double.isInfinite(amount) || amount <= 0) {
            throw new ValidationException("Jumlah harus berupa angka lebih besar dari 0");
        }
    }
}
