package src;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class NasabahController {
    private static final String URL = "jdbc:mysql://localhost:3306/db_bank_sampah";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private HashMap<String, Nasabah> nasabahMap;

    public NasabahController() {
        this.nasabahMap = new HashMap<>();
        loadDataToMemory();
    }

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC tidak ditemukan: " + e.getMessage());
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

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
                nasabahMap.put(nasabah.getNik(), nasabah);
            }
            System.out.println("Sistem: " + nasabahMap.size() + " data nasabah berhasil dimuat ke HashMap.");
        } catch (SQLException e) {
            System.err.println("Gagal memuat data ke RAM: " + e.getMessage());
        }
    }

    public void registrasiNasabah(String username, String password, String nik, String nama, String alamat, String noHp)
            throws DuplicateNIKException, SQLException {

        if (nasabahMap.containsKey(nik)) {
            throw new DuplicateNIKException("Gagal Registrasi: NIK [" + nik + "] sudah digunakan nasabah lain!");
        }

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            String sqlNasabah = "INSERT INTO tb_nasabah (username, password) VALUES (?, ?)";
            PreparedStatement psNasabah = conn.prepareStatement(sqlNasabah, Statement.RETURN_GENERATED_KEYS);
            psNasabah.setString(1, username);
            psNasabah.setString(2, password);
            psNasabah.executeUpdate();

            ResultSet rsKeys = psNasabah.getGeneratedKeys();
            int idNasabahBaru = 0;
            if (rsKeys.next()) {
                idNasabahBaru = rsKeys.getInt(1);
            }

            String sqlBiodata = "INSERT INTO tb_biodata (id_nasabah, nik, nama_lengkap, alamat, no_hp) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psBiodata = conn.prepareStatement(sqlBiodata);
            psBiodata.setInt(1, idNasabahBaru);
            psBiodata.setString(2, nik);
            psBiodata.setString(3, nama);
            psBiodata.setString(4, alamat);
            psBiodata.setString(5, noHp);
            psBiodata.executeUpdate();

            String sqlKyc = "INSERT INTO tb_status_kyc (id_nasabah, status) VALUES (?, 'PENDING')";
            PreparedStatement psKyc = conn.prepareStatement(sqlKyc);
            psKyc.setInt(1, idNasabahBaru);
            psKyc.executeUpdate();

            conn.commit();

            Nasabah nasabahBaru = new Nasabah(idNasabahBaru, username, nik, nama, alamat, noHp, "PENDING");
            nasabahMap.put(nik, nasabahBaru);

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }

    public Nasabah cariNasabahByNIK(String nik) {
        return nasabahMap.get(nik);
    }

    public List<Nasabah> getAllPendingNasabah() {
        List<Nasabah> list = new ArrayList<>();
        for (Nasabah n : nasabahMap.values()) {
            if (n.getStatusKyc().equals("PENDING")) {
                list.add(n);
            }
        }
        return list;
    }

    // METHOD BARU: MENYEDIAKAN LIST UNTUK TAB PENCARIAN
    public List<Nasabah> getAllNasabah() {
        return new ArrayList<>(nasabahMap.values());
    }

    public void updateStatusKyc(int idNasabah, String nik, String statusBaru) throws SQLException {
        String query = "UPDATE tb_status_kyc SET status = ?, tanggal_verifikasi = NOW(), diverifikasi_oleh = 'Admin_Tim_3' WHERE id_nasabah = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, statusBaru);
            ps.setInt(2, idNasabah);
            ps.executeUpdate();

            Nasabah n = nasabahMap.get(nik);
            if (n != null) n.setStatusKyc(statusBaru);
        }
    }

    public void exportToCSV(String filePath) throws IOException {
        try (FileWriter fw = new FileWriter(filePath);
             BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write("ID Nasabah,Username,NIK,Nama Lengkap,No HP,Alamat,Status KYC");
            bw.newLine();

            for (Nasabah n : nasabahMap.values()) {
                String baris = String.format("%d,%s,%s,%s,%s,%s,%s",
                    n.getIdNasabah(),
                    n.getUsername().replace(",", " "),
                    n.getNik(),
                    n.getNamaLengkap().replace(",", " "),
                    n.getNoHp(),
                    n.getAlamat().replace(",", " ").replace("\n", " "),
                    n.getStatusKyc()
                );
                bw.write(baris);
                bw.newLine();
            }
        }
    }
}
