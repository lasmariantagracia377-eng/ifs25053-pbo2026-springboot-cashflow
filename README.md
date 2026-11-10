# abdullah_ubaid-pbo2026-springboot-cashflow

Aplikasi web pencatat keuangan (Cashflow) — Spring Boot 4.1.2, Maven, Vaadin 25, Java 25.
Arsitektur Clean Architecture: `domain ← usecase ← adapter / framework`. Data in-memory (reset saat restart).

## Langkah wajib sebelum dijalankan

1. **Ganti username.** Proyek memakai `abdullah_ubaid` sebagai contoh. Jalankan
   `.\rename-username.ps1 -NewUser username_anda`, lalu ganti nama folder proyek.
2. **Maven Wrapper.** Salin `mvnw`, `mvnw.cmd`, dan folder `.mvn/` dari kerangka proyek di folder lab
   (tidak disertakan di zip ini). Alternatif: buka proyek di IntelliJ IDEA lalu jalankan lewat Maven.
3. **Versi karibu-testing.** Di `pom.xml`, properti `karibu.version` adalah perkiraan. Samakan dengan
   versi di `pom.xml` kerangka lab / proyek Todo List jika build test gagal mengunduh dependency.

## Menjalankan

```powershell
.\mvnw.cmd spring-boot:run                      # development
.\mvnw.cmd clean package -Pproduction           # build JAR
java -jar target\abdullah_ubaid-pbo2026-springboot-cashflow-0.0.1-SNAPSHOT.jar
.\mvnw.cmd clean verify                         # test + laporan Jacoco
```

Port diatur lewat `.env` (`APP_PORT=8080`). Buka http://localhost:8080.

## Struktur

```
src/main/java/org/delcom/<username>pbo2026springbootcashflow/
├── App.java, DotEnv.java
├── config/AppConfig.java                # composition root (@Bean)
├── domain/entity/                       # Transaction, TransactionType, SortOption
├── domain/exception/                    # DomainException, EntityNotFoundException, ValidationException
├── domain/repository/                   # IRepository<T, ID>, ITransactionRepository
├── usecase/FinanceUseCase.java          # tambah/cari/urutkan/hapus/getBalance
├── adapter/repository/                  # InMemoryRepository<T, ID>, TransactionRepository
├── adapter/presenter/FinancePresenter.java   # format Rp 1,000,000.00
└── framework/view/FinanceView.java      # UI Vaadin (@Route(""))
```
