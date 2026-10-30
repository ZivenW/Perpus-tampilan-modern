package perpustakaan.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import perpustakaan.model.Anggota;
import perpustakaan.model.Buku;
import perpustakaan.model.Peminjaman;

/**
 * Service utama yang mengelola buku, anggota, dan transaksi.
 * Data disimpan di dalam ArrayList.
 */
public class Perpustakaan {
    private final List<Buku> daftarBuku = new ArrayList<>();
    private final List<Anggota> daftarAnggota = new ArrayList<>();
    private final List<Peminjaman> daftarPeminjaman = new ArrayList<>();
    private int counterTransaksi = 1;

    private static final int LAMA_PINJAM_HARI = 7;

    // ======================== BUKU ========================

    public boolean tambahBuku(Buku buku) {
        if (buku == null || cariBuku(buku.getId()) != null) {
            return false;
        }
        daftarBuku.add(buku);
        return true;
    }

    public Buku cariBuku(String id) {
        if (id == null) return null;
        for (Buku buku : daftarBuku) {
            if (buku.getId().equalsIgnoreCase(id.trim())) return buku;
        }
        return null;
    }

    public List<Buku> getDaftarBuku() {
        return Collections.unmodifiableList(daftarBuku);
    }

    // ======================= ANGGOTA ======================

    public boolean tambahAnggota(Anggota anggota) {
        if (anggota == null || cariAnggota(anggota.getId()) != null) {
            return false;
        }
        daftarAnggota.add(anggota);
        return true;
    }

    public Anggota cariAnggota(String id) {
        if (id == null) return null;
        for (Anggota anggota : daftarAnggota) {
            if (anggota.getId().equalsIgnoreCase(id.trim())) return anggota;
        }
        return null;
    }

    public List<Anggota> getDaftarAnggota() {
        return Collections.unmodifiableList(daftarAnggota);
    }

    // ===================== PEMINJAMAN =====================

    public String pinjamBuku(String idBuku, String idAnggota) {
        Buku buku = cariBuku(idBuku);
        if (buku == null) {
            return "GAGAL: Buku dengan ID '" + idBuku + "' tidak ditemukan.";
        }

        Anggota anggota = cariAnggota(idAnggota);
        if (anggota == null) {
            return "GAGAL: Anggota dengan ID '" + idAnggota + "' tidak ditemukan.";
        }

        if (!buku.kurangiStok()) {
            return "GAGAL: Stok buku '" + buku.getJudul() + "' sedang kosong.";
        }

        String idTrx = String.format("TRX%03d", counterTransaksi++);
        LocalDateTime waktuPinjam = LocalDateTime.now();
        LocalDateTime jatuhTempo = waktuPinjam.plusDays(LAMA_PINJAM_HARI);

        Peminjaman transaksi = new Peminjaman(
                idTrx, buku, anggota, waktuPinjam, jatuhTempo);
        daftarPeminjaman.add(transaksi);

        return "Nomor transaksi : " + idTrx + "\n"
                + "Judul buku      : " + buku.getJudul() + "\n"
                + "Nama anggota    : " + anggota.getNama() + "\n"
                + "Tanggal pinjam  : " + waktuPinjam.format(Peminjaman.FMT) + "\n"
                + "Jatuh tempo     : " + jatuhTempo.format(Peminjaman.FMT);
    }

    public String kembalikanBuku(String idTransaksi) {
        Peminjaman transaksi = cariPeminjaman(idTransaksi);
        if (transaksi == null) {
            return "GAGAL: Transaksi '" + idTransaksi + "' tidak ditemukan.";
        }
        if (transaksi.isDikembalikan()) {
            return "GAGAL: Transaksi '" + idTransaksi + "' sudah dikembalikan.";
        }

        LocalDateTime waktuKembali = LocalDateTime.now();
        transaksi.kembalikan(waktuKembali);
        transaksi.getBuku().tambahStok();

        String catatan = waktuKembali.isAfter(transaksi.getJatuhTempo())
                ? "\nCatatan         : Pengembalian melewati jatuh tempo."
                : "\nCatatan         : Dikembalikan tepat waktu.";

        return "Judul buku      : " + transaksi.getBuku().getJudul() + "\n"
                + "Nama anggota    : " + transaksi.getAnggota().getNama() + "\n"
                + "Tanggal kembali: " + waktuKembali.format(Peminjaman.FMT)
                + catatan;
    }

    public Peminjaman cariPeminjaman(String id) {
        if (id == null) return null;
        for (Peminjaman transaksi : daftarPeminjaman) {
            if (transaksi.getId().equalsIgnoreCase(id.trim())) return transaksi;
        }
        return null;
    }

    public List<Peminjaman> getDaftarPeminjaman() {
        return Collections.unmodifiableList(daftarPeminjaman);
    }
}
