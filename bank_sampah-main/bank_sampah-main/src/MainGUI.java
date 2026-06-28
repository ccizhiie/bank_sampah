package src;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

public class MainGUI extends JFrame {
    private NasabahController controller;
    private JTextField txtUsername, txtNama, txtNoHp, txtCariNik;
    private JPasswordField txtPassword;
    private JTextArea txtAlamat;
    private JFormattedTextField txtNik;
    private JButton btnDaftar, btnCari, btnResetCari, btnApprove, btnReject, btnRefresh, btnExportCsv;
    private JButton btnScanKtp;
    private JLabel lblPreviewKtp;

    // Tabel Operasional Admin
    private JTable tableKyc;
    private DefaultTableModel tableModelKyc;

    // Tabel Komponen Pencarian (Tab 2)
    private JTable tableSearch;
    private DefaultTableModel tableModelSearch;

    public MainGUI() {
        controller = new NasabahController();
        initComponent();
        refreshAllTables();
    }

    private void initComponent() {
        setTitle("Sistem Bank Sampah - Tim 3 Core Nasabah (Async Mode)");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // --------------------------------------------------------
        // TAB 1: FORM REGISTRASI
        // --------------------------------------------------------
        JPanel panelRegistrasi = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); gbc.fill = GridBagConstraints.HORIZONTAL;

        txtUsername = new JTextField(15); txtPassword = new JPasswordField(15);
        txtNama = new JTextField(15); txtNoHp = new JTextField(15);
        txtAlamat = new JTextArea(3, 15); txtAlamat.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        try {
            MaskFormatter maskNik = new MaskFormatter("################");
            maskNik.setPlaceholderCharacter('_');
            txtNik = new JFormattedTextField(maskNik);
        } catch (ParseException e) { txtNik = new JFormattedTextField(); }

        addFormRow(panelRegistrasi, "Username:", txtUsername, gbc, 0);
        addFormRow(panelRegistrasi, "Password:", txtPassword, gbc, 1);
        addFormRow(panelRegistrasi, "NIK (16 Digit):", txtNik, gbc, 2);
        addFormRow(panelRegistrasi, "Nama Lengkap:", txtNama, gbc, 3);
        addFormRow(panelRegistrasi, "No HP:", txtNoHp, gbc, 4);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1; panelRegistrasi.add(new JLabel("Alamat:"), gbc);
        gbc.gridx = 1; panelRegistrasi.add(new JScrollPane(txtAlamat), gbc);

        btnScanKtp = new JButton("Scan KTP");
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1; panelRegistrasi.add(btnScanKtp, gbc);

        lblPreviewKtp = new JLabel("Preview KTP", SwingConstants.CENTER);
        lblPreviewKtp.setPreferredSize(new Dimension(150, 100));
        lblPreviewKtp.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        gbc.gridx = 1; gbc.gridy = 6; panelRegistrasi.add(lblPreviewKtp, gbc);

        btnDaftar = new JButton("Daftar & Ajukan KYC");
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; panelRegistrasi.add(btnDaftar, gbc);

        // --------------------------------------------------------
        // TAB 2: PENCARIAN GLOBAL & SINKRONISASI LIST
        // --------------------------------------------------------
        JPanel panelPencarian = new JPanel(new BorderLayout(10, 10));
        panelPencarian.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelCariInput = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtCariNik = new JTextField(14);
        btnCari = new JButton("Cari NIK");
        btnResetCari = new JButton("Tampilkan Semua");

        panelCariInput.add(new JLabel("Masukkan NIK Nasabah:"));
        panelCariInput.add(txtCariNik);
        panelCariInput.add(btnCari);
        panelCariInput.add(btnResetCari);

