package smchan.freemind_my_plugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.KeyStore;
import java.security.KeyStore.SecretKeyEntry;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.security.auth.DestroyFailedException;

public class EncryptUtil {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(EncryptUtil.class.getName());

    // Header size in bytes:
    // Magic number: 2 bytes
    // Version: 1 byte
    // Reserved: 1 byte
    // IV vector size: 2 bytes
    private static final int HEADER_SIZE = 6;

    // 2-byte magic number giving "EN" prefix in BASE64 encoding
    private static final short MAGIC_NUMBER = 0x10d0;

    private static final String DEFAULT_CIPHER_ALGORITHM = "AES_256/GCM/NoPadding";
    private static final int DEFAULT_GCM_TAG_LENGTH = 128;
    private static final int DEFAULT_CIPHER_IV_SIZE = 16;

    private static final int CURRENT_VERSION = 0;

    private static final String DEFAULT_KEYSTORE_FILE_NAME = ".keystore";
    private static final String DEFAULT_KEY_ALIAS = "privateKeyAlias";

    private static EncryptUtil _instance;

    private final HashMap<String, SecretKey> _mapKeys = new HashMap<>();
    private String _keystorePath;

    private EncryptUtil() {
        // ...
    }

    public static EncryptUtil getInstance() {
        if (_instance == null) {
            _instance = new EncryptUtil();
        }
        return _instance;
    }

    public void reset() {
        for (Entry<String, SecretKey> entry : _mapKeys.entrySet()) {
            try {
                entry.getValue().destroy();
            } catch (DestroyFailedException e) {
                LOGGER.warning("Failed to destroy secret key: " + entry.getKey());
            }
        }

        _mapKeys.clear();
    }

    public void setKeystorePath(String path) {
        _keystorePath = path;
    }

    public void getSecretKey() {
        getSecretKey(DEFAULT_KEY_ALIAS);
    }

    public String encrypt(String content) {
        SecretKey secretKey = getSecretKey(DEFAULT_KEY_ALIAS);

        try {
            return encrypt(DEFAULT_CIPHER_ALGORITHM, content, secretKey);
        } catch (Exception e) {
            throw new RuntimeException("Failed", e);
        }
    }

    public String decrypt(String content) {
        SecretKey secretKey = getSecretKey(DEFAULT_KEY_ALIAS);

        try {
            return decrypt(DEFAULT_CIPHER_ALGORITHM, content, secretKey);
        } catch (Exception e) {
            throw new RuntimeException("Failed", e);
        }
    }

    private SecretKey getSecretKey(String keyAlias) {
        // Lazy init
        if (!_mapKeys.containsKey(keyAlias)) {
            SecretKey secretKey = loadSecretKey(keyAlias);
            _mapKeys.put(keyAlias, secretKey);
        }

        return _mapKeys.get(keyAlias);
    }

    private SecretKey loadSecretKey(String keyAlias) {
        char[] password = PasswordDialog.getPassword("Enter keystore main password:");

        try {
            // Load keystore file, assumed to be protected with the main password
            KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(password);
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            try (FileInputStream fis = new FileInputStream(getPathKeystore())) {
                ks.load(fis, password);
            }

            // Get the secret key from the keystore using the entry password
            char[] entryPassword = PasswordDialog
                    .getPassword("Enter key entry password\n(ENTER to use keystore password):");
            if (entryPassword.length > 0) {
                clearArray(password);
                protParam = new KeyStore.PasswordProtection(password = entryPassword);
            }

            KeyStore.SecretKeyEntry skEntry = (SecretKeyEntry) ks.getEntry(keyAlias, protParam);
            return skEntry.getSecretKey();
        } catch (UserCancelledException uce) {
            throw uce;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get secret key from keystore", e);
        } finally {
            clearArray(password);
        }
    }

    private File getPathKeystore() {
        if (_keystorePath != null) {
            return new File(_keystorePath);
        }

        File dir = new File(System.getProperty("user.home"), ".freemind");
        return new File(dir, DEFAULT_KEYSTORE_FILE_NAME);
    }

    private static String encrypt(String algorithm, String cleartext, SecretKey secretKey) throws Exception {
        byte[] ivBytes = generateRandomBytes(DEFAULT_CIPHER_IV_SIZE);
        GCMParameterSpec pspec = new GCMParameterSpec(DEFAULT_GCM_TAG_LENGTH, ivBytes);

        // Encrypt input clear text
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, pspec);
        byte[] cipherText = cipher.doFinal(cleartext.getBytes());

        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        try (DataOutputStream dos = new DataOutputStream(baos)) {
            // Write the header
            dos.writeShort(MAGIC_NUMBER);
            // Version 0
            dos.write(0);
            // Reserved byte: always 0
            dos.write(0);
            // IV size in bytes
            dos.writeShort(ivBytes.length);

            // Append the IV
            dos.write(ivBytes);

            // Append the encrypted text
            dos.write(cipherText);
        }

        // BASE64 encode the packet
        Encoder base64encoder = Base64.getEncoder();
        return base64encoder.encodeToString(baos.toByteArray());
    }

    private static String decrypt(String algorithm, String cipherText, SecretKey secretKey) throws Exception {
        byte[] encBytes = Base64.getDecoder().decode(cipherText);

        // Perform decryption only if the magic number is verified
        ByteBuffer bbHeader = ByteBuffer.wrap(encBytes, 0, HEADER_SIZE);
        bbHeader.order(ByteOrder.BIG_ENDIAN);
        if ((0xffff & bbHeader.getShort()) != MAGIC_NUMBER) {
            throw new RuntimeException("Not a recognized encrypted node");
        }

        // Read version byte
        int version = 0xff & bbHeader.get();
        if (version != CURRENT_VERSION) {
            throw new RuntimeException("Unsupported encrypted payload version " + version);
        }

        // Read reserved byte
        int reserved = bbHeader.get();
        if (reserved != 0) {
            throw new RuntimeException("Unexpected header content");
        }

        // Read IV size
        int ivSize = 0xffff & bbHeader.getShort();

        // Read the IV block and construct GCM parameter
        int offset = HEADER_SIZE;
        GCMParameterSpec pspec = new GCMParameterSpec(DEFAULT_GCM_TAG_LENGTH, encBytes, offset, ivSize);

        offset += ivSize;
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, pspec);
        byte[] plainText = cipher.doFinal(encBytes, offset, encBytes.length - offset);
        return new String(plainText);
    }

    private void clearArray(char[] password) {
        Arrays.fill(password, (char) 0);
    }

    private static byte[] generateRandomBytes(int size) {
        byte[] salt = new byte[size];
        new SecureRandom().nextBytes(salt);
        return salt;
    }
}
