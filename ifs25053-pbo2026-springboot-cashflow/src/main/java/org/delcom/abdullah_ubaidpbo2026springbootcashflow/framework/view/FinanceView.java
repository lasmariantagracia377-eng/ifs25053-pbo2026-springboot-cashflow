package org.delcom.abdullah_ubaidpbo2026springbootcashflow.framework.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.delcom.abdullah_ubaidpbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.abdullah_ubaidpbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.abdullah_ubaidpbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.abdullah_ubaidpbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.abdullah_ubaidpbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.abdullah_ubaidpbo2026springbootcashflow.usecase.FinanceUseCase;

import java.util.Comparator;
import java.util.List;

/**
 * Tampilan web Vaadin aplikasi Cashflow.
 * Tambah pemasukan/pengeluaran lewat dialog, cari keterangan, urutkan lewat header kolom
 * atau pilihan SortOption, dan hapus lewat dialog konfirmasi.
 */
@Route("")
@PageTitle("Cashflow")
public class FinanceView extends VerticalLayout {

    private static final String EMPTY_DATA = "- Data transaksi belum tersedia!";
    private static final String EMPTY_SEARCH = "- Transaksi tidak ditemukan!";

    private final FinanceUseCase useCase;
    private final FinancePresenter presenter;

    private final Grid<Transaction> grid = new Grid<>(Transaction.class, false);
    private final TextField searchField = new TextField();
    private final Select<SortOption> sortSelect = new Select<>();
    private final Paragraph statusLabel = new Paragraph();
    private final Span emptyState = new Span(EMPTY_DATA);
    private final Span totalCount = new Span("0");
    private final Span incomeTotal = new Span();
    private final Span expenseTotal = new Span();
    private final Span balanceTotal = new Span();

    private enum DialogTone { INCOME, EXPENSE, DANGER }

    public FinanceView(FinanceUseCase useCase, FinancePresenter presenter) {
        this.useCase = useCase;
        this.presenter = presenter;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        configureGrid();
        configureToolbar();

        add(new H1("Cashflow"), buildSummaryCards(), buildToolbar(), statusLabel, grid);
        refreshAll();
    }

    // ---------------------------------------------------------------- ringkasan

    private HorizontalLayout buildSummaryCards() {
        HorizontalLayout summary = new HorizontalLayout(
                createSummaryCard("Jumlah Transaksi", totalCount, "#DBEAFE", "#1D4ED8", "#1E40AF"),
                createSummaryCard("Total Pemasukan", incomeTotal, "#DCFCE7", "#16A34A", "#166534"),
                createSummaryCard("Total Pengeluaran", expenseTotal, "#FEE2E2", "#DC2626", "#991B1B"),
                createSummaryCard("Saldo", balanceTotal, "#FEF3C7", "#D97706", "#92400E")
        );
        summary.setWidthFull();
        summary.setSpacing(true);
        summary.getStyle().set("align-items", "stretch");
        return summary;
    }

    private Div createSummaryCard(String label, Span value, String background, String accent, String valueColor) {
        Span caption = new Span(label);
        caption.getStyle()
                .set("display", "block")
                .set("font-size", "0.875rem")
                .set("font-weight", "500")
                .set("color", accent);

        value.getStyle()
                .set("display", "block")
                .set("margin-top", "0.35rem")
                .set("font-size", "1.5rem")
                .set("font-weight", "700")
                .set("line-height", "1.2")
                .set("color", valueColor);

        Div card = new Div(caption, value);
        card.getStyle()
                .set("flex", "1")
                .set("min-width", "0")
                .set("background", background)
                .set("border", "1px solid " + accent)
                .set("border-left", "6px solid " + accent)
                .set("border-radius", "12px")
                .set("padding", "1rem 1.1rem")
                .set("box-sizing", "border-box");
        return card;
    }

    private void updateSummary() {
        totalCount.setText(String.valueOf(useCase.getAllTransactions().size()));
        incomeTotal.setText(presenter.formatMoney(useCase.getTotalIncome()));
        expenseTotal.setText(presenter.formatMoney(useCase.getTotalExpense()));
        balanceTotal.setText(presenter.formatBalance(useCase.getBalance()));
    }

    // --------------------------------------------------------------------- grid

