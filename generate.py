import pandas as pd
import random
from datetime import datetime, timedelta

# 1. Definisikan Kolom Sesuai File Asli
columns = [
    "Timestamp", "Nama atau Inisial:", "Jenjang Pendidikan Saat Ini:",
    "Apakah kamu pernah mencoba belajar coding/pemrograman sebelumnya?",
    "Hambatan Saat Belajar Coding [Saya bingung harus mulai belajar coding dari mana karena materinya terlalu banyak di internet.]",
    "Hambatan Saat Belajar Coding [Penjelasan materi coding di internet sering kali terlalu rumit dan membingungkan.]",
    "Hambatan Saat Belajar Coding [Menghafal rumus tulisan kode (seperti tanda kurung, titik koma, simbol) sangat menyulitkan bagi saya.]",
    "Hambatan Saat Belajar Coding [Mencari bagian kode yang salah saat program komputer eror (debugging) membuat saya malas melanjutkan.]",
    "Hambatan Saat Belajar Coding [Pesan eror komputer yang menggunakan istilah bahasa Inggris yang rumit membuat saya stres dan merasa gagal.]",
    "Solusi & Fitur Impianmu [Saya lebih suka materi coding yang disusun berurutan lewat peta jalan (roadmap) yang jelas.]",
    "Solusi & Fitur Impianmu [Saya ingin langsung melihat hasil dari kode yang saya buat secara langsung di layar saat itu juga.]",
    "Solusi & Fitur Impianmu [Saya ingin pesan eror memberikan petunjuk arah yang ramah, membimbing, dan tetap memotivasi saya.]",
    "Solusi & Fitur Impianmu [Saya lebih semangat belajar jika ada sistem hadiah, level, lencana, dan papan peringkat kompetisi dengan teman.]",
    "Solusi & Fitur Impianmu [Memiliki teman belajar virtual (robot pendamping/maskot) di dalam aplikasi akan mengurangi rasa bosan saya.]",
    "Ceritakan pengalaman paling menyebalkan atau membuat frustrasi yang pernah kamu alami saat belajarmu eror atau saat mencoba memahami coding! (Jika belum pernah belajar, apa ketakutan terbesarmu sebelum mulai belajar coding?)",
    "Preferensi Visual & Antarmuka (UI/UX Expectation) [Saya lebih menyukai aplikasi belajar yang menggunakan warna cerah namun menenangkan (seperti kombinasi warna biru dan teal/hijau kebiruan) agar bisa tetap fokus.]",
    "Preferensi Visual & Antarmuka (UI/UX Expectation) [Saya lebih suka desain aplikasi yang modern, bersih, banyak ruang kosong, dan tidak terlalu padat penuh teks (clean design).]",
    "Preferensi Visual & Antarmuka (UI/UX Expectation) [Saya merasa bentuk tombol atau kotak materi yang membulat (rounded corners) terasa lebih ramah dan tidak kaku dibandingkan bentuk kotak yang bersudut tajam.]",
    "Preferensi Visual & Antarmuka (UI/UX Expectation) [Saya menyukai aplikasi yang memiliki pilihan Mode Gelap (Dark Mode) karena saya sering belajar atau membuka HP di malam hari.]",
    "Preferensi Visual & Antarmuka (UI/UX Expectation) [Saya merasa penggunaan gambar ikon/simbol yang tebal dan jelas jauh lebih membantu saya memahami fungsi menu daripada teks penjelasan yang panjang.]",
    "Preferensi Visual & Antarmuka (UI/UX Expectation) [Saya lebih mudah memahami data perkembangan belajar saya jika ditampilkan dalam bentuk grafik visual (seperti grafik batang atau lingkaran progres) daripada angka-angka saja.]",
    "Ketika belajar menggunakan aplikasi baru di HP atau laptop, hal apa yang paling sering membuatmu bingung atau malas melanjutkan?",
    "Tuliskan aplikasi belajar (apa pun itu, tidak harus aplikasi coding) yang menurutmu memiliki tampilan paling bagus, rapi, dan paling nyaman saat kamu gunakan! Sebutkan juga alasan kenapa kamu menyukai tampilan aplikasi tersebut."
]

# 2. Bank Data untuk Variasi Jawaban
nama_list = ["Rian", "Siti", "Budi", "Alya", "Dika", "Putri", "Dimas", "Nabila", "Fajar", "Rara",
             "Andi", "Salsa", "Gilang", "Vina", "Tio", "Amel", "Roni", "Dhea", "Arif", "Intan",
             "Rizky", "Eka", "Fadel", "Tiara", "Hafiz", "Anisa", "Bayu", "Laras", "Zaki", "Mia"]
jenjang_options = ["SMA / SMK / Sederajat", "Perguruan Tinggi (D3/D4/S1)"]
pernah_options = ["Pernah, baru mencoba dasar-dasarnya", "Belum pernah sama sekali", "Pernah, sedang aktif belajar saat ini"]

skala_likert = ["1 (Sangat Tidak Setuju)", "2 (Tidak Setuju)", "3 (Netral)", "4 (Setuju)", "5 (Sangat Setuju)"]

