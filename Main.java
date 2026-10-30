package perpustakaan;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.function.Predicate;

import perpustakaan.model.Anggota;
import perpustakaan.model.Buku;
import perpustakaan.model.Peminjaman;
import perpustakaan.service.Perpustakaan;

/**
 * Aplikasi CLI Sistem Informasi Perpustakaan.
 * Tampilan terminal menggunakan warna ANSI dan tabel teks.
 */
public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final Perpustakaan PERPUSTAKAAN = new Perpustakaan();

    // ANSI escape code ditulis dengan escape Java octal, bukan "\\u001B".
    private static final String RESET = "\033[0m";
    private static final String BOLD = "\033[1m";
    private static final String CYAN = "\033[36m";
    private static final String BLUE = "\033[34m";
    private static final String GREEN = "\033[32m";
    private static final String YELLOW = "\033[33m";
    private static final String RED = "\033[31m";
    private static final String GRAY = "\033[90m";

    private static final int WIDTH = 130;

    public static void main(String[] args) {
        seedData();
        boolean running = true;

        while (running) {
            clearScreen();
            tampilHeader();
            tampilDashboard();
            tampilMenu();

            int pilihan = bacaInt("Pilih menu [0-6]: ");
            System.out.println();

            switch (pilihan) {
                case 1 -> menuBuku();
                case 2 -> menuAnggota();
                case 3 -> pinjamBuku();
                case 4 -> kembalikanBuku();
                case 5 -> tampilPeminjaman();
                case 6 -> tampilSemua();
                case 0 -> {
                    running = false;
                    pesanSukses("Terima kasih telah menggunakan Perpustakaan Digital.");
                }
                default -> pesanError("Pilihan tidak tersedia. Masukkan angka 0 sampai 6.");
            }

            if (running) pause();
        }
        SCANNER.close();
    }

    // ========================= TAMPILAN =========================

    private static void tampilHeader() {
        garis(CYAN);
        centered(BOLD + CYAN + "PERPUSTAKAAN DIGITAL" + RESET);
        centered("SISTEM INFORMASI MANAJEMEN PERPUSTAKAAN");
        centered(GRAY + "Kelola koleksi, anggota, dan transaksi dalam satu tempat" + RESET);
        garis(CYAN);
        System.out.println();
    }

    private static void tampilDashboard() {
        int totalJudul = PERPUSTAKAAN.getDaftarBuku().size();
        int totalBuku = PERPUSTAKAAN.getDaftarBuku().stream()
                .mapToInt(Buku::getStok).sum();
        int totalAnggota = PERPUSTAKAAN.getDaftarAnggota().size();
        long sedangDipinjam = PERPUSTAKAAN.getDaftarPeminjaman().stream()
                .filter(p -> !p.isDikembalikan()).count();

        System.out.println(BOLD + "  RINGKASAN PERPUSTAKAAN" + RESET);
        System.out.printf("  %-25s %-25s %-25s%n",
                kotakStat("TOTAL BUKU", String.valueOf(totalBuku)),
                kotakStat("TOTAL JUDUL", String.valueOf(totalJudul)),
                kotakStat("ANGGOTA", String.valueOf(totalAnggota)));
        System.out.println("  " + CYAN + "Sedang dipinjam: " + RESET
                + BOLD + sedangDipinjam + RESET + " transaksi");
        System.out.println();
    }

    private static String kotakStat(String label, String value) {
        return "[" + label + ": " + BOLD + value + RESET + "]";
    }

    private static void tampilMenu() {
        System.out.println(BOLD + BLUE + "  MENU UTAMA" + RESET);
        garis(GRAY);
        System.out.println("  " + CYAN + "[1]" + RESET + " Manajemen Buku       "
                + CYAN + "[2]" + RESET + " Manajemen Anggota");
        System.out.println("  " + CYAN + "[3]" + RESET + " Peminjaman Buku      "
                + CYAN + "[4]" + RESET + " Pengembalian Buku");
        System.out.println("  " + CYAN + "[5]" + RESET + " Riwayat Peminjaman   "
                + CYAN + "[6]" + RESET + " Lihat Semua Data");
        System.out.println("  " + RED + "[0]" + RESET + " Keluar");
        garis(GRAY);
    }

    private static void judulBagian(String judul, String deskripsi) {
        System.out.println();
        garis(CYAN);
        System.out.println("  " + BOLD + CYAN + judul + RESET);
        if (deskripsi != null && !deskripsi.isBlank()) {
            System.out.println("  " + GRAY + deskripsi + RESET);
        }
        garis(CYAN);
    }

    private static void garis(String warna) {
        System.out.println(warna + "  " + "─".repeat(WIDTH) + RESET);
    }

    private static void centered(String text) {
        // Hitung panjang teks tanpa menghitung kode warna ANSI.
        String teksBersih = text.replaceAll("\\033\\[[;\\d]*m", "");
        int padding = Math.max(0, (WIDTH - teksBersih.length()) / 2);
        System.out.println("  " + " ".repeat(padding) + text);
    }

    private static void clearScreen() {
        // Windows Terminal dan terminal VS Code mendukung ANSI escape sequence.
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void pause() {
        System.out.println();
        System.out.print(GRAY + "Tekan ENTER untuk kembali ke menu utama..." + RESET);
        SCANNER.nextLine();
    }

    // ========================= MENU BUKU =========================

    private static void menuBuku() {
        tampilBuku();
        if (konfirmasi("\nTambah buku baru? (y/n): ")) {
            tambahBuku();
        }
    }

    private static void tampilBuku() {
        judulBagian("DAFTAR BUKU", "Koleksi buku yang terdaftar di perpustakaan");
        List<Buku> daftar = PERPUSTAKAAN.getDaftarBuku();

        if (daftar.isEmpty()) {
            pesanInfo("Belum ada data buku.");
            return;
        }

        System.out.printf(BOLD + "  %-7s | %-25s | %-20s | %-16s | %4s | %4s%n" + RESET,
                "ID", "JUDUL", "PENGARANG", "PENERBIT", "TAHUN", "STOK");
        garis(GRAY);
        for (Buku buku : daftar) {
            String warnaStok = buku.getStok() == 0 ? RED : GREEN;
            System.out.printf("  %-7s | %-25s | %-20s | %-16s | %4d | %s%4d%s%n",
                    potong(buku.getId(), 7), potong(buku.getJudul(), 25),
                    potong(buku.getPengarang(), 20), potong(buku.getPenerbit(), 16),
                    buku.getTahun(), warnaStok, buku.getStok(), RESET);
        }
        garis(GRAY);
        int totalEksemplar = daftar.stream().mapToInt(Buku::getStok).sum();
        System.out.println("  Total buku: " + BOLD + totalEksemplar
                + RESET + " eksemplar  " + GRAY
                + "(dari " + daftar.size() + " judul)" + RESET);
    }

    private static void tambahBuku() {
        judulBagian("TAMBAH BUKU", "Masukkan informasi buku baru");
        String id = bacaId("ID Buku (B001): ", s -> s.matches("(?i)B\\d{3}"),
                "Format ID harus B diikuti 3 angka, contoh B001.",
                s -> PERPUSTAKAAN.cariBuku(s) != null);
        String judul = bacaString("Judul buku       : ");
        String pengarang = bacaString("Nama pengarang   : ");
        String penerbit = bacaString("Nama penerbit    : ");
        int tahun = bacaTahun("Tahun terbit     : ");
        int stok = bacaIntMinimal("Jumlah stok      : ", 0);

        boolean berhasil = PERPUSTAKAAN.tambahBuku(
                new Buku(id.toUpperCase(Locale.ROOT), judul, pengarang, penerbit, tahun, stok));

        if (berhasil) pesanSukses("Buku '" + judul + "' berhasil ditambahkan.");
        else pesanError("ID buku sudah digunakan.");
    }

    // ======================== MENU ANGGOTA ========================

    private static void menuAnggota() {
        tampilAnggota();
        if (konfirmasi("\nTambah anggota baru? (y/n): ")) {
            tambahAnggota();
        }
    }

    private static void tampilAnggota() {
        judulBagian("DAFTAR ANGGOTA", "Data anggota yang terdaftar");
        List<Anggota> daftar = PERPUSTAKAAN.getDaftarAnggota();

        if (daftar.isEmpty()) {
            pesanInfo("Belum ada data anggota.");
            return;
        }

        System.out.printf(BOLD + "  %-8s | %-22s | %-25s | %-13s%n" + RESET,
                "ID", "NAMA", "ALAMAT", "TELEPON");
        garis(GRAY);
        for (Anggota anggota : daftar) {
            System.out.printf("  %-8s | %-22s | %-25s | %-13s%n",
                    potong(anggota.getId(), 8), potong(anggota.getNama(), 22),
                    potong(anggota.getAlamat(), 25), anggota.getTelepon());
        }
        garis(GRAY);
        System.out.println("  Total anggota: " + BOLD + daftar.size() + RESET);
    }

    private static void tambahAnggota() {
        judulBagian("TAMBAH ANGGOTA", "Masukkan informasi anggota baru");
        String id = bacaId("ID Anggota (A001): ", s -> s.matches("(?i)A\\d{3}"),
                "Format ID harus A diikuti 3 angka, contoh A001.",
                s -> PERPUSTAKAAN.cariAnggota(s) != null);
        String nama = bacaString("Nama lengkap     : ");
        String alamat = bacaString("Alamat           : ");
        String telepon = bacaFormat("Nomor telepon (08xxxxxxxxxx): ",
                s -> s.matches("08\\d{10}"),
                "Nomor telepon harus diawali 08 dan berjumlah 12 digit.");

        boolean berhasil = PERPUSTAKAAN.tambahAnggota(
                new Anggota(id.toUpperCase(Locale.ROOT), nama, alamat, telepon));

        if (berhasil) pesanSukses("Anggota '" + nama + "' berhasil ditambahkan.");
        else pesanError("ID anggota sudah digunakan.");
    }

    // ======================= PEMINJAMAN =======================

    private static void pinjamBuku() {
        judulBagian("PEMINJAMAN BUKU", "Pilih buku dan anggota yang akan melakukan peminjaman");
        tampilBuku();
        if (PERPUSTAKAAN.getDaftarBuku().isEmpty()) {
            pesanInfo("Tambahkan data buku terlebih dahulu.");
            return;
        }

        String idBuku = bacaFormat("\nMasukkan ID buku (B001): ",
                s -> s.matches("(?i)B\\d{3}"),
                "Format ID buku tidak sesuai.");
        tampilAnggota();
        if (PERPUSTAKAAN.getDaftarAnggota().isEmpty()) {
            pesanInfo("Tambahkan data anggota terlebih dahulu.");
            return;
        }

        String idAnggota = bacaFormat("\nMasukkan ID anggota (A001): ",
                s -> s.matches("(?i)A\\d{3}"),
                "Format ID anggota tidak sesuai.");

        String hasil = PERPUSTAKAAN.pinjamBuku(idBuku, idAnggota);
        tampilHasilOperasi(hasil);
    }

    private static void kembalikanBuku() {
        judulBagian("PENGEMBALIAN BUKU", "Masukkan nomor transaksi yang ingin diselesaikan");
        tampilPeminjaman();

        if (PERPUSTAKAAN.getDaftarPeminjaman().isEmpty()) {
            pesanInfo("Belum ada transaksi peminjaman.");
            return;
        }

        String idTransaksi = bacaFormat("\nID transaksi (TRX001): ",
                s -> s.matches("(?i)TRX\\d{3}"),
                "Format ID transaksi tidak sesuai.");
        tampilHasilOperasi(PERPUSTAKAAN.kembalikanBuku(idTransaksi));
    }

    // ======================= RIWAYAT TRANSAKSI =======================

    private static void tampilPeminjaman() {
        judulBagian("RIWAYAT PEMINJAMAN", "Daftar seluruh transaksi peminjaman dan pengembalian");
        List<Peminjaman> daftar = PERPUSTAKAAN.getDaftarPeminjaman();

        if (daftar.isEmpty()) {
            pesanInfo("Belum ada transaksi peminjaman.");
            return;
        }

        System.out.printf(BOLD + "  %-7s | %-23s | %-18s | %-16s | %-16s | %-16s | %-13s%n" + RESET,
                "ID", "BUKU", "ANGGOTA", "TGL PINJAM", "JATUH TEMPO", "TGL KEMBALI", "STATUS");
        garis(GRAY);
        for (Peminjaman p : daftar) {
            String kembali = p.isDikembalikan()
                    ? p.getWaktuKembali().format(Peminjaman.FMT) : "-";
            String statusWarna = p.isDikembalikan() ? GREEN : YELLOW;
            System.out.printf("  %-7s | %-23s | %-18s | %-16s | %-16s | %-16s | %s%-13s%s%n",
                    p.getId(), potong(p.getBuku().getJudul(), 23),
                    potong(p.getAnggota().getNama(), 18),
                    p.getWaktuPinjam().format(Peminjaman.FMT),
                    p.getJatuhTempo().format(Peminjaman.FMT),
                    kembali, statusWarna, p.getStatus(), RESET);
        }
        garis(GRAY);
    }

    private static void tampilSemua() {
        tampilBuku();
        tampilAnggota();
        tampilPeminjaman();
    }

    // ======================== DATA AWAL ========================

    private static void seedData() {
        PERPUSTAKAAN.tambahBuku(new Buku(
                "B001", "Laskar Pelangi", "Andrea Hirata", "Bentang", 2005, 3));
        PERPUSTAKAAN.tambahBuku(new Buku(
                "B002", "Bumi Manusia", "Pramoedya A.T.", "Hasta Mitra", 1980, 2));
        PERPUSTAKAAN.tambahBuku(new Buku(
                "B003", "Clean Code", "Robert C. Martin", "Prentice Hall", 2008, 1));

        PERPUSTAKAAN.tambahAnggota(new Anggota(
                "A001", "Budi Santoso", "Jl. Melati 10", "081234567001"));
        PERPUSTAKAAN.tambahAnggota(new Anggota(
                "A002", "Siti Aminah", "Jl. Mawar 5", "081234567002"));
    }

    // ======================== INPUT & UTILITAS ========================

    private static String bacaString(String prompt) {
        while (true) {
            System.out.print("  " + prompt);
            String input = SCANNER.nextLine().trim();
            if (!input.isEmpty()) return input;
            pesanError("Input tidak boleh kosong.");
        }
    }

    private static String bacaFormat(String prompt, Predicate<String> valid, String pesan) {
        while (true) {
            String input = bacaString(prompt);
            if (valid.test(input)) return input;
            pesanError(pesan);
        }
    }

    private static String bacaId(String prompt, Predicate<String> valid,
                                 String pesan, Predicate<String> sudahAda) {
        while (true) {
            String id = bacaFormat(prompt, valid, pesan);
            if (!sudahAda.test(id)) return id;
            pesanError("ID " + id.toUpperCase(Locale.ROOT) + " sudah terdaftar. Gunakan ID lain.");
        }
    }

    private static int bacaInt(String prompt) {
        while (true) {
            System.out.print("  " + prompt);
            String input = SCANNER.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                pesanError("Masukkan angka yang valid.");
            }
        }
    }

    private static int bacaIntMinimal(String prompt, int minimal) {
        while (true) {
            int nilai = bacaInt(prompt);
            if (nilai >= minimal) return nilai;
            pesanError("Nilai minimal adalah " + minimal + ".");
        }
    }

    private static int bacaTahun(String prompt) {
        while (true) {
            int tahun = bacaInt(prompt);
            if (tahun >= 1000 && tahun <= 2100) return tahun;
            pesanError("Tahun harus berada pada rentang 1000 sampai 2100.");
        }
    }

    private static boolean konfirmasi(String prompt) {
        while (true) {
            String jawaban = bacaString(prompt).toLowerCase(Locale.ROOT);
            if (jawaban.equals("y") || jawaban.equals("ya")) return true;
            if (jawaban.equals("n") || jawaban.equals("tidak") || jawaban.equals("t")) return false;
            pesanError("Jawab dengan y/ya atau n/tidak.");
        }
    }

    private static void tampilHasilOperasi(String hasil) {
        if (hasil.startsWith("GAGAL:")) {
            pesanError(hasil);
        } else {
            pesanSukses(hasil);
        }
    }

    private static void pesanSukses(String pesan) {
        System.out.println("\n  " + GREEN + BOLD + "[BERHASIL]" + RESET + " " + pesan);
    }

    private static void pesanError(String pesan) {
        System.out.println("  " + RED + "[PERHATIAN]" + RESET + " " + pesan);
    }

    private static void pesanInfo(String pesan) {
        System.out.println("  " + YELLOW + "[INFO]" + RESET + " " + pesan);
    }

    private static String potong(String teks, int panjang) {
        if (teks == null) return "";
        if (teks.length() <= panjang) return teks;
        if (panjang <= 3) return teks.substring(0, panjang);
        return teks.substring(0, panjang - 3) + "...";
    }
}
