package perpustakaan.model;

/**
 * Data administrator perpustakaan.
 * Admin mewarisi atribut ID dan nama dari Person.
 */
public class Admin extends Person {
    private String jabatan;

    public Admin(String id, String nama, String jabatan) {
        super(id, nama);
        this.jabatan = jabatan;
    }

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    @Override
    public String tampilInfo() {
        return String.format("%-8s | %-22s | %-18s",
                getId(), getNama(), jabatan);
    }
}
