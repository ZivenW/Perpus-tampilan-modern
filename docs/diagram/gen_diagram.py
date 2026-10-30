"""Generate UML Class Diagram & Flowchart PNG untuk Sistem Perpustakaan."""
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
from matplotlib.patches import FancyBboxPatch, FancyArrowPatch, Polygon
from matplotlib.lines import Line2D

HEADER = "#2c3e50"
FILL = "#ecf6fd"
BORDER = "#2980b9"
TEXT = "#1b2631"


def uml_box(ax, x, y, w, name, attrs, methods, stereotype=None):
    """Gambar satu kotak class UML di posisi kiri-atas (x, y)."""
    line_h = 0.32
    pad = 0.12
    n_head = 2 if stereotype else 1
    head_h = n_head * line_h + pad
    attr_h = max(len(attrs), 1) * line_h + pad
    meth_h = max(len(methods), 1) * line_h + pad
    total_h = head_h + attr_h + meth_h

    # badan
    ax.add_patch(FancyBboxPatch((x, y - total_h), w, total_h,
                 boxstyle="round,pad=0.02,rounding_size=0.02",
                 linewidth=1.6, edgecolor=BORDER, facecolor=FILL, zorder=2))
    # header
    ax.add_patch(plt.Rectangle((x, y - head_h), w, head_h,
                 linewidth=0, facecolor=HEADER, zorder=3))
    cy = y - line_h + 0.04
    if stereotype:
        ax.text(x + w / 2, cy, stereotype, ha="center", va="center",
                color="#d6eaf8", fontsize=8, style="italic", zorder=4)
        cy -= line_h
    ax.text(x + w / 2, cy, name, ha="center", va="center",
            color="white", fontsize=11, fontweight="bold", zorder=4)

    # garis pemisah atribut
    sep1 = y - head_h
    ax.plot([x, x + w], [sep1, sep1], color=BORDER, lw=1, zorder=4)
    ty = sep1 - line_h + 0.03
    for a in attrs:
        ax.text(x + 0.12, ty, a, ha="left", va="center",
                color=TEXT, fontsize=8.3, family="monospace", zorder=4)
        ty -= line_h

    # garis pemisah method
    sep2 = sep1 - attr_h
    ax.plot([x, x + w], [sep2, sep2], color=BORDER, lw=1, zorder=4)
    ty = sep2 - line_h + 0.03
    for m in methods:
        ax.text(x + 0.12, ty, m, ha="left", va="center",
                color=TEXT, fontsize=8.3, family="monospace", zorder=4)
        ty -= line_h

    return {"x": x, "y": y, "w": w, "h": total_h,
            "cx": x + w / 2, "top": y, "bottom": y - total_h,
            "left": x, "right": x + w}


def triangle(ax, tip, base_center, size=0.18):
    """Segitiga kosong (generalization / inheritance) di ujung 'tip'."""
    import numpy as np
    dx, dy = base_center[0] - tip[0], base_center[1] - tip[1]
    ln = (dx**2 + dy**2) ** 0.5
    ux, uy = dx / ln, dy / ln
    px, py = -uy, ux
    b1 = (tip[0] + ux * size + px * size * 0.7, tip[1] + uy * size + py * size * 0.7)
    b2 = (tip[0] + ux * size - px * size * 0.7, tip[1] + uy * size - py * size * 0.7)
    ax.add_patch(Polygon([tip, b1, b2], closed=True,
                 facecolor="white", edgecolor=HEADER, lw=1.6, zorder=6))
    return (tip[0] + ux * size, tip[1] + uy * size)


def diamond(ax, center, size=0.16, filled=False, color=None):
    """Belah ketupat. filled=False -> aggregation (kosong),
    filled=True -> composition (terisi)."""
    cx, cy = center
    ec = color if color else HEADER
    pts = [(cx, cy + size), (cx + size, cy), (cx, cy - size), (cx - size, cy)]
    ax.add_patch(Polygon(pts, closed=True,
                 facecolor=(ec if filled else "white"),
                 edgecolor=ec, lw=1.6, zorder=6))


