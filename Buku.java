package perpustakaan.model;

/**
 * Model buku dengan atribut private (encapsulation).
 * Perubahan stok dilakukan melalui method khusus.
 */
public class Buku {
    private String id;
    private String judul;
    private String pengarang;
    private String penerbit;
    private int tahun;
    private int stok;

    public Buku(String id, String judul, String pengarang,
                String penerbit, int tahun, int stok) {
        this.id = id;
        this.judul = judul;
        this.pengarang = pengarang;
        this.penerbit = penerbit;
        this.tahun = tahun;
        this.stok = stok;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }

    public String getPengarang() { return pengarang; }
    public void setPengarang(String pengarang) { this.pengarang = pengarang; }

    public String getPenerbit() { return penerbit; }
    public void setPenerbit(String penerbit) { this.penerbit = penerbit; }

    public int getTahun() { return tahun; }
    public void setTahun(int tahun) { this.tahun = tahun; }

    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }

    /** Mengurangi stok satu buah. False jika stok kosong. */
    public boolean kurangiStok() {
        if (stok <= 0) {
            return false;
        }
        stok--;
        return true;
    }

    /** Menambah stok setelah buku dikembalikan. */
    public void tambahStok() {
        stok++;
    }

    public String tampilInfo() {
        return String.format("%-7s | %-25s | %-20s | %-16s | %4d | %4d",
                id, judul, pengarang, penerbit, tahun, stok);
    }
}
