package org.delcom.abdullah_ubaidpbo2026springbootcashflow.adapter.presenter;

import org.delcom.abdullah_ubaidpbo2026springbootcashflow.domain.entity.Transaction;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Presenter yang memformat data dari use case menjadi teks siap tampil.
 * Format uang: Rp 1,000,000.00 (pemisah ribuan koma, 2 desimal).
 */
public class FinancePresenter {

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Memformat angka menjadi "Rp 1,000,000.00". */
    public String formatMoney(double amount) {
        return String.format(Locale.US, "Rp %,.2f", amount);
    }

    /** Label tipe: Pemasukan / Pengeluaran. */
    public String formatType(Transaction t) {
        return t.getType().label();
    }

    /** Waktu transaksi. */
    public String formatCreatedAt(Transaction t) {
        return t.getCreatedAt().format(DATE_TIME_FORMAT);
    }

    /** Satu baris: [id] keterangan | Rp 1,000,000.00 | [Pemasukan]. */
    public String format(Transaction t) {
        return String.format("[%d] %s | %s | [%s]",
                t.getId(), t.getDescription(), formatMoney(t.getAmount()), formatType(t));
    }

    /** Memformat saldo: "Saldo: Rp 4,975,000.00". */
    public String formatBalance(double balance) {
        return "Saldo: " + formatMoney(balance);
    }

    private String formatList(List<Transaction> list, String header, String emptyMessage) {
        StringBuilder b = new StringBuilder(header);
        if (list.isEmpty()) {
            return b.append('\n').append(emptyMessage).toString();
        }
        return b.append('\n')
                .append(list.stream().map(this::format).collect(Collectors.joining("\n")))
                .toString();
    }

    public String showTransactions(List<Transaction> list) {
        return formatList(list, "Daftar Transaksi:", "- Data transaksi belum tersedia!");
    }

    public String showSearchResults(List<Transaction> list, String keyword) {
        return formatList(list, "Hasil Pencarian: \"" + keyword + "\"", "- Transaksi tidak ditemukan!");
    }

    public String showSortedTransactions(List<Transaction> list) {
        return formatList(list, "Daftar Transaksi (Terurut):", "- Data transaksi belum tersedia!");
    }

    public String showAddSuccess(Transaction t) {
        return "Berhasil menambah " + t.getType().label().toLowerCase() + ": " + format(t);
    }

    public String showRemoveSuccess() {
        return "Berhasil menghapus transaksi.";
    }

    public String showRemoveFailed(int id) {
        return String.format("[!] Gagal menghapus transaksi dengan ID: %d.", id);
    }

    public String showInvalidChoice() {
        return "[!] Pilihan tidak dimengerti.";
    }

    public String showInvalidId() {
        return "[!] ID tidak valid!";
    }

    public String showInvalidAmount() {
        return "[!] Jumlah harus berupa angka yang valid!";
    }

    public String showValidationError(String message) {
        return "[!] " + message;
    }
}