        String[] kolomSearch = {"ID", "NIK", "Nama Lengkap", "No HP", "Status KYC"};
        tableModelSearch = new DefaultTableModel(kolomSearch, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tableSearch = new JTable(tableModelSearch);

        panelPencarian.add(panelCariInput, BorderLayout.NORTH);
        panelPencarian.add(new JScrollPane(tableSearch), BorderLayout.CENTER);

        // --------------------------------------------------------
        // TAB 3: VERIFIKASI ADMIN (PENDING FILTER)
        // --------------------------------------------------------
        JPanel panelVerifikasi = new JPanel(new BorderLayout(10, 10));
        panelVerifikasi.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String[] kolomKyc = {"ID", "NIK", "Nama", "Status"};
        tableModelKyc = new DefaultTableModel(kolomKyc, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tableKyc = new JTable(tableModelKyc);

        JPanel panelTombolAdmin = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnApprove = new JButton("APPROVE"); btnApprove.setBackground(new Color(46, 204, 113)); btnApprove.setForeground(Color.WHITE);
        btnReject = new JButton("REJECT"); btnReject.setBackground(new Color(231, 76, 60)); btnReject.setForeground(Color.WHITE);
        btnRefresh = new JButton("Refresh Data");
        btnExportCsv = new JButton("Export CSV"); btnExportCsv.setBackground(new Color(52, 152, 219)); btnExportCsv.setForeground(Color.WHITE);

        panelTombolAdmin.add(btnRefresh);
        panelTombolAdmin.add(btnExportCsv);
        panelTombolAdmin.add(btnReject);
        panelTombolAdmin.add(btnApprove);
        panelVerifikasi.add(new JScrollPane(tableKyc), BorderLayout.CENTER); panelVerifikasi.add(panelTombolAdmin, BorderLayout.SOUTH);

        tabbedPane.addTab("Registrasi Nasabah Baru", panelRegistrasi);
        tabbedPane.addTab("Pencarian & Daftar Nasabah", panelPencarian);
        tabbedPane.addTab("Verifikasi & Status KYC (Admin)", panelVerifikasi);
        add(tabbedPane);

        // --------------------------------------------------------
        // ACTIONS AND EVENT LISTENERS
        // --------------------------------------------------------
        btnScanKtp.addActionListener(e -> {
            Object[] options = {"Upload Gambar", "Kamera"};
            int choice = JOptionPane.showOptionDialog(this, "Pilih sumber gambar KTP:", "Scan KTP",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            if (choice == 1) {
                bukaKamera();
                return;
            } else if (choice == 0) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png"));
                if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    tampilkanPreview(selectedFile);
                    prosesOcr(selectedFile);
                }
            }
        });

        btnDaftar.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();
            String nik = txtNik.getText().replace("_", "").trim();
            String nama = txtNama.getText().trim();
            String noHp = txtNoHp.getText().trim();
            String alamat = txtAlamat.getText().trim();

            if (username.isEmpty() || password.isEmpty() || nik.length() < 16 || nama.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Format data pendaftaran salah/belum lengkap!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            btnDaftar.setEnabled(false);
            new SwingWorker<Void, Void>() {
                private boolean success = false; private String err = "";
                @Override protected Void doInBackground() {
                    try { controller.registrasiNasabah(username, password, nik, nama, alamat, noHp); success = true; }
                    catch (DuplicateNIKException ex) { err = ex.getMessage(); }
                    catch (Exception ex) { err = ex.getMessage(); }
                    return null;
                }
                @Override protected void done() {
                    btnDaftar.setEnabled(true);
                    if (success) {
                        JOptionPane.showMessageDialog(MainGUI.this, "Pendaftaran Berhasil Diajukan!");
                        clearForm();
                        refreshAllTables();
                    } else {
                        JOptionPane.showMessageDialog(MainGUI.this, err, "Gagal Registrasi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });

        // Event Pencarian Dinamis
        btnCari.addActionListener(e -> {
            String targetNik = txtCariNik.getText().trim();
            if (targetNik.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Masukkan NIK yang ingin dicari!");
                return;
            }
            Nasabah n = controller.cariNasabahByNIK(targetNik);
            tableModelSearch.setRowCount(0); // Bersihkan tabel
            if (n != null) {
                tableModelSearch.addRow(new Object[]{n.getIdNasabah(), n.getNik(), n.getNamaLengkap(), n.getNoHp(), n.getStatusKyc()});
            } else {
                JOptionPane.showMessageDialog(this, "Nasabah dengan NIK tersebut tidak terdaftar di memori RAM.");
            }
        });

        btnResetCari.addActionListener(e -> {
            txtCariNik.setText("");
            refreshTableSearch();
        });

        btnApprove.addActionListener(e -> eksekusiStatusAdmin("APPROVED"));
        btnReject.addActionListener(e -> eksekusiStatusAdmin("REJECTED"));
        btnRefresh.addActionListener(e -> refreshAllTables());

        btnExportCsv.addActionListener(e -> {
            btnExportCsv.setEnabled(false);
            new SwingWorker<Void, Void>() {
                private boolean success = false;
                @Override protected Void doInBackground() throws Exception {
                    controller.exportToCSV("laporan_nasabah.csv");
                    success = true;
                    return null;
                }
                @Override protected void done() {
                    btnExportCsv.setEnabled(true);
                    if (success) {
                        JOptionPane.showMessageDialog(MainGUI.this, "Sukses mengekspor memori RAM ke file 'laporan_nasabah.csv'!", "Ekspor Sukses", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(MainGUI.this, "Gagal memproses file CSV.", "Error File IO", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });
    }

    private void eksekusiStatusAdmin(String status) {
        int row = tableKyc.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Pilih salah satu baris nasabah terlebih dahulu!"); return; }
        int id = (int) tableModelKyc.getValueAt(row, 0);
        String nik = (String) tableModelKyc.getValueAt(row, 1);

        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception { controller.updateStatusKyc(id, nik, status); return null; }
            @Override protected void done() {
                JOptionPane.showMessageDialog(MainGUI.this, "Nasabah NIK ["+nik+"] Berhasil Diubah ke Status: " + status);
                refreshAllTables();
            }
        }.execute();
    }

    private void refreshAllTables() {
        refreshTableKyc();
        refreshTableSearch();
    }

    private void refreshTableKyc() {
        tableModelKyc.setRowCount(0);
        List<Nasabah> list = controller.getAllPendingNasabah();
        for (Nasabah n : list) tableModelKyc.addRow(new Object[]{n.getIdNasabah(), n.getNik(), n.getNamaLengkap(), n.getStatusKyc()});
    }

    private void refreshTableSearch() {
        tableModelSearch.setRowCount(0);
        List<Nasabah> list = controller.getAllNasabah();
        for (Nasabah n : list) tableModelSearch.addRow(new Object[]{n.getIdNasabah(), n.getNik(), n.getNamaLengkap(), n.getNoHp(), n.getStatusKyc()});
    }

    private void addFormRow(JPanel p, String l, Component c, GridBagConstraints g, int y) {
        g.gridwidth = 1; g.gridx = 0; g.gridy = y; p.add(new JLabel(l), g);
        g.gridx = 1; p.add(c, g);
    }

    private void clearForm() {
        txtUsername.setText(""); txtPassword.setText(""); txtNik.setText("");
        txtNama.setText(""); txtNoHp.setText(""); txtAlamat.setText("");
        if (lblPreviewKtp != null) {
            lblPreviewKtp.setIcon(null);
            lblPreviewKtp.setText("Preview KTP");
        }
    }

    private void tampilkanPreview(File file) {
        try {
            BufferedImage img = ImageIO.read(file);
            if (img != null) {
                Image scaledImg = img.getScaledInstance(150, 100, Image.SCALE_SMOOTH);
                lblPreviewKtp.setIcon(new ImageIcon(scaledImg));
                lblPreviewKtp.setText("");
            } else {
                throw new Exception("Format gambar tidak valid.");
            }
        } catch (Exception ex) {
            lblPreviewKtp.setText("Gagal muat preview");
            lblPreviewKtp.setIcon(null);
            JOptionPane.showMessageDialog(this, "Gagal memuat gambar: " + ex.getMessage(), "Error Gambar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bukaKamera() {
        System.setProperty("webcam.lock", "false"); // Fix: Disable webcam lock to avoid device errors
        JDialog loadingDialog = new JDialog(this, "Membuka Kamera", true);
        loadingDialog.setLayout(new BorderLayout());
        loadingDialog.add(new JLabel("Inisialisasi kamera, mohon tunggu...", SwingConstants.CENTER), BorderLayout.CENTER);
        loadingDialog.setSize(300, 100);
        loadingDialog.setLocationRelativeTo(this);

        SwingWorker<Webcam, Void> worker = new SwingWorker<Webcam, Void>() {
            @Override
            protected Webcam doInBackground() {
                return Webcam.getDefault();
            }

            @Override
            protected void done() {
                loadingDialog.dispose();
                try {
                    Webcam webcam = get();
                    if (webcam == null) {
                        JOptionPane.showMessageDialog(MainGUI.this, "Tidak ada kamera terdeteksi di sistem Anda.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    JDialog camDialog = new JDialog(MainGUI.this, "Scan KTP dari Kamera", true);
                    camDialog.setLayout(new BorderLayout());
                    
                    WebcamPanel panel = new WebcamPanel(webcam);
                    panel.setFPSDisplayed(true);
                    panel.setImageSizeDisplayed(true);
                    panel.setMirrored(false);
                    camDialog.add(panel, BorderLayout.CENTER);
                    
                    JButton btnCapture = new JButton("Ambil Foto & Scan");
                    btnCapture.setFont(new Font("Arial", Font.BOLD, 16));
                    btnCapture.setBackground(new Color(46, 204, 113));
                    btnCapture.setForeground(Color.WHITE);
                    btnCapture.setPreferredSize(new Dimension(100, 50));
                    btnCapture.addActionListener(e -> {
                        BufferedImage image = webcam.getImage();
                        if (image != null) {
                            try {
                                File tempFile = new File("ktp_cam_temp.png");
                                ImageIO.write(image, "PNG", tempFile);
                                camDialog.dispose();
                                webcam.close();
                                tampilkanPreview(tempFile);
                                prosesOcr(tempFile);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(camDialog, "Gagal menyimpan foto: " + ex.getMessage());
                            }
                        }
                    });
                    camDialog.add(btnCapture, BorderLayout.SOUTH);
                    
                    camDialog.pack();
                    camDialog.setLocationRelativeTo(MainGUI.this);
                    
                    camDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                            webcam.close();
                        }
                    });
                    
                    camDialog.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainGUI.this, "Gagal mengakses kamera: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
        loadingDialog.setVisible(true);
    }

    private void prosesOcr(File file) {
        JDialog loadingDialog = new JDialog(this, "Memproses OCR", true);
        loadingDialog.setLayout(new BorderLayout());
        loadingDialog.add(new JLabel("Sedang mengekstrak data KTP, mohon tunggu...", SwingConstants.CENTER), BorderLayout.CENTER);
        loadingDialog.setSize(300, 100);
        loadingDialog.setLocationRelativeTo(this);

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                // Memanggil command tesseract
                ProcessBuilder pb = new ProcessBuilder("tesseract", file.getAbsolutePath(), "stdout");
                pb.redirectErrorStream(true);
                Process process = pb.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                process.waitFor();
                return sb.toString();
            }

            @Override
            protected void done() {
                loadingDialog.dispose();
                try {
                    String ocrResult = get();
                    if (ocrResult == null || ocrResult.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(MainGUI.this, "Teks tidak terdeteksi. Pastikan gambar jelas dan tidak blur.", "OCR Gagal", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    parseDataKtp(ocrResult);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainGUI.this, "Gagal menjalankan OCR. Pastikan Tesseract telah terinstal. Detail: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
        loadingDialog.setVisible(true);
    }

    private void parseDataKtp(String ocrText) {
        String[] lines = ocrText.split("\n");
        String nik = "";
        String nama = "";
        String alamat = "";

        /*
         * CATATAN IMPLEMENTASI OCR:
         * Data lain yang diekstrak tetapi diabaikan karena tidak ada di tb_biodata:
         * - Tempat/Tgl Lahir
         * - Jenis Kelamin
         * - Gol. Darah
         * - RT/RW
         * - Kel/Desa
         * - Kecamatan
         * - Agama
         * - Status Perkawinan
         * - Pekerjaan
         * - Kewarganegaraan
         * - Berlaku Hingga
         * Data ini tidak disimpan atau dibuatkan kolom baru sesuai instruksi.
         */

        // Ekstraksi NIK (Mencari 16 digit angka berurutan)
        Matcher mNik = Pattern.compile("\\b\\d{16}\\b").matcher(ocrText.replaceAll("\\s", ""));
        if (mNik.find()) {
            nik = mNik.group();
        }

        for (String line : lines) {
            String upper = line.toUpperCase().trim();
            // Ekstraksi Nama
            if (upper.contains("NAMA") && !upper.contains("PROVINSI") && !upper.contains("KABUPATEN")) {
                String tempNama = upper.replaceAll(".*?NAMA\\s*[:=]?\\s*", "").trim();
                if (!tempNama.isEmpty() && nama.isEmpty()) {
                    nama = tempNama;
                }
            }
            // Ekstraksi Alamat
            else if (upper.contains("ALAMAT")) {
                String tempAlamat = upper.replaceAll(".*?ALAMAT\\s*[:=]?\\s*", "").trim();
                if (!tempAlamat.isEmpty() && alamat.isEmpty()) {
                    alamat = tempAlamat;
                }
            }
        }

        if (nik.isEmpty() && nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Data KTP tidak dikenali dengan baik. Harap periksa kejernihan gambar.", "Peringatan OCR", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!nik.isEmpty()) txtNik.setText(nik);
        if (!nama.isEmpty()) txtNama.setText(nama);
        if (!alamat.isEmpty()) txtAlamat.setText(alamat);

        JOptionPane.showMessageDialog(this, "Scan KTP Selesai! Data NIK, Nama, dan Alamat berhasil di-mapping. Silakan periksa atau edit kembali form.");
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new MainGUI().setVisible(true)); }
}
