fun main() {
    val daftar = mutableListOf<Tugas>()
    var pilihan: Int

    do {
        println("\n=== AGENDA TUGAS ===")
        println("1. Tambah tugas")
        println("2. Tampilkan tugas")
        println("0. Keluar")
        print("Pilihan: ")
        pilihan = readln().toIntOrNull() ?: -1

        when (pilihan) {
            1 -> {
                print("ID tugas: ")
                val id = readln().trim()
                print("Judul tugas: ")
                val judul = readln().trim()
                if (id.isBlank() || judul.isBlank()) {
                    println("Data tidak valid")
                } else if (daftar.any { it.id.equals(id, ignoreCase = true) }) {
                    println("ID duplikat")
                } else {
                    daftar.add(Tugas(id, judul))
                    println("Tugas ditambahkan")
                }
            }
            2 -> {
                if (daftar.isEmpty()) {
                    println("Belum ada tugas")
                } else {
                    for ((i, t) in daftar.withIndex()) {
                        val status = if (t.selesai) "Selesai" else "Belum"
                        println("${i + 1}. ${t.id} | ${t.judul} | $status")
                    }
                }
            }
            0 -> println("Program selesai")
            else -> println("Pilihan tidak tersedia")
        }
    } while (pilihan != 0)
}