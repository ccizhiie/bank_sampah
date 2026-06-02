package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.List;

public class MainGUI extends JFrame {
    private NasabahController controller;

    private JTextField txtUsername, txtNama, txtNoHp;
    private JPasswordField txtPassword;
    private JTextArea txtAlamat;
    private JFormattedTextField txtNik;
    private JButton btnDaftar;

    private JTextField txtCariNik;
    private JButton btnCari;
    private JTextArea txtHasilPencarian;

    private JTable tableKyc;
    private DefaultTableModel tableModel;
    private JButton btnApprove, btnReject, btnRefresh;

    public MainGUI() {
        controller = new NasabahController();
        initComponent();
        refreshTableKyc();
    }

    private void initComponent() {
        setTitle("Sistem Bank Sampah - Tim 3 Core Nasabah (Async Mode)");
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // TAB 1: REGISTRASI & KYC DIGITAL
        JPanel panelRegistrasi = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);
        txtNama = new JTextField(15);
        txtNoHp = new JTextField(15);
        txtAlamat = new JTextArea(3, 15);
        txtAlamat.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        try {
            MaskFormatter maskNik = new MaskFormatter("################");
            maskNik.setPlaceholderCharacter('_');
            txtNik = new JFormattedTextField(maskNik);
        } catch (ParseException e) {
            txtNik = new JFormattedTextField();
        }

        addFormRow(panelRegistrasi, "Username:", txtUsername, gbc, 0);
        addFormRow(panelRegistrasi, "Password:", txtPassword, gbc, 1);
        addFormRow(panelRegistrasi, "NIK (16 Digit):", txtNik, gbc, 2);
        addFormRow(panelRegistrasi, "Nama Lengkap:", txtNama, gbc, 3);
        addFormRow(panelRegistrasi, "No HP:", txtNoHp, gbc, 4);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        panelRegistrasi.add(new JLabel("Alamat:"), gbc);
        gbc.gridx = 1;
        panelRegistrasi.add(new JScrollPane(txtAlamat), gbc);

        btnDaftar = new JButton("Daftar & Ajukan KYC");
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        panelRegistrasi.add(btnDaftar, gbc);

        // TAB 2: PENCARIAN TINGKAT LANJUT
        JPanel panelPencarian = new JPanel(new BorderLayout(10, 10));
        panelPencarian.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelCariInput = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelCariInput.add(new JLabel("Masukkan NIK Nasabah:"));
        txtCariNik = new JTextField(16);
        btnCari = new JButton("Cari Instan (RAM)");
        panelCariInput.add(txtCariNik);
        panelCariInput.add(btnCari);

        txtHasilPencarian = new JTextArea();
        txtHasilPencarian.setEditable(false);
        txtHasilPencarian.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtHasilPencarian.setBorder(BorderFactory.createTitledBorder("Hasil Pencarian di Memori"));

        panelPencarian.add(panelCariInput, BorderLayout.NORTH);
        panelPencarian.add(new JScrollPane(txtHasilPencarian), BorderLayout.CENTER);

