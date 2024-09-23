package smchan.freemind_my_plugin.encrypted_attr;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Serialization version 0 of the encrypted attribute introduced in commit
 * <tt>0f1a5056</tt>
 * 
 * <P>
 * Header size: 6 bytes
 * <OL>
 * <LI>Magic number: 2 bytes (0x10d0)
 * <LI>Version: 1 byte (0)
 * <LI>Reserved: 1 byte (0)
 * <LI><i>=== Version-specific below ===</i>
 * <LI>IV vector size: 2 bytes
 * </OL>
 */
public class EncryptedAttributeV0 extends EncryptedAttribute {
    private static final String DEFAULT_KEY_ALIAS = "privateKeyAlias";

    private int _ivSize;
    private byte[] _iv;
    private byte[] _ciphertext;

    public EncryptedAttributeV0() {
        super(0);
    }

    @Override
    protected void decodeVersionSpecific(DataInputStream dis) throws IOException {
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
        // IV size in bytes
        dos.writeShort(_ivSize);

        // Append the IV
        dos.write(_iv);

        // Append the encrypted text
        dos.write(_ciphertext);
    }

    @Override
    public byte[] getIV() {
        return _iv;
    }

    @Override
    public byte[] getCipherText() {
        return _ciphertext;
    }

    @Override
    public CipherAlgoSelection getCipherAlgo() {
        // Version 0 uses hard-coded algorithm selection
        return CipherAlgoSelection.AES_256_GCM_NoPadding_TAG128;
    }

    @Override
    public String getSecretKeyAlias() {
        // Version 0 uses hard-coded secret key alias
        return DEFAULT_KEY_ALIAS;
    }

}