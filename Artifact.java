public class Artifact {
    byte[] bytes;

    public Artifact(byte[] bytes) {
        this.bytes = bytes;
    }

    public Header getHeader() {
        return new Header(bytes);
    }

    public byte[] getBody() {
        return new byte[1];
    }
}