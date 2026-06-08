package src;

public class App {

    public static void assertEquals(Object expected, Object actual, String namaPengujian) {
        if (expected == null && actual == null) {
            System.out.println("  [?] PASSED: " + namaPengujian);
        } else if (expected != null && expected.equals(actual)) {
            System.out.println("  [?] PASSED: " + namaPengujian);
        } else {
            System.out.println("  [X] FAILED: " + namaPengujian + " (Ekspektasi: " + expected + ", Aktual: " + actual + ")");
            throw new AssertionError("Pengujian Gagal!");
        }
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   MULAILAH AUTOMATED UNIT TESTING (THE SHIELD)   ");
        System.out.println("==================================================");

        NasabahController controller = new NasabahController();

        // 1. PENGUJIAN SKENARIO A
        try {
            Nasabah hasil = controller.cariNasabahByNIK("9999999999999999");
            assertEquals(null, hasil, "Uji Pencarian NIK Kosong Harus Menghasilkan Null");
        } catch (Exception e) {
            System.out.println("  [X] FAILED: Terjadi error pada Skenario A.");
        }

        // 2. PENGUJIAN SKENARIO B
        try {
            System.out.println("\nMencoba registrasi data nasabah pertama ke database...");
            controller.registrasiNasabah("userA", "pass1", "3512345678901111", "Budi", "Malang", "081");

            System.out.println("Mencoba registrasi data nasabah kedua dengan NIK ganda yang sama...");
            controller.registrasiNasabah("userB", "pass2", "3512345678901111", "Budi KW", "Surabaya", "082");

            System.out.println("  [X] FAILED: Sistem meloloskan data NIK duplikat!");
        } catch (DuplicateNIKException e) {
            System.out.println("  [?] PASSED: DuplicateNIKException Berhasil Dilempar!");
            System.out.println("  Pesan Blokir Sistem: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("  [X] FAILED: Menangkap error tipe lain: " + e.getMessage());
        }

        System.out.println("==================================================");
        System.out.println("      SELURUH UNIT TESTING SELESAI (PASSED)       ");
        System.out.println("==================================================");
    }
}
