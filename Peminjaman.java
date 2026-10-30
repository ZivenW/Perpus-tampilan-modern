package perpustakaan.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Transaksi peminjaman yang menghubungkan Buku dan Anggota (association).
 */
public class Peminjaman {
    public static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private String id;
    private Buku buku;
    private Anggota anggota;
    private LocalDateTime waktuPinjam;
    private LocalDateTime jatuhTempo;
    private LocalDateTime waktuKembali;
    private boolean dikembalikan;

    public Peminjaman(String id, Buku buku, Anggota anggota,
                      LocalDateTime waktuPinjam, LocalDateTime jatuhTempo) {
        this.id = id;
        this.buku = buku;
        this.anggota = anggota;
        this.waktuPinjam = waktuPinjam;
        this.jatuhTempo = jatuhTempo;
        this.waktuKembali = null;
        this.dikembalikan = false;
    }

    public String getId() { return id; }
    public Buku getBuku() { return buku; }
    public Anggota getAnggota() { return anggota; }
    public LocalDateTime getWaktuPinjam() { return waktuPinjam; }
    public LocalDateTime getJatuhTempo() { return jatuhTempo; }
    public LocalDateTime getWaktuKembali() { return waktuKembali; }
    public boolean isDikembalikan() { return dikembalikan; }

    public void kembalikan(LocalDateTime waktu) {
        if (!dikembalikan) {
            dikembalikan = true;
            waktuKembali = waktu;
        }
    }

    public String getStatus() {
        return dikembalikan ? "Dikembalikan" : "Dipinjam";
    }

    private String waktuKembaliTampil() {
        return dikembalikan ? waktuKembali.format(FMT) : "-";
    }

    public String tampilInfo() {
        return String.format("%-7s | %-23s | %-18s | %-16s | %-16s | %-16s | %-13s",
                id, buku.getJudul(), anggota.getNama(),
                waktuPinjam.format(FMT), jatuhTempo.format(FMT),
                waktuKembaliTampil(), getStatus());
    }
}
