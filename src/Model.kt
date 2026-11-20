import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeParseException

data class Tugas(
    val id: String,
    var judul: String,
    var mapel: String,
    var deskripsi: String,
    var tenggat: LocalDate,
    var selesai: Boolean = false
) {
    val status: String get() = if (selesai) "Selesai" else "Belum"
}

class TugasService(private val daftar: MutableList<Tugas> = mutableListOf()) {
    var pesan: String = ""
        private set

    val jumlah: Int get() = daftar.size

    fun semua(): List<Tugas> = daftar.sortedBy { it.tenggat }

    fun semuaData(): List<Tugas> = daftar.toList()

    fun tambah(id: String, judul: String, mapel: String, tenggatText: String, deskripsi: String = ""): Boolean {
        if (id.isBlank() || judul.isBlank()) {
            pesan = "ID dan judul wajib diisi"
            return false
        }
        if (daftar.any { it.id.equals(id, ignoreCase = true) }) {
            pesan = "ID sudah dipakai"
            return false
        }
        val tenggat = try {
            LocalDate.parse(tenggatText)
        } catch (e: DateTimeParseException) {
            pesan = "Format tenggat harus yyyy-MM-dd, contoh 2026-12-01"
            return false
        }
        if (tenggat.isBefore(LocalDate.now())) {
            pesan = "Tenggat tidak boleh sebelum hari ini"
            return false
        }
        daftar.add(Tugas(id.trim(), judul.trim(), mapel.trim(), deskripsi.trim(), tenggat))
        pesan = "Tugas berhasil ditambahkan"
        return true
    }

    fun cari(kataKunci: String): List<Tugas> {
        pesan = ""
        if (kataKunci.isBlank()) {
            pesan = "Kata kunci tidak boleh kosong"
            return emptyList()
        }
        return daftar.filter {
            it.judul.contains(kataKunci, ignoreCase = true) ||
                    it.mapel.contains(kataKunci, ignoreCase = true)
        }
    }

    fun ubahStatus(id: String, statusBaru: String): Boolean {
        val tugas = daftar.find { it.id.equals(id, ignoreCase = true) }
        if (tugas == null) {
            pesan = "ID tidak ditemukan"
            return false
        }
        when (statusBaru.trim().lowercase()) {
            "belum" -> tugas.selesai = false
            "selesai" -> tugas.selesai = true
            else -> {
                pesan = "Status hanya boleh Belum atau Selesai"
                return false
            }
        }
        pesan = "Status diperbarui"
        return true
    }

    fun hapus(id: String): Boolean {
        val berhasil = daftar.removeAll { it.id.equals(id, ignoreCase = true) }
        pesan = if (berhasil) "Tugas dihapus" else "ID tidak ditemukan"
        return berhasil
    }
}

object FileRepository {
    private const val PATH = "data/tugas.json"
    private val polaField = Regex("\"(\\w+)\"\\s*:\\s*(\"((?:[^\"\\\\]|\\\\.)*)\"|true|false)")

    private fun escape(s: String) =
        s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")

    private fun unescape(s: String) =
        Regex("\\\\(.)").replace(s) { if (it.groupValues[1] == "n") "\n" else it.groupValues[1] }

    fun simpan(daftar: List<Tugas>): Boolean {
        return try {
            val berkas = File(PATH)
            berkas.parentFile?.mkdirs()
            val isi = daftar.joinToString(",\n", "[\n", "\n]") {
                "  {\"id\":\"${escape(it.id)}\",\"judul\":\"${escape(it.judul)}\"," +
                        "\"mapel\":\"${escape(it.mapel)}\",\"deskripsi\":\"${escape(it.deskripsi)}\"," +
                        "\"tenggat\":\"${it.tenggat}\",\"selesai\":${it.selesai}}"
            }
            berkas.writeText(isi)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun muat(): MutableList<Tugas> {
        val hasil = mutableListOf<Tugas>()
        val berkas = File(PATH)
        if (!berkas.exists()) return hasil
        try {
            for (baris in berkas.readLines()) {
                val teks = baris.trim().trimEnd(',')
                if (!teks.startsWith("{")) continue
                val data = polaField.findAll(teks).associate { m ->
                    val mentah = m.groupValues[2]
                    m.groupValues[1] to (if (mentah.startsWith("\"")) unescape(m.groupValues[3]) else mentah)
                }
                val id = data["id"]
                val judul = data["judul"]
                val tenggat = try { LocalDate.parse(data["tenggat"]) } catch (e: Exception) { null }
                if (id.isNullOrBlank() || judul.isNullOrBlank() || tenggat == null) continue
                hasil.add(
                    Tugas(id, judul, data["mapel"] ?: "", data["deskripsi"] ?: "", tenggat, data["selesai"] == "true")
                )
            }
        } catch (e: Exception) {
            // berkas tidak bisa dibaca: mulai dengan daftar kosong, program tetap jalan
        }
        return hasil
    }
}