        // TAB 3: VERIFIKASI DATA & STATUS KYC
        JPanel panelVerifikasi = new JPanel(new BorderLayout(10, 10));
        panelVerifikasi.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] kolom = {"ID", "NIK", "Nama Lengkap", "Status KYC"};
        tableModel = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableKyc = new JTable(tableModel);
        JScrollPane scrollTable = new JScrollPane(tableKyc);

        JPanel panelTombolAdmin = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnApprove = new JButton("Setujui (APPROVE)");
        btnApprove.setBackground(new Color(46, 204, 113));
        btnApprove.setForeground(Color.WHITE);

        btnReject = new JButton("Tolak (REJECT)");
        btnReject.setBackground(new Color(231, 76, 60));
        btnReject.setForeground(Color.WHITE);

        btnRefresh = new JButton("Refresh Data");

        panelTombolAdmin.add(btnRefresh);
        panelTombolAdmin.add(btnReject);
        panelTombolAdmin.add(btnApprove);

        panelVerifikasi.add(new JLabel("Daftar Pengajuan KYC Nasabah (Status: PENDING):"), BorderLayout.NORTH);
        panelVerifikasi.add(scrollTable, BorderLayout.CENTER);
        panelVerifikasi.add(panelTombolAdmin, BorderLayout.SOUTH);

        tabbedPane.addTab("Registrasi Nasabah Baru", panelRegistrasi);
        tabbedPane.addTab("Pencarian Tingkat Lanjut", panelPencarian);
        tabbedPane.addTab("Verifikasi & Status KYC (Admin)", panelVerifikasi);

        add(tabbedPane);

        // ========================================================
        // IMPLEMENTASI MATERI BARU: SwingWorker (Async Process)
        // ========================================================

        // 1. Aksi Registrasi Menggunakan SwingWorker
        btnDaftar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText().trim();
                String password = new String(txtPassword.getPassword()).trim();
                String nik = txtNik.getText().replace("_", "").trim();
                String nama = txtNama.getText().trim();
                String noHp = txtNoHp.getText().trim();
                String alamat = txtAlamat.getText().trim();

                if (username.isEmpty() || password.isEmpty() || nik.length() < 16 || nama.isEmpty()) {
                    JOptionPane.showMessageDialog(MainGUI.this, "Mohon lengkapi data dengan benar!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                btnDaftar.setEnabled(false); // Matikan tombol agar tidak diklik 2x (Sesuai slide materi baru)

                // Jalankan SwingWorker agar pendaftaran ke database tidak membuat GUI freeze
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    private boolean isSuccess = false;
                    private String errorMessage = "";

                    @Override
                    protected Void doInBackground() throws Exception {
                        try {
                            controller.registrasiNasabah(username, password, nik, nama, alamat, noHp);
                            isSuccess = true;
                        } catch (DuplicateNIKException ex) {
                            errorMessage = ex.getMessage();
                        } catch (Exception ex) {
                            errorMessage = "Database Error: " + ex.getMessage();
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        btnDaftar.setEnabled(true); // Hidupkan kembali tombol di EDT Thread Utama
                        if (isSuccess) {
                            JOptionPane.showMessageDialog(MainGUI.this, "Registrasi Sukses! Data masuk antrean KYC.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                            clearForm();
                            refreshTableKyc();
                        } else {
                            JOptionPane.showMessageDialog(MainGUI.this, errorMessage, "Pendaftaran Gagal", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };
                worker.execute(); // Eksekusi pekerjaan di Background Thread
            }
        });

        // 2. Cari Instan (Langsung dari RAM via HashMap, prosesnya instan jadi aman tanpa SwingWorker)
        btnCari.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nikCari = txtCariNik.getText().trim();
                if (nikCari.isEmpty()) return;

                Nasabah n = controller.cariNasabahByNIK(nikCari);
                if (n != null) {
                    txtHasilPencarian.setText(
                        "=== DATA NASABAH DI TEMUKAN DI RAM ===\n" +
                        "NIK          : " + n.getNik() + "\n" +
                        "Nama Lengkap : " + n.getNamaLengkap() + "\n" +
                        "Status KYC   : " + n.getStatusKyc() + "\n" +
                        "======================================"
                    );
                } else {
                    txtHasilPencarian.setText("Nasabah dengan NIK [" + nikCari + "] Tidak Ditemukan.");
                }
            }
        });

        // 3. Aksi Approve dengan SwingWorker
        btnApprove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableKyc.getSelectedRow();
                if (selectedRow == -1) return;

                int idNasabah = (int) tableModel.getValueAt(selectedRow, 0);
                String nik = (String) tableModel.getValueAt(selectedRow, 1);

                btnApprove.setEnabled(false);

                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        controller.updateStatusKyc(idNasabah, nik, "APPROVED");
                        return null;
                    }

                    @Override
                    protected void done() {
                        btnApprove.setEnabled(true);
                        JOptionPane.showMessageDialog(MainGUI.this, "Nasabah APPROVED!");
                        refreshTableKyc();
                    }
                };
                worker.execute();
            }
        });

        // 4. Aksi Reject dengan SwingWorker
        btnReject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableKyc.getSelectedRow();
                if (selectedRow == -1) return;

                int idNasabah = (int) tableModel.getValueAt(selectedRow, 0);
                String nik = (String) tableModel.getValueAt(selectedRow, 1);

                btnReject.setEnabled(false);

                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        controller.updateStatusKyc(idNasabah, nik, "REJECTED");
                        return null;
                    }

                    @Override
                    protected void done() {
                        btnReject.setEnabled(true);
                        JOptionPane.showMessageDialog(MainGUI.this, "Nasabah REJECTED!");
                        refreshTableKyc();
                    }
                };
                worker.execute();
            }
        });

        btnRefresh.addActionListener(e -> refreshTableKyc());
    }

    private void refreshTableKyc() {
        tableModel.setRowCount(0);
        List<Nasabah> listPending = controller.getAllPendingNasabah();
        for (Nasabah n : listPending) {
            tableModel.addRow(new Object[]{n.getIdNasabah(), n.getNik(), n.getNamaLengkap(), n.getStatusKyc()});
        }
    }

    private void addFormRow(JPanel panel, String labelText, Component comp, GridBagConstraints gbc, int y) {
        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1; panel.add(comp, gbc);
    }

    private void clearForm() {
        txtUsername.setText(""); txtPassword.setText(""); txtNik.setText("");
        txtNama.setText(""); txtNoHp.setText(""); txtAlamat.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainGUI().setVisible(true);
        });
    }
}
