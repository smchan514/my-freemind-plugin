package smchan.freemind_my_plugin;

import java.io.File;
import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStore.SecretKeyEntry;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.security.auth.DestroyFailedException;

import smchan.freemind_my_plugin.encrypted_attr.CipherAlgoSelection;
import smchan.freemind_my_plugin.encrypted_attr.EncryptedAttribute;
import smchan.freemind_my_plugin.encrypted_attr.EncryptedAttributeV1;

public class EncryptUtil {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(EncryptUtil.class.getName());

    private static final String DEFAULT_CIPHER_ALGORITHM = "AES_256/GCM/NoPadding";
    private static final int DEFAULT_GCM_TAG_LENGTH = 128;
    private static final int DEFAULT_CIPHER_IV_SIZE = 16;

    private static final String DEFAULT_KEYSTORE_FILE_NAME = ".keystore";

    private static EncryptUtil _instance;

    private final HashMap<String, SecretKey> _mapKeys = new HashMap<>();
    private String _keystorePath;
    private String _defaultKeyAlias;

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

    public void setDefaultKeyAlias(String alias) {
        _defaultKeyAlias = alias;
    }

    public void getDefaultSecretKey() {
        getSecretKey(_defaultKeyAlias);
    }

    public String encrypt(String content) {
        try {
            return encrypt(DEFAULT_CIPHER_ALGORITHM, content, _defaultKeyAlias);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public String decrypt(String content) {
        try {
            return decrypt(DEFAULT_CIPHER_ALGORITHM, content);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
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

            // Check if the key alias exists
            if (!ks.containsAlias(keyAlias)) {
                throw new RuntimeException("key alias '"+keyAlias+"' not found in keystore");
            }

            // Check if the key alias corresponds to the secret key entry
            if (!ks.entryInstanceOf(keyAlias, SecretKeyEntry.class)) {
                throw new RuntimeException("key alias '" + keyAlias + "' not a secret key entry");
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
            throw new RuntimeException("Failed to get secret key '" + keyAlias + "' from keystore", e);
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

    private String encrypt(String algorithm, String cleartext, String secretKeyAlias) throws Exception {
        byte[] ivBytes = generateRandomBytes(DEFAULT_CIPHER_IV_SIZE);
        GCMParameterSpec pspec = new GCMParameterSpec(DEFAULT_GCM_TAG_LENGTH, ivBytes);
        Key secretKey = getSecretKey(secretKeyAlias);

        // Encrypt input clear text
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, pspec);
        byte[] cipherText = cipher.doFinal(cleartext.getBytes());

        EncryptedAttributeV1 ea = new EncryptedAttributeV1();
        ea.setCipherAlgo(CipherAlgoSelection.AES_256_GCM_NoPadding_TAG128);
        ea.setSecretKeyAlias(secretKeyAlias);
        ea.setIV(ivBytes);
        ea.setCipherText(cipherText);

        return ea.encodeBase64();
    }

    private String decrypt(String algorithm, String based64encoded) throws Exception {
        EncryptedAttribute ea = EncryptedAttribute.decodeBase64(based64encoded);
        byte[] iv = ea.getIV();
        byte[] cipherText = ea.getCipherText();
        String secretKeyAlias = ea.getSecretKeyAlias();

        Key secretKey = getSecretKey(secretKeyAlias);
        GCMParameterSpec pspec = new GCMParameterSpec(DEFAULT_GCM_TAG_LENGTH, iv, 0, iv.length);

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, pspec);
        byte[] plainText = cipher.doFinal(cipherText, 0, cipherText.length);
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