# =================== UML CLASS DIAGRAM ===================
def build_uml():
    fig, ax = plt.subplots(figsize=(17, 13))
    ax.set_xlim(0, 17)
    ax.set_ylim(0, 13)
    ax.axis("off")
    fig.suptitle("UML Class Diagram — Sistem Informasi Perpustakaan",
                 fontsize=17, fontweight="bold", color=HEADER, y=0.96)

    # --- Baris atas: Person di tengah ---
    person = uml_box(ax, 6.3, 12.0, 4.4, "Person",
        ["- id : String", "- nama : String"],
        ["+ getId() : String", "+ setId(String)", "+ getNama() : String",
         "+ setNama(String)", "+ tampilInfo() : String"],
        stereotype="<<abstract>>")

    # --- Baris kedua: Anggota (kiri) & Admin (kanan) ---
    anggota = uml_box(ax, 0.5, 8.5, 4.6, "Anggota",
        ["- alamat : String", "- telepon : String"],
        ["+ tampilInfo() : String"])

    petugas = uml_box(ax, 11.9, 8.5, 4.6, "Admin",
        ["- jabatan : String"],
        ["+ tampilInfo() : String"])

    # --- Baris ketiga: Perpustakaan (tengah) ---
    perpus = uml_box(ax, 5.9, 8.3, 5.3, "Perpustakaan",
        ["- daftarBuku : List<Buku>",
         "- daftarAnggota : List<Anggota>",
         "- daftarPeminjaman : List<Peminjaman>"],
        ["+ tambahBuku() / cariBuku()",
         "+ tambahAnggota() / cariAnggota()",
         "+ pinjamBuku() / kembalikanBuku()",
         "+ cariPeminjaman() / getDaftar...()"])

    # --- Baris bawah: Buku (kiri) & Peminjaman (kanan) ---
    buku = uml_box(ax, 0.5, 4.3, 4.8, "Buku",
        ["- id : String", "- judul : String", "- pengarang : String",
         "- penerbit : String", "- tahun : int", "- stok : int"],
        ["+ getter / setter ...", "+ kurangiStok() : boolean",
         "+ tambahStok()", "+ tampilInfo() : String"])

    peminjaman = uml_box(ax, 11.3, 4.3, 5.4, "Peminjaman",
        ["- id : String", "- buku : Buku", "- anggota : Anggota",
         "- waktuPinjam : LocalDateTime", "- jatuhTempo : LocalDateTime",
         "- waktuKembali : LocalDateTime", "- dikembalikan : boolean"],
        ["+ kembalikan(LocalDateTime)", "+ getStatus() : String",
         "+ tampilInfo() : String"])

    # --- INHERITANCE: Anggota -> Person, Admin -> Person (segitiga kosong) ---
    tip = triangle(ax, (person["cx"] - 1.2, person["bottom"]),
                   (anggota["cx"], anggota["top"]))
    ax.add_line(Line2D([tip[0], anggota["cx"]], [tip[1], anggota["top"]],
                color=HEADER, lw=1.6, zorder=5))
    tip = triangle(ax, (person["cx"] + 1.2, person["bottom"]),
                   (petugas["cx"], petugas["top"]))
    ax.add_line(Line2D([tip[0], petugas["cx"]], [tip[1], petugas["top"]],
                color=HEADER, lw=1.6, zorder=5))
    ax.text(3.0, 10.0, "inheritance", fontsize=9, style="italic", color="#7f8c8d")
    ax.text(11.4, 10.0, "inheritance", fontsize=9, style="italic", color="#7f8c8d")

    # --- AGGREGATION: Perpustakaan o-- Buku (belah ketupat KOSONG) ---
    # Buku dibuat di luar (Main) lalu dititipkan -> kepemilikan lemah.
    diamond(ax, (perpus["left"] + 0.2, perpus["bottom"]), color="#16a085")
    ax.add_line(Line2D([perpus["left"] + 0.2, buku["right"] - 0.4],
                [perpus["bottom"] - 0.14, buku["top"]],
                color="#16a085", lw=1.5, zorder=1))
    ax.text(4.9, 6.1, "aggregation", fontsize=9, style="italic", color="#16a085")

    # --- COMPOSITION: Perpustakaan *-- Peminjaman (belah ketupat TERISI) ---
    # Peminjaman di-"new" di dalam pinjamBuku() & mati jika Perpustakaan hilang.
    diamond(ax, (perpus["right"] - 0.2, perpus["bottom"]),
            filled=True, color="#8e44ad")
    ax.add_line(Line2D([perpus["right"] - 0.2, peminjaman["left"] + 0.4],
                [perpus["bottom"] - 0.14, peminjaman["top"]],
                color="#8e44ad", lw=1.5, zorder=1))
    ax.text(10.1, 6.1, "composition", fontsize=9, style="italic", color="#8e44ad")

    # --- ASSOCIATION: Peminjaman --> Buku & Peminjaman --> Anggota ---
    # Peminjaman -> Buku (garis lurus bawah)
    ax.add_patch(FancyArrowPatch((peminjaman["left"], peminjaman["bottom"] + 0.5),
                 (buku["right"], buku["bottom"] + 0.5),
                 arrowstyle="-|>", mutation_scale=15, color="#c0392b",
                 lw=1.3, connectionstyle="arc3,rad=0.12", zorder=5))
    ax.text(5.8, 2.3, "meminjam (association)", fontsize=9,
            style="italic", color="#c0392b", ha="center",
            bbox=dict(boxstyle="round,pad=0.2", fc="white", ec="none"))
    # Peminjaman -> Anggota (lengkung kanan-atas ke kiri)
    ax.add_patch(FancyArrowPatch((peminjaman["right"] - 0.3, peminjaman["top"]),
                 (anggota["right"] - 0.6, anggota["bottom"]),
                 arrowstyle="-|>", mutation_scale=15, color="#c0392b",
                 lw=1.3, connectionstyle="arc3,rad=0.32", zorder=0))
    ax.text(12.9, 6.8, "oleh (association)", fontsize=9,
            style="italic", color="#c0392b",
            bbox=dict(boxstyle="round,pad=0.2", fc="white", ec="none"))

    # legend
    legend = [
        Line2D([0], [0], color=HEADER, lw=2.5, label="Inheritance (generalization)"),
        Line2D([0], [0], color="#16a085", lw=2.5, label="Aggregation  ◇  (kepemilikan lemah)"),
        Line2D([0], [0], color="#8e44ad", lw=2.5, label="Composition  ◆  (kepemilikan kuat)"),
        Line2D([0], [0], color="#c0392b", lw=2.5, label="Association (memakai)"),
    ]
    ax.legend(handles=legend, loc="upper left", ncol=1,
              fontsize=10, frameon=True, bbox_to_anchor=(0.01, 0.99))

    fig.savefig("uml_class_diagram.png", dpi=170, bbox_inches="tight")
    plt.close(fig)
    print("OK uml_class_diagram.png")


