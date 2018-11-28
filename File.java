public class File {
    byte[] bytes;

    public File(byte[] bytes) {
        this.bytes = bytes;
    }

    public Header getHeader() {
        return new Header();
    }

    public byte[] getBody() {
        return new byte[1];
    }
}