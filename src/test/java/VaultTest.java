import junit.framework.TestCase;
import org.junit.Before;

import java.util.HashMap;

public class VaultTest extends TestCase {

    private String token = System.getenv("VAULT_TOKEN");
    private Vault vault;
    @Before
    public void setUp() {
        this.vault = new Vault("http://127.0.0.1:8200", token);
    }
    public void testWrite() throws Exception {

        HashMap<String, String> data = new HashMap<>();
        data.put("value", "hello");
        vault.write("secret/hello", data);
    }

    public void testRead() throws Exception {
        VaultResponse result = vault.read("secret/hello");
        assertEquals(result.getData().get("value"), "hello");
    }

    public void testDelete() throws Exception {
        vault.delete("secret/hello");
        try {
            vault.read("secret/hello");
            fail("Expected VaultException");
        } catch (VaultException e) {
            assertEquals(e.getStatusCode(), 404);

        }
    }

    public void testReadWithInvalidToken() throws Exception {
        Vault vault = new Vault("http://127.0.0.1:8200", "invalid");
        try {
            vault.read("secret/hello");
            fail("Expected VaultException");
        } catch (VaultException e) {
            assertEquals(e.getStatusCode(), 403);
        }
    }

    public void testGetStatus() throws Exception {
        Vault.Status status = vault.getStatus();
        assertEquals(status.getKeyShares(), 1);
        assertEquals(status.getKeyThreshold(), 1);
        assertEquals(status.getProgress(), 0);
        assertEquals(status.isSealed(), false);
    }

}