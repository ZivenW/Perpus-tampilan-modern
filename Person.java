package perpustakaan.model;

/**
 * Kelas induk abstrak untuk Admin dan Anggota.
 * Menerapkan inheritance, encapsulation, dan polymorphism.
 */
public abstract class Person {
    private String id;
    private String nama;

    public Person(String id, String nama) {
        this.id = id;
        this.nama = nama;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    /** Setiap turunan wajib menyediakan format informasi masing-masing. */
    public abstract String tampilInfo();
}
