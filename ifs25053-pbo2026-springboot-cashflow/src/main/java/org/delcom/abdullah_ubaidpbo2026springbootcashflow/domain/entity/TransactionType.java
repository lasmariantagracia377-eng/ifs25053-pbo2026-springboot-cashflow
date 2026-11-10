package org.delcom.abdullah_ubaidpbo2026springbootcashflow.domain.entity;

/** Jenis transaksi keuangan. */
public enum TransactionType {
    /** Pemasukan: menambah saldo. */
    INCOME("Pemasukan"),

    /** Pengeluaran: mengurangi saldo. */
    EXPENSE("Pengeluaran");

    private final String label;

    TransactionType(String label) {
        this.label = label;
    }

    /** Label tampilan dalam bahasa Indonesia. */
    public String label() {
        return label;
    }
}