    private void configureGrid() {
        grid.addColumn(Transaction::getId)
                .setHeader("ID").setKey("id")
                .setAutoWidth(true).setFlexGrow(0)
                .setSortable(true).setComparator(Transaction::getId);

        grid.addColumn(Transaction::getDescription)
                .setHeader("Keterangan").setKey("description")
                .setFlexGrow(1).setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getDescription, String.CASE_INSENSITIVE_ORDER));

        grid.addColumn(t -> presenter.formatMoney(t.getAmount()))
                .setHeader("Jumlah").setKey("amount")
                .setAutoWidth(true).setSortable(true)
                .setComparator(Comparator.comparingDouble(Transaction::getAmount));

        grid.addComponentColumn(this::createTypeBadge)
                .setHeader("Tipe").setKey("type")
                .setAutoWidth(true).setFlexGrow(0)
                .setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getType));

        grid.addColumn(presenter::formatCreatedAt)
                .setHeader("Waktu").setKey("createdAt")
                .setAutoWidth(true).setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getCreatedAt));

        grid.addComponentColumn(this::createActionButtons)
                .setHeader("Tindakan").setKey("tindakan")
                .setAutoWidth(true).setFlexGrow(0).setSortable(false);

        grid.setEmptyStateComponent(emptyState);
        grid.setSizeFull();
    }

    private Span createTypeBadge(Transaction t) {
        Span badge = new Span(presenter.formatType(t));
        String color = t.isIncome() ? "#166534" : "#991B1B";
        String bg = t.isIncome() ? "#DCFCE7" : "#FEE2E2";
        badge.getStyle()
                .set("color", color)
                .set("background", bg)
                .set("padding", "0.15rem 0.6rem")
                .set("border-radius", "999px")
                .set("font-weight", "600")
                .set("font-size", "0.8rem");
        return badge;
    }

    private HorizontalLayout createActionButtons(Transaction t) {
        Button delete = new Button("Hapus", VaadinIcon.TRASH.create(), e -> openDeleteDialog(t));
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY_INLINE);
        return new HorizontalLayout(delete);
    }

    // ------------------------------------------------------------------ toolbar

    private void configureToolbar() {
        searchField.setPlaceholder("Cari keterangan...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);

        sortSelect.setPlaceholder("Urutkan...");
        sortSelect.setItems(SortOption.values());
        sortSelect.setItemLabelGenerator(this::sortLabel);
        sortSelect.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                sortTransactions(e.getValue());
            }
        });
    }

    private String sortLabel(SortOption option) {
        return switch (option) {
            case AMOUNT_ASC -> "Jumlah terkecil";
            case AMOUNT_DESC -> "Jumlah terbesar";
            case INCOME_FIRST -> "Pemasukan dulu";
            case EXPENSE_FIRST -> "Pengeluaran dulu";
        };
    }

    private HorizontalLayout buildToolbar() {
        Button income = new Button("Tambah Pemasukan", VaadinIcon.PLUS.create(), e -> openAddDialog(true));
        income.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);

        Button expense = new Button("Tambah Pengeluaran", VaadinIcon.MINUS.create(), e -> openAddDialog(false));
        expense.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);

        Button search = new Button("Cari", VaadinIcon.SEARCH.create(), e -> searchTransactions());
        Button refresh = new Button("Muat Ulang", VaadinIcon.REFRESH.create(), e -> refreshAll());

        HorizontalLayout toolbar = new HorizontalLayout(searchField, search, sortSelect, refresh, income, expense);
        toolbar.setWidthFull();
        toolbar.setFlexGrow(1, searchField);
        toolbar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        return toolbar;
    }

    // ------------------------------------------------------------------ dialogs

    private void openAddDialog(boolean income) {
        TextField descriptionField = new TextField("Keterangan");
        descriptionField.setWidthFull();

        TextField amountField = new TextField("Jumlah (Rp)");
        amountField.setPlaceholder("contoh: 1000000");
        amountField.setWidthFull();

        VerticalLayout form = new VerticalLayout(descriptionField, amountField);
        form.setPadding(false);
        form.setSpacing(true);

        Dialog dialog = new Dialog();
        Button save = new Button("Simpan", VaadinIcon.CHECK.create(), e -> {
            try {
                double amount = parseAmount(amountField.getValue());
                Transaction created = income
                        ? useCase.addIncome(descriptionField.getValue(), amount)
                        : useCase.addExpense(descriptionField.getValue(), amount);
                showSuccess(presenter.showAddSuccess(created));
                dialog.close();
                refreshAll();
            } catch (ValidationException ex) {
                showError(presenter.showValidationError(ex.getMessage()));
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancel = new Button("Batal", VaadinIcon.CLOSE.create(), e -> dialog.close());
        buildDialog(dialog, income ? "Tambah Pemasukan" : "Tambah Pengeluaran",
                income ? DialogTone.INCOME : DialogTone.EXPENSE, form, cancel, save);
        dialog.open();
    }

    private void openDeleteDialog(Transaction t) {
        Paragraph message = new Paragraph("Yakin ingin menghapus transaksi \"" + t.getDescription()
                + "\" (" + presenter.formatMoney(t.getAmount()) + ", ID " + t.getId() + ")?");
        message.getStyle().set("margin", "0");

        Dialog dialog = new Dialog();
        Button confirm = new Button("Hapus", VaadinIcon.TRASH.create(), e -> {
            deleteTransaction(t);
            dialog.close();
        });
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button cancel = new Button("Batal", VaadinIcon.CLOSE.create(), e -> dialog.close());
        buildDialog(dialog, "Menghapus Transaksi", DialogTone.DANGER, message, cancel, confirm);
        dialog.open();
    }

    private void buildDialog(Dialog dialog, String title, DialogTone tone, Component body, Button... footerButtons) {
        dialog.setWidth("500px");
        dialog.setMaxWidth("95vw");
        dialog.setResizable(false);

        String headerBg = switch (tone) {
            case INCOME -> "#16A34A";
            case EXPENSE, DANGER -> "#DC2626";
        };

        Span headerTitle = new Span(title);
        headerTitle.getStyle()
                .set("color", "#FFFFFF")
                .set("font-size", "1.125rem")
                .set("font-weight", "600");

        Div header = new Div(headerTitle);
        header.getStyle()
                .set("background", headerBg)
                .set("padding", "1rem 1.5rem")
                .set("box-sizing", "border-box")
                .set("width", "100%");

        Div bodyBox = new Div(body);
        bodyBox.getStyle()
                .set("background", "#FFFFFF")
                .set("padding", "1.25rem 1.5rem")
                .set("box-sizing", "border-box")
                .set("width", "100%");

        Div footer = new Div();
        footer.getStyle()
                .set("display", "flex")
                .set("justify-content", "flex-end")
                .set("gap", "0.75rem")
                .set("background", "#F3F4F6")
                .set("border-top", "1px solid #E5E7EB")
                .set("padding", "0.85rem 1.5rem 1.1rem")
                .set("box-sizing", "border-box")
                .set("width", "100%");
        footer.add(footerButtons);

        Div shell = new Div(header, bodyBox, footer);
        shell.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("width", "100%")
                .set("overflow", "hidden")
                .set("border-radius", "12px")
                .set("background", "#FFFFFF");

        dialog.removeAll();
        dialog.add(shell);
        removeOverlayPadding(dialog);
    }

    private void removeOverlayPadding(Dialog dialog) {
        dialog.addOpenedChangeListener(event -> {
            if (event.isOpened()) {
                dialog.getElement().executeJs("""
                        const overlay = this.$.overlay;
                        if (!overlay || !overlay.shadowRoot) {
                          return;
                        }
                        const content = overlay.shadowRoot.querySelector('[part="content"]');
                        if (content) {
                          content.style.padding = '0';
                          content.style.overflow = 'visible';
                          content.style.maxHeight = 'none';
                        }
                        const overlayPart = overlay.shadowRoot.querySelector('[part="overlay"]');
                        if (overlayPart) {
                          overlayPart.style.padding = '0';
                          overlayPart.style.overflow = 'hidden';
                        }
                        """);
            }
        });
    }

    // ------------------------------------------------------------------ aksi

    /** Mengubah teks input menjadi angka; melempar ValidationException jika bukan angka. */
    private double parseAmount(String raw) throws ValidationException {
        String text = raw == null ? "" : raw.trim();
        if (!text.matches("-?\\d+(\\.\\d+)?")) {
            throw new ValidationException("Jumlah harus berupa angka yang valid");
        }
        return Double.parseDouble(text);
    }

    private void deleteTransaction(Transaction t) {
        try {
            useCase.removeTransaction(t.getId());
            showSuccess(presenter.showRemoveSuccess());
            refreshAll();
        } catch (EntityNotFoundException ex) {
            showError(presenter.showRemoveFailed(ex.getId()));
            refreshAll();
        }
    }

    private void searchTransactions() {
        String keyword = searchField.getValue();
        if (keyword.isBlank()) {
            refreshAll();
            return;
        }
        List<Transaction> results = useCase.searchTransactions(keyword);
        emptyState.setText(results.isEmpty() ? EMPTY_SEARCH : EMPTY_DATA);
        refreshGrid(results, "Hasil Pencarian: \"" + keyword + "\"");
    }

    private void sortTransactions(SortOption option) {
        emptyState.setText(EMPTY_DATA);
        refreshGrid(useCase.sortTransactions(option), "Daftar Transaksi (" + sortLabel(option) + ")");
    }

    private void refreshAll() {
        emptyState.setText(EMPTY_DATA);
        searchField.clear();
        sortSelect.clear();
        refreshGrid(useCase.getAllTransactions(), "Daftar Transaksi");
    }

    private void refreshGrid(List<Transaction> items, String title) {
        grid.setItems(items);
        updateSummary();
        if (items.isEmpty()) {
            statusLabel.setText(title + " — " + emptyState.getText().replaceFirst("^- ", ""));
        } else {
            statusLabel.setText(title);
        }
    }

    private void showSuccess(String message) {
        Notification n = Notification.show(message, 3000, Notification.Position.BOTTOM_START);
        n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showError(String message) {
        Notification n = Notification.show(message, 4000, Notification.Position.BOTTOM_START);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
