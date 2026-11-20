fun tampilkanDaftar(daftar: List<Tugas>) {
    if (daftar.isEmpty()) {
        println("Belum ada tugas")
        return
    }
    for ((i, t) in daftar.withIndex()) {
        println("${i + 1}. ${t.id} | ${t.judul} | ${t.mapel} | ${t.tenggat} | ${t.status}")
    }
}

fun main() {
    val service = TugasService(FileRepository.muat())
    var pilihan: Int

    do {
        println("\n=== AGENDA TUGAS ===")
        println("1. Tambah tugas")
        println("2. Tampilkan tugas")
        println("3. Tentang aplikasi")
        println("4. Jumlah tugas")
        println("5. Simpan")
        println("6. Hapus tugas")
        println("7. Cari tugas")
        println("8. Ubah status")
        println("0. Keluar")

        print("Pilihan: ")
        pilihan = readln().toIntOrNull() ?: -1

        when (pilihan) {
            1 -> {
                print("ID tugas: ")
                val id = readln().trim()
                print("Judul tugas: ")
                val judul = readln().trim()
                print("Mata pelajaran: ")
                val mapel = readln().trim()
                print("Tenggat (yyyy-MM-dd): ")
                val tenggat = readln().trim()
                print("Deskripsi (boleh kosong): ")
                val deskripsi = readln().trim()
                service.tambah(id, judul, mapel, tenggat, deskripsi)
                println(service.pesan)
                if (service.pesan == "Tugas berhasil ditambahkan") FileRepository.simpan(service.semuaData())
            }
            2 -> tampilkanDaftar(service.semua())
            3 -> println("Aplikasi Agenda Tugas - Edward Galys Suganda")
            4 -> println("Jumlah tugas: ${service.jumlah}")
            5 -> println(if (FileRepository.simpan(service.semuaData())) "Data tersimpan" else "Gagal menyimpan data")
            6 -> {
                print("ID tugas yang dihapus: ")
                service.hapus(readln().trim())
                println(service.pesan)
                FileRepository.simpan(service.semuaData())
            }
            7 -> {
                print("Kata kunci: ")
                val hasil = service.cari(readln().trim())
                if (hasil.isEmpty()) {
                    println(if (service.pesan.startsWith("Kata kunci")) service.pesan else "Tidak ditemukan")
                } else {
                    tampilkanDaftar(hasil)
                }
            }
            8 -> {
                print("ID tugas: ")
                val id = readln().trim()
                print("Status baru (Belum/Selesai): ")
                val status = readln().trim()
                service.ubahStatus(id, status)
                println(service.pesan)
                FileRepository.simpan(service.semuaData())
            }
            0 -> {
                FileRepository.simpan(service.semuaData())
                println("Program selesai")
            }
            else -> println("Pilihan tidak tersedia")
        }
    } while (pilihan != 0)
}