# =================== FLOWCHART PEMINJAMAN ===================
def fbox(ax, x, y, w, h, text, shape="rect", fc="#eaf2f8", ec="#2471a3"):
    if shape == "round":  # terminal
        p = FancyBboxPatch((x - w/2, y - h/2), w, h,
            boxstyle="round,pad=0.02,rounding_size=0.25",
            fc="#d5f5e3", ec="#1e8449", lw=1.6, zorder=2)
        ax.add_patch(p)
    elif shape == "diamond":  # decision
        pts = [(x, y + h/2), (x + w/2, y), (x, y - h/2), (x - w/2, y)]
        ax.add_patch(Polygon(pts, closed=True, fc="#fcf3cf", ec="#b7950b",
                     lw=1.6, zorder=2))
    elif shape == "io":  # parallelogram input/output
        sk = 0.25
        pts = [(x - w/2 + sk, y + h/2), (x + w/2 + sk, y + h/2),
               (x + w/2 - sk, y - h/2), (x - w/2 - sk, y - h/2)]
        ax.add_patch(Polygon(pts, closed=True, fc="#d6eaf8", ec="#2471a3",
                     lw=1.6, zorder=2))
    else:  # process
        ax.add_patch(FancyBboxPatch((x - w/2, y - h/2), w, h,
            boxstyle="round,pad=0.02,rounding_size=0.03",
            fc=fc, ec=ec, lw=1.6, zorder=2))
    ax.text(x, y, text, ha="center", va="center", fontsize=9,
            color=TEXT, zorder=3, wrap=True)


