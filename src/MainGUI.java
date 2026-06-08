package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.util.List;

public class MainGUI extends JFrame {
    private NasabahController controller;
    private JTextField txtUsername, txtNama, txtNoHp, txtCariNik;
    private JPasswordField txtPassword;
    private JTextArea txtAlamat;
    private JFormattedTextField txtNik;
    private JButton btnDaftar, btnCari, btnResetCari, btnApprove, btnReject, btnRefresh, btnExportCsv;

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

        btnDaftar = new JButton("Daftar & Ajukan KYC");
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; panelRegistrasi.add(btnDaftar, gbc);

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
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new MainGUI().setVisible(true)); }
}
