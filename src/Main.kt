fun main() {
    var pilihan: Int

    do {
        println("\n=== AGENDA TUGAS ===")
        println("1. Tambah tugas")
        println("2. Tampilkan tugas")
        println("0. Keluar")
        print("Pilihan: ")
        pilihan = readln().toIntOrNull() ?: -1

        when (pilihan) {
            1 -> println("Fitur tambah tugas belum tersedia")
            2 -> println("Fitur tampil tugas belum tersedia")
            0 -> println("Program selesai")
            else -> println("Pilihan tidak tersedia")
        }
    } while (pilihan != 0)
}