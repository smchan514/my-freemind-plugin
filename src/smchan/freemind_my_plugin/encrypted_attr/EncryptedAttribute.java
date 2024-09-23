package smchan.freemind_my_plugin.encrypted_attr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Base64.Encoder;

/**
 * Abstract base class of the data object representing the content of an
 * encrypted attribute (in a mind map node)
 * 
 * <P>
 * Common header elements size: 4 bytes
 * <OL>
 * <LI>Magic number: 2 bytes (0x10d0)
 * <LI>Version: 1 byte
 * <LI>Reserved: 1 byte
 * </OL>
 */
public abstract class EncryptedAttribute {
    // 2-byte magic number giving "EN" prefix in BASE64 encoding
    private static final short MAGIC_NUMBER = 0x10d0;

    private final int _version;

    /**
     * Factory method to decode a BASE64-encoded string assumed to contain a
     * recognized version of encrypted attribute into an instance of
     * {@link EncryptedAttribute}
     * 
     * @param base64encoded a non-null BASE64 encoded string
     * @return an instance of {@link EncryptedAttribute} created from the argument
     *         BASE64-encoded string
     * @throws IOException on decoding failure
     */
    public static EncryptedAttribute decodeBase64(String base64encoded) throws IOException {
        byte[] bin = Base64.getDecoder().decode(base64encoded);
        ByteArrayInputStream bais = new ByteArrayInputStream(bin);
        DataInputStream dis = new DataInputStream(bais);

        if ((dis.readUnsignedShort()) != MAGIC_NUMBER) {
            throw new RuntimeException("Not a recognized encrypted attribute");
        }

        // Read version byte and reserved byte at once
        int version = dis.readUnsignedShort();

        // Deserialize with the implementation corresponding to the version
        EncryptedAttribute newInstance;
        switch (version) {
        case 0:
            newInstance = new EncryptedAttributeV0();
            break;

        case 0x100:
            newInstance = new EncryptedAttributeV1();
            break;

        default:
            throw new RuntimeException("Unknown encrypted payload version " + String.format("0x%02x", version));
        }

        // Tell the subclass to continue the rest of the decoding
        newInstance.decodeVersionSpecific(dis);
        return newInstance;
    }

    /**
     * Encode this instance of {@link EncryptedAttribute} in binary format into the
     * argument {@link DataOutputStream}
     * 
     * @param dos an instance of {@link DataOutputStream} to encode this object into
     * @throws IOException on encoding failure
     */
    public final void encodeBinary(DataOutputStream dos) throws IOException {
        // Write the common header: 4 bytes
        dos.writeShort(MAGIC_NUMBER);
        dos.write(getVersion());
        dos.write(0);

        // Tell the subclass to continue decode into the DataOutputStream
        encodeVersionSpecific(dos);
    }

    /**
     * Encode this instance of {@link EncryptedAttribute} in BASE64 encoded format
     * 
     * @param dos an instance of {@link DataOutputStream}
     * @throws IOException on encoding failure
     */
    public final String encodeBase64() throws IOException {
        // Encode in binary format first
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        try (DataOutputStream dos = new DataOutputStream(baos)) {
            encodeBinary(dos);
        }

        Encoder base64encoder = Base64.getEncoder();
        return base64encoder.encodeToString(baos.toByteArray());
    }

    //////////////////////////////////////////////////////////////////////////

    protected EncryptedAttribute(int version) {
        _version = version;
    }

    /**
     * Abstract method to be implemented by subclass to decode version-specific
     * content from the argument {@link DataInputStream} which, on invocation of
     * this method, is pointing right after the common header elements in the data
     * stream
     * 
     * @param dis an instance of {@link DataInputStream} to continue decoding from
     * @throws IOException
     */
    protected abstract void decodeVersionSpecific(DataInputStream dis) throws IOException;

    /**
     * Abstract method to be implemented by subclass to encode version-specific
     * content from the argument {@link DataOutputStream} which, on invocation of
     * this method, already received common header elements
     * 
     * @param dos an instance of {@link DataOutputStream} to continue encoding into
     * @throws IOException
     */
    protected abstract void encodeVersionSpecific(DataOutputStream dos) throws IOException;

    public abstract CipherAlgoSelection getCipherAlgo();

    public abstract String getSecretKeyAlias();

    public abstract byte[] getIV();

    public abstract byte[] getCipherText();

    public int getVersion() {
        return _version;
    }
}
