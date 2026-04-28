# Money Manager

A personal finance tracking app for Android. Log daily transactions, categorize spending, and analyze income and expense patterns through interactive charts and a calendar view — all stored locally on-device.

---

## Features

- **Five transaction types** — Spent, Received, Lent, Saved, and Gifted, each tracked independently.
- **Payment method tracking** — Every transaction is tagged as Bank or Cash, with separate balance calculations for each.
- **Real-time balance summary** — Overall, bank, and cash balances are computed on the fly from stored transactions.
- **Category system** — Assign transactions to categories with a custom icon and color. Categories can be created and deleted from within the app.
- **Interactive pie charts** — Dedicated income and expense screens display an MPAndroidChart donut chart broken down by category. Tapping a slice toggles between amount and percentage views.
- **Calendar view** — Browse transactions by day; a summary card shows income, expense, and net total for the selected date.
- **Add and edit transactions** — Full form with amount, type toggle, payment method, category autocomplete, date picker, optional person field, notes, and a settled toggle for lent/received entries.
- **Delete with undo** — Transactions deleted from the list show a Snackbar undo action, guarding against accidental removals.
- **CSV export** — Export all transactions to a CSV file saved to the device's external storage.
- **Category management** — A grid picker for icons and a color palette let users create visually distinct categories.

---

## Screenshots

| Dashboard                                                                                                | Add Transaction                                                                                          | Expense Analysis                                                                                         |
| -------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------- |
| <img src="https://github.com/user-attachments/assets/32b6e5d0-be17-4491-a85a-a010846ceb56" width="250"/> | <img src="https://github.com/user-attachments/assets/bfdee2e7-173c-434d-bd2b-9d7128a5bc3a" width="250"/> | <img src="https://github.com/user-attachments/assets/4120853a-3d7c-4534-a1e7-c8f45c56f8af" width="250"/> |

| Calendar View                                                                                            | Income Analysis                                                                                          | Settings                                                                                                 |
| -------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------- |
| <img src="https://github.com/user-attachments/assets/48071a05-8797-4db0-8e37-8d82330b282a" width="250"/> | <img src="https://github.com/user-attachments/assets/b69a7dc6-ca45-4b7c-94fd-78f408d79b95" width="250"/> |<img width="250" alt="settings" src="https://github.com/user-attachments/assets/91b190a9-ed39-44ac-81e9-000071b049f7" />
 |

---

## Tech Stack

| Layer | Library / Tool |
|---|---|
| Language | Kotlin 2.0.21 |
| Min SDK / Target SDK | 24 / 36 |
| UI | Material Components 1.11+, ConstraintLayout, CardView |
| Navigation | Jetpack Navigation Component 2.9.x |
| Local database | Room 2.6.1 (two databases: transactions + categories) |
| Async | Kotlin Coroutines 1.7.3, Flow, LiveData |
| Charts | MPAndroidChart v3.1.0 |
| Calendar | Applandeo Material Calendar View 1.9.2 |
| CSV export | OpenCSV 5.9 |
| Build | Gradle 8.13, AGP 8.13.2, KAPT |

---

## Installation

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 11
- Android SDK with API 24–36 installed

### Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/Nagzz4vr/money-manager.git
   cd money-manager
   ```

2. Open the project in Android Studio via **File → Open**.

3. Let Gradle sync complete. If prompted, accept any SDK component installations.

4. Connect a physical device or start an emulator running API 24 or higher.

5. Run the app with the **Run** button or:
   ```bash
   ./gradlew installDebug
   ```

> The app requests `READ_EXTERNAL_STORAGE` and `WRITE_EXTERNAL_STORAGE` permissions for CSV export. Grant these when prompted on first use.

---

## Usage

### Recording a Transaction

1. Tap the **+** FAB on any screen to open the Add Transaction form.
2. Select a type (Spent / Saved / Lent / Received).
3. Enter the amount, pick a payment method (Bank or Cash), and choose a category.
4. Optionally set a date, add a person (useful for Lent/Received entries), write a note, or mark the transaction as settled.
5. Tap **Save**.

### Browsing Transactions

- The **main dashboard** shows recent transactions and overall balance figures.
- The **Calendar** tab lets you select any date to see that day's transactions and a daily income/expense/net summary.
- The **Income** and **Expense** tabs each display a category breakdown chart and a scrollable history list. Use the spinner at the top to filter by sub-type.

### Managing Categories

Navigate to **Settings → Manage Categories** to add or remove custom categories. Each category requires a name, a color from the palette, and an icon from the grid.

### Exporting Data

Go to **Settings → Backup Database** to export all transactions to `transactions.csv` in the app's external files directory.

---

## Project Structure

```
app/src/main/java/com/nagz/money_manager/
│
├── MainActivity.kt                  # Single-activity host; sets up edge-to-edge
│
├── data/
│   ├── local/
│   │   ├── converter/               # Room TypeConverter for TransactionType enum
│   │   ├── dao/                     # TransactionDao, CategoryDao, query result types
│   │   ├── database/                # AppDatabase (transactions), CategoryDatabase
│   │   └── entity/                  # TransactionEntity, CategoryEntity
│   └── repository/                  # TransactionRepository, CategoryRepository
│
├── domain/
│   └── model/
│       └── TransactionType.kt       # SPENT, RECEIVED, LENT, GIFTED, SAVED
│
├── ui/                              # All fragments, adapters, and ViewModels
│   ├── MainFragment.kt              # Dashboard / home screen
│   ├── AddTransactionFragment.kt    # New transaction form
│   ├── ModifyFragment.kt            # Edit / delete existing transaction
│   ├── CalendarFragment.kt          # Date-browsing with daily summary
│   ├── IncomeFragment.kt            # Income chart + history
│   ├── ExpenseFragment.kt           # Expense chart + history
│   ├── AddCategoriesFragment.kt     # Create custom category
│   ├── DeleteCategoriesFragment.kt  # Delete existing category
│   ├── AiFragment.kt                # AI chat screen (in progress)
│   ├── SettingsFragment.kt          # Backup, clear data, category management
│   ├── TransactionAdapter.kt        # RecyclerView adapter for transaction lists
│   ├── CategoryDropdownAdapter.kt   # Autocomplete adapter with icons
│   ├── IconAdapter.kt               # Grid adapter for icon picker
│   ├── ColorAdapter.kt              # Grid adapter for color picker
│   └── CategoryViewModel.kt        # ViewModel backed by CategoryRepository
│
└── utils/
    ├── Delete_Utils.kt              # Reusable confirm-and-delete with undo Snackbar
    └── Plot_Utils.kt                # MPAndroidChart pie chart setup helpers

app/src/main/res/
├── layout/                          # XML layouts for all fragments and list items
├── navigation/nav_graph.xml         # Full Navigation Component graph
├── drawable/                        # Vector icons for categories and UI
└── values/                          # Colors, themes, styles
```

---

## Future Improvements

- **AI spending assistant** — The `AiFragment` UI scaffold is in place. The intent is to connect it to a language model API to answer natural-language questions about spending patterns.
- **Google Sheets sync** — Settings exposes a sheet URL input and a Sync button; the integration logic is not yet implemented.
- **Budget limits** — Allow users to set monthly spending caps per category with alerts when approaching the limit.
- **Recurring transactions** — Automate fixed income or expense entries on a schedule.
- **Monthly / weekly summary views** — Aggregate charts across custom date ranges, not just per-day or all-time.
- **Database migration strategy** — Replace `fallbackToDestructiveMigration` with proper Room migration scripts before any production release.
- **Consolidate databases** — `AppDatabase` and `CategoryDatabase` are separate Room instances; merging them would simplify queries and allow joins.

---

## Contributing

Contributions are welcome.

1. Fork the repository and create a feature branch:
   ```bash
   git checkout -b feature/your-feature-name
   ```
2. Make your changes and ensure the project builds cleanly.
3. Write or update any relevant tests under `app/src/test/` or `app/src/androidTest/`.
4. Open a pull request with a clear description of the change and its motivation.

Please keep PRs focused on a single concern. For significant changes, open an issue first to discuss the approach.

---

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for the full text.

Copyright (c) 2026 Nagzz4vr
