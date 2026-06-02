package src;
public class Nasabah {
    private int idNasabah;
    private String username;
    private String nik;
    private String namaLengkap;
    private String alamat;
    private String noHp;
    private String statusKyc;

    // Constructor
    public Nasabah(int idNasabah, String username, String nik, String namaLengkap, String alamat, String noHp, String statusKyc) {
        this.idNasabah = idNasabah;
        this.username = username;
        this.nik = nik;
        this.namaLengkap = namaLengkap;
        this.alamat = alamat;
        this.noHp = noHp;
        this.statusKyc = statusKyc;
    }

    // Encapsulation: Getter dan Setter
    public int getIdNasabah() { return idNasabah; }
    public String getUsername() { return username; }
    public String getNik() { return nik; }
    public String getNamaLengkap() { return namaLengkap; }
    public String getAlamat() { return alamat; }
    public String getNoHp() { return noHp; }
    public String getStatusKyc() { return statusKyc; }

    public void setStatusKyc(String statusKyc) { this.statusKyc = statusKyc; }
}
