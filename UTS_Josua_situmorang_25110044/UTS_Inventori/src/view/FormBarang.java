package view;

import dao.BarangDAO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Barang;

public class FormBarang extends JFrame {

    private final JTextField txtKode = new JTextField(15);
    private final JTextField txtNama = new JTextField(15);
    private final JComboBox<String> cmbKategori =
            new JComboBox<>(new String[]{"Komputer", "Jaringan", "Alat Ukur"});
    private final JTextField txtStok = new JTextField(15);

    private final JButton btnSimpan    = new JButton("Simpan");
    private final JButton btnUbah      = new JButton("Ubah");
    private final JButton btnHapus     = new JButton("Hapus");
    private final JButton btnBersihkan = new JButton("Bersihkan");

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Kode Barang", "Nama Barang", "Kategori", "Stok"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabel = new JTable(tableModel);

    private final BarangDAO dao = new BarangDAO();

    public FormBarang() {
        setTitle("Form Inventori Barang");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initComponents();
        initEvents();
        loadTable();           // data otomatis tampil saat aplikasi dijalankan
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Data Barang"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        String[] label = {"Kode Barang", "Nama Barang", "Kategori", "Stok"};
        JComponent[] input = {txtKode, txtNama, cmbKategori, txtStok};
        for (int i = 0; i < label.length; i++) {
            g.gridx = 0; g.gridy = i; g.weightx = 0;
            form.add(new JLabel(label[i]), g);
            g.gridx = 1; g.weightx = 1;
            form.add(input[i], g);
        }

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        tombol.add(btnSimpan);
        tombol.add(btnUbah);
        tombol.add(btnHapus);
        tombol.add(btnBersihkan);

        JPanel atas = new JPanel(new BorderLayout());
        atas.add(form, BorderLayout.CENTER);
        atas.add(tombol, BorderLayout.SOUTH);

        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabel);
        scroll.setPreferredSize(new Dimension(520, 220));

        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(atas, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private void initEvents() {
        btnSimpan.addActionListener(e -> simpan());
        btnUbah.addActionListener(e -> ubah());
        btnHapus.addActionListener(e -> hapus());
        btnBersihkan.addActionListener(e -> bersihkan());

        // Klik baris -> data muncul di form
        tabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tabel.getSelectedRow();
                if (row < 0) return;
                txtKode.setText(tableModel.getValueAt(row, 0).toString());
                txtNama.setText(tableModel.getValueAt(row, 1).toString());
                cmbKategori.setSelectedItem(tableModel.getValueAt(row, 2).toString());
                txtStok.setText(tableModel.getValueAt(row, 3).toString());
                txtKode.setEditable(false);   // kode adalah acuan Ubah/Hapus
            }
        });
    }

    // Ambil data dari DAO lalu isi ulang JTable
    private void loadTable() {
        tableModel.setRowCount(0);
        List<Barang> list = dao.tampilData();
        for (Barang b : list) {
            tableModel.addRow(new Object[]{
                b.getKodeBarang(), b.getNamaBarang(), b.getKategori(), b.getStok()});
        }
    }

    private void bersihkan() {
        txtKode.setText("");
        txtNama.setText("");
        cmbKategori.setSelectedIndex(0);
        txtStok.setText("");
        txtKode.setEditable(true);
        tabel.clearSelection();
        txtKode.requestFocus();
    }

    // Validasi + pembuatan objek Barang; return null jika tidak valid
    private Barang validasiInput() {
        String kode = txtKode.getText().trim();
        String nama = txtNama.getText().trim();
        String stokStr = txtStok.getText().trim();

        if (kode.isEmpty()) {
            pesanValidasi("Kode barang tidak boleh kosong!", txtKode);
            return null;
        }
        if (nama.isEmpty()) {
            pesanValidasi("Nama barang tidak boleh kosong!", txtNama);
            return null;
        }
        int stok;
        try {
            stok = Integer.parseInt(stokStr);
        } catch (NumberFormatException ex) {
            pesanValidasi("Stok harus berupa angka!", txtStok);
            return null;
        }
        if (stok < 0) {
            pesanValidasi("Stok tidak boleh kurang dari 0!", txtStok);
            return null;
        }
        return new Barang(kode, nama, cmbKategori.getSelectedItem().toString(), stok);
    }

    private void pesanValidasi(String pesan, JComponent fokus) {
        JOptionPane.showMessageDialog(this, pesan, "Validasi", JOptionPane.WARNING_MESSAGE);
        fokus.requestFocus();
    }

    private void simpan() {
        Barang b = validasiInput();
        if (b == null) return;
        if (dao.simpan(b)) {
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan!");
            loadTable();
            bersihkan();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Data gagal disimpan! (kode barang mungkin sudah ada)",
                    "Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ubah() {
        Barang b = validasiInput();
        if (b == null) return;
        if (dao.ubah(b)) {
            JOptionPane.showMessageDialog(this, "Data berhasil diubah!");
            loadTable();
            bersihkan();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Data gagal diubah! Pastikan kode barang sudah ada di database.",
                    "Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapus() {
        String kode = txtKode.getText().trim();
        if (kode.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data di tabel yang akan dihapus!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus data ini?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (konfirmasi != JOptionPane.YES_OPTION) return;

        if (dao.hapus(kode)) {
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
            loadTable();
            bersihkan();
        } else {
            JOptionPane.showMessageDialog(this, "Data gagal dihapus!",
                    "Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FormBarang().setVisible(true));
    }
}
