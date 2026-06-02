package src;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NasabahControllerTest {
    private NasabahController controller;

    @BeforeEach
    public void setUp() {
        controller = new NasabahController();
    }

    @Test
    public void testPencarianNasabahKosong() {
        Nasabah hasil = controller.cariNasabahByNIK("9999999999999999");
        assertNull(hasil);
    }
}
