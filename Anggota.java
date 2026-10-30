package perpustakaan.model;

/**
 * Data anggota perpustakaan.
 * Anggota mewarisi identitas dasar dari Person.
 */
public class Anggota extends Person {
    private String alamat;
    private String telepon;

    public Anggota(String id, String nama, String alamat, String telepon) {
        super(id, nama);
        this.alamat = alamat;
        this.telepon = telepon;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    @Override
    public String tampilInfo() {
        return String.format("%-8s | %-22s | %-25s | %-13s",
                getId(), getNama(), alamat, telepon);
    }
}
