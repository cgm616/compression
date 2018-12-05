import java.util.Arrays;

public class Tester {
    public static void main(String[] args) {
        testArtifact();
        testBitArray();
    }

    public static void testArtifact() {
        byte[] input = { 0x31, 0x41, 0x59, 0x26, 0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x00, 0x00,
                0x00, 0x01, 0x00 };
        byte[] body = { 0x00, 0x00, 0x00, 0x01, 0x00 };
        byte[] tree = { 0x00, 0x00, 0x00 };

        Artifact art = Artifact.fromBytes(input);

        assert art.markerIndex == 7;
        assert Arrays.equals(art.getBody(), body);
        assert Arrays.equals(art.getSerializedTree(), tree);
    }

    public static void testBitArray() {
        BitArray first = new BitArray();
        first.pushInt(0x77777777);
        for (int i = 0; i < 32; i++) {
            if (i % 4 == 0) {
                assert !first.get(i);
            } else {
                assert first.get(i);
            }
        }

        BitArray second = new BitArray();
        second.push(true); // 0
        second.push(false); // 1
        second.push(true); // 2
        second.push(true); // 3
        second.push(true); // 4
        second.push(false); // 5
        second.push(true); // 6
        second.push(true); // 7
        second.push(false); // 8
        second.push(false); // 9: { 0b10111011, 0b00000000 }

        assert second.get(0);
        assert !second.get(1);
        assert second.get(2);
        assert second.get(3);
        assert second.get(4);
        assert !second.get(5);
        assert second.get(6);
        assert second.get(7);
        assert !second.get(8);
        assert !second.get(9);

        second.push(false); // 10
        second.push(true); // 11 : { 0b10111011, 0b00010000 }

        assert second.get(0);
        assert !second.get(1);
        assert second.get(2);
        assert second.get(3);
        assert second.get(4);
        assert !second.get(5);
        assert second.get(6);
        assert second.get(7);
        assert !second.get(8);
        assert !second.get(9);
        assert !second.get(10);
        assert second.get(11);

    }
}