cerita_frustrasi = [
    "Eror titik koma kurang satu tapi nyarinya sampai dua jam, bikin pusing.",
    "Nanya ke AI tapi jawabannya malah muter-muter dan kodenya dibatasi limit gratisan.",
    "Pesan erornya panjang banget pakai bahasa Inggris dewa, gak paham maksudnya apa.",
    "Takut gak bakalan paham karena dasarnya bukan anak matematika atau SMK komputer.",
    "Udah ngikutin tutorial di YouTube step-by-step tapi pas dijalankan di laptop sendiri tetep eror.",
    "Bingung nyari eror di baris mana, aplikasinya langsung nge-crash tanpa petunjuk jelas.",
    "Materi di internet lompat-lompat, baru belajar dasar besoknya udah disuruh bikin projek ribet.",
    "Sering typo nulis sintaks terus nyari salahnya setengah mati.",
    "Gak tahu cara instalasi *environment*-nya, baru mulai udah eror duluan di terminal.",
    "Waktu nyoba nyontek kode dari Github malah versinya gak cocok dan eror semua."
]

hal_bingung = [
    "Alur perpindahan halaman yang membingungkan dan tombol menu yang sulit dicari.",
    "Terlalu banyak teks penjelasan di satu layar, bikin mata capek membaca.",
    "Menu navigasinya sembunyi-sembunyi, jadi bingung harus klik yang mana.",
    "Iklan pop-up yang terlalu banyak mendadak muncul di tengah layar.",
    "Gak ada petunjuk awal (onboarding) pas pertama kali buka aplikasi.",
    "Loading-nya lama dan transisi halatmannya kaku banget.",
    "Warna aplikasinya terlalu nabrak atau kontrasnya bikin sakit mata.",
    "Ukuran font-nya terlalu kecil dan gak bisa dizoom."
]

aplikasi_favorit = [
    ("Duolingo", "Karna interaktif, banyak animasi lucu, dan warna clean jadinya belajar berasa main game."),
    ("Ruangguru", "Tampilannya rapi, materi disusun berurutan, dan grafik progres belajarnya jelas."),
    ("Skilleo", "Desainnya minimalis, gak banyak teks padat, mudah dipahami menunya."),
    ("Canva", "Tombol-tombolnya gampang dicari, bentuk rounded-nya estetik dan modern."),
    ("Sololearn", "Simpel, langsung bisa nyoba ketik kode di HP tanpa ribet setup."),
    ("Notion", "Clean design, banyak ruang kosong, kerasa produktif kalau dipakai.")
]

# 3. Generate 100 Data Responden
data = []
start_time = datetime(2026, 5, 25, 11, 5, 0)

for i in range(100):
    # Simulasi waktu pengisian yang bertahap
    current_timestamp = (start_time + timedelta(minutes=random.randint(2, 15) * i)).strftime("%d/%m/%Y %H:%M:%S")
    nama = random.choice(nama_list) + str(random.randint(10, 99))
    jenjang = random.choice(jenjang_options)
    pernah = random.choice(pernah_options)

    # Membuat bobot agar jawaban cenderung setuju/sangat setuju sesuai hipotesis masalah aplikasi
    h1 = random.choice(skala_likert[2:]) # Bingung mulai dari mana
    h2 = random.choice(skala_likert[2:]) # Penjelasan rumit
    h3 = random.choice(skala_likert[1:]) # Menghafal rumus
    h4 = random.choice(skala_likert[3:]) # Malas debugging (cenderung sangat setuju)
    h5 = random.choice(skala_likert[2:]) # Pesan eror inggris bikin stres

    # Fitur Impian (Cenderung sangat setuju)
    s1 = random.choice(skala_likert[3:]) # Roadmap
    s2 = random.choice(skala_likert[3:]) # Hasil langsung di layar
    s3 = random.choice(skala_likert[3:]) # Pesan eror ramah
    s4 = random.choice(skala_likert[2:]) # Gamifikasi
    s5 = random.choice(skala_likert[2:]) # Maskot/Robot

    cerita = random.choice(cerita_frustrasi)

    # UI/UX Expectation
    u1 = random.choice(skala_likert[2:]) # Biru & Teal
    u2 = random.choice(skala_likert[3:]) # Clean design
    u3 = random.choice(skala_likert[3:]) # Rounded corners
    u4 = random.choice(skala_likert[3:]) # Dark mode
    u5 = random.choice(skala_likert[2:]) # Ikon tebal
    u6 = random.choice(skala_likert[3:]) # Grafik kemajuan

    bingung = random.choice(hal_bingung)
    app_fav = random.choice(aplikasi_favorit)
    app_nama_alasan = f"Saya suka {app_fav[0]}, {app_fav[1]}"

    row = [
        current_timestamp, nama, jenjang, pernah,
        h1, h2, h3, h4, h5,
        s1, s2, s3, s4, s5,
        cerita,
        u1, u2, u3, u4, u5, u6,
        bingung, app_nama_alasan
    ]
    data.append(row)

# 4. Simpan ke File CSV Baru
df_generated = pd.DataFrame(data, columns=columns)
# Menggabungkan data asli (Nasya) di baris pertama jika diperlukan
df_generated.to_csv("Hasil_100_Responden_Aplikasi_Coding.csv", index=False)
print("Berhasil mengenerasikan 100 data responden!")