def arrow(ax, p1, p2, label=None, color="#34495e", rad=0.0):
    ax.add_patch(FancyArrowPatch(p1, p2, arrowstyle="-|>", mutation_scale=16,
                 color=color, lw=1.4, zorder=1,
                 connectionstyle=f"arc3,rad={rad}"))
    if label:
        mx, my = (p1[0] + p2[0]) / 2, (p1[1] + p2[1]) / 2
        ax.text(mx + 0.15, my, label, fontsize=8.5, color=color,
                fontweight="bold", ha="left", va="center",
                bbox=dict(boxstyle="round,pad=0.15", fc="white", ec="none"))


def build_flowchart():
    fig, ax = plt.subplots(figsize=(8.5, 12))
    ax.set_xlim(0, 8.5)
    ax.set_ylim(0, 12)
    ax.axis("off")
    fig.suptitle("Flowchart — Proses Peminjaman Buku",
                 fontsize=15, fontweight="bold", color=HEADER, y=0.95)

    W, Hc = 3.6, 0.7
    cx = 3.0
    fbox(ax, cx, 11.2, 1.8, 0.7, "Mulai", "round")
    fbox(ax, cx, 10.0, 4.2, 0.8, "Input ID Buku & ID Anggota", "io")
    fbox(ax, cx, 8.6, 4.4, 1.0, "Buku & Anggota\nditemukan?", "diamond")
    fbox(ax, cx, 6.9, 3.6, 1.0, "Stok buku > 0 ?", "diamond")
    fbox(ax, cx, 5.3, 4.0, 0.8, "Kurangi stok buku\n(stok = stok - 1)", "rect")
    fbox(ax, cx, 4.0, 4.4, 0.9, "Buat objek Peminjaman\n+ catat tanggal pinjam", "rect")
    fbox(ax, cx, 2.7, 4.4, 0.8, "Simpan ke\ndaftarPeminjaman (ArrayList)", "rect")
    fbox(ax, cx, 1.4, 2.0, 0.7, "Selesai", "round")

    # jalur gagal (kanan)
    ex = 6.8
    fbox(ax, ex, 7.7, 2.8, 1.0, "Tampilkan pesan\nerror / GAGAL", "rect",
         fc="#fadbd2", ec="#c0392b")

    # panah alur utama
    arrow(ax, (cx, 10.85), (cx, 10.4))
    arrow(ax, (cx, 9.6), (cx, 9.1))
    arrow(ax, (cx, 8.1), (cx, 7.4), "Ya")
    arrow(ax, (cx, 6.4), (cx, 5.7), "Ya")
    arrow(ax, (cx, 4.9), (cx, 4.45))
    arrow(ax, (cx, 3.55), (cx, 3.1))
    arrow(ax, (cx, 2.3), (cx, 1.75))

    # cabang Tidak -> error
    arrow(ax, (cx + 2.2, 8.6), (ex, 8.2), "Tidak", color="#c0392b")
    arrow(ax, (cx + 1.8, 6.9), (ex, 7.3), "Tidak", color="#c0392b", rad=-0.2)
    # error -> selesai
    ax.add_patch(FancyArrowPatch((ex, 7.2), (ex, 1.4), arrowstyle="-|>",
                 mutation_scale=16, color="#c0392b", lw=1.4,
                 connectionstyle="arc3,rad=0.0", zorder=1))
    arrow(ax, (ex, 1.4), (cx + 1.0, 1.4), color="#c0392b")

    fig.savefig("flowchart_peminjaman.png", dpi=170, bbox_inches="tight")
    plt.close(fig)
    print("OK flowchart_peminjaman.png")


if __name__ == "__main__":
    build_uml()
    build_flowchart()
