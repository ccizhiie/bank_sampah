package src;
import java.sql.*;
import java.util.HashMap;

public class NasabahController {
    // Port disesuaikan ke 3306 (Standar XAMPP)
    private static final String URL = "jdbc:mysql://localhost:3306/db_bank_sampah";
    private static final String USER = "root";
    private static final String PASSWORD = "";
// Tambahan Fitur Admin 1: Ambil semua nasabah yang berstatus PENDING untuk di-render di JTable
    public java.util.List<Nasabah> getAllPendingNasabah() {
        java.util.List<Nasabah> list = new java.util.ArrayList<>();
        // Kita filter langsung dari memori HashMap agar cepat
        for (Nasabah n : nasabahMap.values()) {
            if (n.getStatusKyc().equals("PENDING")) {
                list.add(n);
            }
        }
        return list;
    }

    // Tambahan Fitur Admin 2: Update status KYC ke Database & Sinkronisasi RAM HashMap
    public void updateStatusKyc(int idNasabah, String nik, String statusBaru) throws SQLException {
        String query = "UPDATE tb_status_kyc SET status = ?, tanggal_verifikasi = NOW(), diverifikasi_oleh = 'Admin_Tim_3' WHERE id_nasabah = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, statusBaru);
            ps.setInt(2, idNasabah);
            ps.executeUpdate();

            // SINKRONISASI MEMORI RAM: Update juga objek di HashMap supaya fitur pencarian ikut ter-update statusnya
            Nasabah n = nasabahMap.get(nik);
            if (n != null) {
                n.setStatusKyc(statusBaru);
            }
        }
    }
    // HashMap untuk pencarian instan berbasis memori (Key: NIK, Value: Objek Nasabah)
    private HashMap<String, Nasabah> nasabahMap;

    public NasabahController() {
        this.nasabahMap = new HashMap<>();
        loadDataToMemory(); // Otomatis sinkronisasi saat aplikasi dinyalakan
    }

    // Membuka koneksi ke XAMPP
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // FITUR 1: Sinkronisasi awal dari Database ke HashMap (Memori)
    private void loadDataToMemory() {
        String query = "SELECT n.id_nasabah, n.username, b.nik, b.nama_lengkap, b.alamat, b.no_hp, k.status " +
                       "FROM tb_nasabah n " +
                       "JOIN tb_biodata b ON n.id_nasabah = b.id_nasabah " +
                       "JOIN tb_status_kyc k ON n.id_nasabah = k.id_nasabah";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Nasabah nasabah = new Nasabah(
                    rs.getInt("id_nasabah"),
                    rs.getString("username"),
                    rs.getString("nik"),
                    rs.getString("nama_lengkap"),
                    rs.getString("alamat"),
                    rs.getString("no_hp"),
                    rs.getString("status")
                );
                // NIK dipasang sebagai Key di HashMap untuk pencarian instan
                nasabahMap.put(nasabah.getNik(), nasabah);
            }
            System.out.println("Sistem: " + nasabahMap.size() + " data nasabah berhasil dimuat ke HashMap.");

        } catch (SQLException e) {
            System.err.println("Gagal sinkronisasi ke memori: " + e.getMessage());
        }
    }

    // FITUR 2: Registrasi dengan Proteksi DuplicateNIKException (Mencegah Query Berulang)
    public void registrasiNasabah(String username, String password, String nik, String nama, String alamat, String noHp)
            throws DuplicateNIKException, SQLException {

        // Cek langsung di memori RAM via HashMap, tidak perlu hit database
        if (nasabahMap.containsKey(nik)) {
            throw new DuplicateNIKException("Gagal Registrasi: NIK [" + nik + "] sudah digunakan nasabah lain!");
        }

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Mode Transaksi diaktifkan (Mencegah data parsial jika crash)

            // 1. Amankan data ke tb_nasabah
            String sqlNasabah = "INSERT INTO tb_nasabah (username, password) VALUES (?, ?)";
            PreparedStatement psNasabah = conn.prepareStatement(sqlNasabah, Statement.RETURN_GENERATED_KEYS);
            psNasabah.setString(1, username);
            psNasabah.setString(2, password);
            psNasabah.executeUpdate();

            // Ambil Auto Increment ID yang baru dibuat oleh MySQL
            ResultSet rsKeys = psNasabah.getGeneratedKeys();
            int idNasabahBaru = 0;
            if (rsKeys.next()) {
                idNasabahBaru = rsKeys.getInt(1);
            }

            // 2. Amankan data ke tb_biodata (Menggunakan JFormattedTextField di GUI nantinya)
            String sqlBiodata = "INSERT INTO tb_biodata (id_nasabah, nik, nama_lengkap, alamat, no_hp) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psBiodata = conn.prepareStatement(sqlBiodata);
            psBiodata.setInt(1, idNasabahBaru);
            psBiodata.setString(2, nik);
            psBiodata.setString(3, nama);
            psBiodata.setString(4, alamat);
            psBiodata.setString(5, noHp);
            psBiodata.executeUpdate();

            // 3. Amankan status awal KYC (Default: PENDING)
            String sqlKyc = "INSERT INTO tb_status_kyc (id_nasabah, status) VALUES (?, 'PENDING')";
            PreparedStatement psKyc = conn.prepareStatement(sqlKyc);
            psKyc.setInt(1, idNasabahBaru);
            psKyc.executeUpdate();

            conn.commit(); // Eksekusi semua perubahan ke XAMPP secara permanen

            // 4. Sinkronisasi instan ke HashMap agar data baru bisa langsung dicari tanpa restart app
            Nasabah nasabahBaru = new Nasabah(idNasabahBaru, username, nik, nama, alamat, noHp, "PENDING");
            nasabahMap.put(nik, nasabahBaru);

        } catch (SQLException e) {
            if (conn != null) conn.rollback(); // Batalkan semua insert jika salah satu tabel gagal
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }

    // FITUR 3: Pencarian Tingkat Lanjut Instan O(1)
    public Nasabah cariNasabahByNIK(String nik) {
        return nasabahMap.get(nik); // Mengambil dari RAM, sangat instan
    }
}
