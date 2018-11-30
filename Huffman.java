import java.util.Optional;

public class Huffman {
    Node top;

    public Huffman(byte[] input) {
        this.top = build(input);
    }

    private Node build(byte[] input) {
        return new Node(0, (byte) 0); // TODO: make this really build the tree!
    }

    public static Huffman deserialize(byte[] input) {
        return new Huffman(new byte[1]); // TODO: make this work!
    }

    public byte[] serialize() {
        return new byte[1]; // TODO: do this!
    }

    class Node {
        public Optional<Node> parent;

        public Optional<Node> left;
        public Optional<Node> right;

        public int weight;
        public byte value;

        public Node(Optional<Node> parent, Optional<Node> left, Optional<Node> right, int weight, byte value) {
            this.parent = parent;

            this.left = left;
            this.right = right;

            this.weight = weight;
            this.value = value;
        }

        public Node(int weight, byte value) {
            new Node(Optional.empty(), Optional.empty(), Optional.empty(), weight, value);
        }
    }
}