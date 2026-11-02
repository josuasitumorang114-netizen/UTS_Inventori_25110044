package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Koneksi {
    private static final String URL  = "jdbc:mysql://localhost:3306/db_inventori";
    private static final String USER = "root";
    private static final String PASS = "";   // sesuaikan dengan password MySQL Anda

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                    "Driver MySQL (Connector/J) belum ditambahkan ke Libraries:\n" + e.getMessage(),
                    "Kesalahan Driver", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Koneksi ke database gagal:\n" + e.getMessage(),
                    "Kesalahan Koneksi", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}
