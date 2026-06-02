package src;

public class App {
    public static void main(String[] args) {
        System.out.println("Memulai Aplikasi Core Nasabah...");
        NasabahController controller = new NasabahController();

        try {
            // Test Kasus 1: Registrasi Nasabah Baru Sukses
            System.out.println("\n[Test 1] Mencoba registrasi data baru...");
            controller.registrasiNasabah(
                "andini27", "rahasia123", "3512345678900001",
                "Andini Lestari", "Kepanjen, Malang", "081234567890"
            );
            System.out.println("Hasil: Registrasi Berhasil!");

            // Test Kasus 2: Memicu DuplicateNIKException dengan NIK yang sama
            System.out.println("\n[Test 2] Mencoba duplikasi NIK yang sama...");
            controller.registrasiNasabah(
                "andini_palsu", "salah567", "3512345678900001",
                "Andini KW", "Surabaya", "089999999"
            );

        } catch (DuplicateNIKException e) {
            System.out.println("Hasil Terproteksi: " + e.getMessage()); // Menangkap exception buatan kita
        } catch (Exception e) {
            System.err.println("Error Sistem Lain: " + e.getMessage());
        }

        // Test Kasus 3: Pencarian Instan tingkat lanjut lewat HashMap
        System.out.println("\n[Test 3] Menguji Fitur Pencarian Instan...");
        String nikCari = "3512345678900001";
        Nasabah target = controller.cariNasabahByNIK(nikCari);

        if (target != null) {
            System.out.println("Data Ditemukan di RAM!");
            System.out.println("-> Nama   : " + target.getNamaLengkap());
            System.out.println("-> Status KYC: " + target.getStatusKyc());
        } else {
            System.out.println("Data dengan NIK tersebut tidak ada.");
        }
    }
}
