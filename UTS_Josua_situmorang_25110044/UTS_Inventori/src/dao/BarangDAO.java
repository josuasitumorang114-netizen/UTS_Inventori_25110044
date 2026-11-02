package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Barang;

public class BarangDAO {

    // SIMPAN (INSERT)
    public boolean simpan(Barang b) {
        String sql = "INSERT INTO barang (kode_barang, nama_barang, kategori, stok) VALUES (?, ?, ?, ?)";
        try (Connection conn = Koneksi.getConnection()) {
            if (conn == null) return false;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, b.getKodeBarang());
                ps.setString(2, b.getNamaBarang());
                ps.setString(3, b.getKategori());
                ps.setInt(4, b.getStok());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Gagal simpan: " + e.getMessage());
            return false;
        }
    }

    // UBAH (UPDATE) berdasarkan kode_barang
    public boolean ubah(Barang b) {
        String sql = "UPDATE barang SET nama_barang = ?, kategori = ?, stok = ? WHERE kode_barang = ?";
        try (Connection conn = Koneksi.getConnection()) {
            if (conn == null) return false;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, b.getNamaBarang());
                ps.setString(2, b.getKategori());
                ps.setInt(3, b.getStok());
                ps.setString(4, b.getKodeBarang());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Gagal ubah: " + e.getMessage());
            return false;
        }
    }

    // HAPUS (DELETE) berdasarkan kode_barang
    public boolean hapus(String kodeBarang) {
        String sql = "DELETE FROM barang WHERE kode_barang = ?";
        try (Connection conn = Koneksi.getConnection()) {
            if (conn == null) return false;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, kodeBarang);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Gagal hapus: " + e.getMessage());
            return false;
        }
    }

    // TAMPIL DATA (SELECT)
    public List<Barang> tampilData() {
        List<Barang> list = new ArrayList<>();
        String sql = "SELECT kode_barang, nama_barang, kategori, stok FROM barang ORDER BY kode_barang";
        try (Connection conn = Koneksi.getConnection()) {
            if (conn == null) return list;
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Barang(
                            rs.getString("kode_barang"),
                            rs.getString("nama_barang"),
                            rs.getString("kategori"),
                            rs.getInt("stok")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal tampil data: " + e.getMessage());
        }
        return list;
    }
}
