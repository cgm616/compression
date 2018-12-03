import java.util.Optional;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.Map.Entry;

public class Huffman {
    private Node top;

    public Huffman(byte[] input) {
        this.top = build(input);
    }

    private Node build(byte[] input) {
        if (input.length < 1) {
            return null; // TODO: make this real error handling
        }

        TreeMap<Byte, Integer> map = new TreeMap<Byte, Integer>();

        for (byte b : input) {
            if (map.containsKey(b)) {
                Integer frequency = map.get(b) + 1;
                map.put(b, frequency);
            } else {
                map.put(b, 1);
            }
        }

        PriorityQueue<Node> queue = new PriorityQueue<Node>();

        while (!map.isEmpty()) {
            Entry<Byte, Integer> e = map.pollFirstEntry();
            byte[] values = new byte[1];
            values[0] = e.getKey();
            Node node = new Node(values, (int) e.getValue());
            queue.add(node);
        }

        while (!queue.isEmpty()) {
            Node last = queue.poll();
            if (queue.isEmpty()) {
                return last;
            } else {
                Node second = queue.poll();

                int len = last.values.length + second.values.length;
                byte[] values = new byte[len];

                for (int i = 0; i < last.values.length; i++) {
                    values[i] = last.values[i];
                }
                for (int i = 0; i < second.values.length; i++) {
                    values[i + last.values.length] = second.values[i];
                }

                Arrays.sort(values);

                int weight = last.weight + second.weight;
                Node parent = new Node(second, last, values, weight);
                queue.add(parent);
            }
        }

        return null; // This should never happen, but make it real error handling if it does
    }

    public static Huffman deserialize(byte[] input) {
        return new Huffman(new byte[1]); // TODO: make this work!
    }

    public byte[] serialize() {
        ArrayList<Boolean> bits = new ArrayList<Boolean>();
        serializeInternal(this.top, bits);
        return bytesFromBits(bits);
    }

    private void serializeInternal(Node n, ArrayList<Boolean> output) {
        if (n.values.length == 1) {
            output.add(false);
            int value = (int) n.values[0];
            for (int i = 0; i < 8; i++) {
                if (value % 2 == 0) {
                    output.add(false);
                } else {
                    output.add(true);
                }
                value = value >> 1;
            }
            return;
        } else {
            output.add(true);
            serializeInternal(n.left, output);
            serializeInternal(n.right, output);
        }
    }

    public byte[] compress(byte[] input) {
        ArrayList<Boolean> output = new ArrayList<Boolean>();

        for (byte b : input) {
            Node parent = this.top;

            if (Arrays.binarySearch(parent.values, b) < 0) {
                System.out.println(b);
                return null; // TODO: error handling
            }

            while (true) {
                if (parent.values.length == 1) {
                    break;
                }

                if (Arrays.binarySearch(parent.left.values, b) >= 0) {
                    output.add(false);
                    parent = parent.left;
                } else if (Arrays.binarySearch(parent.right.values, b) >= 0) {
                    output.add(true);
                    parent = parent.right;
                } else {
                    return null; // TODO: error handling
                    // This is a malformed tree
                }
            }
        }

        return bytesFromBits(output);
    }

    private static byte[] bytesFromBits(ArrayList<Boolean> bits) {
        int len = (bits.size() / 8) + 1;

        byte[] ret = new byte[len];
        int next = 0;

        for (int i = 0; i < bits.size(); i++) {
            if (bits.get(i)) {
                int mask = 1 << (7 - (i % 8));
                next = next | mask;
            }

            if (i % 8 == 7) {
                ret[i / 8] = (byte) next;
                next = 0;
            }
        }

        ret[len - 1] = (byte) next;

        return ret;
    }

    public static byte[] expand(byte[] input) {
        /*
         * for (byte b : input) { for (int i = 0; i < 8; i++) { byte mask = 1 << (7 -
         * i); int msb = (b & mask) >> i;
         * 
         * Node parent = this.top;
         * 
         * if (msb == 0) {
         * 
         * } } }
         */
        return new byte[1];
    }

    class Node implements Comparable<Node> {
        public Node left;
        public Node right;

        public int weight;
        public byte[] values;

        public Node(Node left, Node right, byte[] values, int weight) {
            this.left = left;
            this.right = right;

            this.values = values;
            this.weight = weight;
        }

        public Node(byte[] values, int weight) {
            this(null, null, values, weight);
        }

        public int compareTo(Node other) {
            return ((Integer) this.weight).compareTo((Integer) other.weight);
        }

        public String toString() {
            return "weight: " + this.weight + ", values: " + this.values;
        }
    }
}