package smchan.freemind_my_plugin.encrypted_attr;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 */
public class EncryptedAttributeV1 extends EncryptedAttribute {

    private static final String DEFAULT_STRING_ENCODING = "UTF-8";

    private CipherAlgoSelection _cipherAlgo = CipherAlgoSelection.AES_256_GCM_NoPadding_TAG128;
    private String _secretKeyAlias;
    private int _ivSize;
    private byte[] _iv;
    private byte[] _ciphertext;

    public EncryptedAttributeV1() {
        super(1);
    }

    @Override
    protected void decodeVersionSpecific(DataInputStream dis) throws IOException {
        // Read cipher algo selection index
        int cipherAlgoIndex = dis.readUnsignedByte();
        // Catch incorrect index with array out of bound exception
        _cipherAlgo = CipherAlgoSelection.values()[cipherAlgoIndex];

        // Read secret key alias
        int secretKeyAliasSize = dis.readUnsignedByte();
        byte[] secretKeyAliasBytes = new byte[secretKeyAliasSize];
        dis.read(secretKeyAliasBytes, 0, secretKeyAliasSize);
        _secretKeyAlias = new String(secretKeyAliasBytes, DEFAULT_STRING_ENCODING);

        // Read IV size
        _ivSize = dis.readUnsignedShort();

        // Read the IV block
        _iv = new byte[_ivSize];
        dis.read(_iv, 0, _ivSize);

        // Read the encrypted payload
        int encryptedSize = dis.available();
        _ciphertext = new byte[encryptedSize];
        dis.read(_ciphertext, 0, encryptedSize);
    }

    @Override
    protected void encodeVersionSpecific(DataOutputStream dos) throws IOException {

        // Cipher algo selection: 1 byte
        dos.write(_cipherAlgo.ordinal());

        // Secret key alias size: 1 byte
        dos.write(_secretKeyAlias.length());

        // Append the secret key alias in UTF-8 encoding
        dos.write(_secretKeyAlias.getBytes(DEFAULT_STRING_ENCODING));

        // IV size in bytes
        dos.writeShort(_ivSize);

        // Append the IV
        dos.write(_iv);

        // Append the encrypted text
        dos.write(_ciphertext);
    }

    public void setSecretKeyAlias(String alias) {
        assert (alias != null) : "alias cannot be null";
        assert (alias.length() >= 1 && alias.length() <= 255) : "alias length must be between 1 and 255";
        _secretKeyAlias = alias;
    }

    public void setCipherAlgo(CipherAlgoSelection cipherAlgo) {
        _cipherAlgo = cipherAlgo;
    }

    public void setIV(byte[] iv) {
        _ivSize = iv.length;
        _iv = Arrays.copyOf(iv, _ivSize);
    }

    public void setCipherText(byte[] ciphertext) {
        _ciphertext = Arrays.copyOf(ciphertext, ciphertext.length);
    }

    @Override
    public CipherAlgoSelection getCipherAlgo() {
        return _cipherAlgo;
    }

    @Override
    public String getSecretKeyAlias() {
        return _secretKeyAlias;
    }

    @Override
    public byte[] getIV() {
        return _iv;
    }

    @Override
    public byte[] getCipherText() {
        return _ciphertext;
    }

    public void copyFrom(EncryptedAttribute other) {
        setSecretKeyAlias(other.getSecretKeyAlias());
        setCipherAlgo(other.getCipherAlgo());
        setIV(other.getIV());
        setCipherText(other.getCipherText());
    }

}