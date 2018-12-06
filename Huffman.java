import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.TreeMap;

public class Huffman {
    private Node top;

    public Huffman(byte[] input) {
        this.top = build(input);
    }

    private Huffman(Node top) {
        this.top = top;
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
        BitArray bits = BitArray.fromBytes(input);
        Node head = new Node(null, null);
        deserializeInternal(head, 0, bits);
        return new Huffman(head);
    }

    private static int deserializeInternal(Node head, int bitIndex, BitArray bits) {
        int newIndex = bitIndex;

        if (bitIndex > bits.length()) {
            return 0; // This should never happen.
        }

        if (bits.get(bitIndex)) {
            // The current bit is 1.
            newIndex += 1;

            Node left = new Node(null, null);
            head.left = left;
            newIndex = deserializeInternal(left, newIndex, bits);

            Node right = new Node(null, null);
            head.right = right;
            newIndex = deserializeInternal(right, newIndex, bits);

            int length = left.values.length + right.values.length;
            byte[] values = Arrays.copyOf(left.values, length);
            for (int i = 0; i < right.values.length; i++) {
                values[left.values.length + i] = right.values[i];
            }

            head.values = values;

            return newIndex;
        } else {
            // Current bit is 0.
            newIndex += 1;

            if (newIndex + 7 > bits.length()) {
                System.out.println("Returning early: " + newIndex);
                return newIndex;
                // This should never happen due to the recursion method.
            }

            head.values = new byte[1];
            head.values[0] = bits.readByte(newIndex);

            newIndex += 8;

            return newIndex;
        }
    }

    public byte[] serialize() {
        BitArray bits = new BitArray();
        serializeInternal(this.top, bits);
        return bits.toArray();
    }

    private void serializeInternal(Node n, BitArray output) {
        if (n.values.length == 1) {
            output.push(false);
            output.pushByte(n.values[0]);
            return;
        } else {
            output.push(true);
            serializeInternal(n.left, output);
            serializeInternal(n.right, output);
        }
    }

    public byte[] compress(byte[] input) {
        BitArray output = new BitArray();
        System.out.println("Compressing length: " + input.length);
        output.pushInt(input.length);

        for (byte b : input) {
            Node parent = this.top;

            if (Arrays.binarySearch(parent.values, b) < 0) {
                return null; // TODO: error handling
            }

            while (true) {
                if (parent.values.length == 1) {
                    break;
                }

                if (Arrays.binarySearch(parent.left.values, b) >= 0) {
                    output.push(false);
                    parent = parent.left;
                } else if (Arrays.binarySearch(parent.right.values, b) >= 0) {
                    output.push(true);
                    parent = parent.right;
                } else {
                    return null; // TODO: error handling
                    // This is a malformed tree
                }
            }
        }

        return output.toArray();
    }

    public byte[] expand(byte[] input) {
        BitArray bits = BitArray.fromBytes(input);
        ArrayList<Byte> output = new ArrayList<Byte>(2 * input.length);
        int length = bits.getInt(0);
        System.out.println("Looking for " + length + " bytes");

        int i = 32;
        int bytesOutput = 0;
        while (bytesOutput < length) {
            Node current = this.top;
            while (current.values.length > 1) {
                if (bits.get(i)) {
                    current = current.right;
                    i += 1;
                } else {
                    current = current.left;
                    i += 1;
                }
            }

            output.add(current.values[0]);
            bytesOutput += 1;
        }

        byte[] ret = new byte[output.size()];
        int index = 0;

        for (byte b : output) {
            ret[index] = b;
            index += 1;
        }

        return ret;
    }

    public void writeToGraph(Path path) {
        try (Writer writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(path.toString()), StandardCharsets.UTF_8))) {
            writer.write("digraph HT {\n");

            writeToGraphInternal(writer, this.top);
            writer.write("}\n");
            writer.flush();
        } catch (IOException ex) {
            // Handle me
        }
    }

    private void writeToGraphInternal(Writer writer, Node head) throws IOException {
        // abc [fillcolor = red]
        if (head.values.length == 1) {
            String label = "0x" + Integer.toHexString((int) head.values[0]);
            if (head.weight != null) {
                label += ": " + head.weight;
            }

            writer.write("\"" + head.toString() + "\" [ label = \"" + label + "\" ];\n");
        } else {
            // This is an internal node
            String label = "";
            if (head.weight != null) {
                label += head.weight;
            }

            writer.write("\"" + head.toString() + "\" [ label = \"" + label + "\" ];\n");

            writer.write("\"" + head.toString() + "\" -> \"" + head.left.toString() + "\" [ label = 0 ];\n");
            writeToGraphInternal(writer, head.left);

            writer.write("\"" + head.toString() + "\" -> \"" + head.right.toString() + "\" [ label = 1 ];\n");
            writeToGraphInternal(writer, head.right);
        }
    }

    static class Node implements Comparable<Node> {
        public Node left;
        public Node right;

        public Integer weight;
        public byte[] values;

        public Node(Node left, Node right, byte[] values, Integer weight) {
            this.left = left;
            this.right = right;

            this.values = values;
            this.weight = weight;
        }

        public Node(byte[] values, Integer weight) {
            this(null, null, values, weight);
        }

        public int compareTo(Node other) {
            return this.weight.compareTo(other.weight);
        }

        public String toString() {
            String full = super.toString();
            return full.substring(full.indexOf("@") + 1, full.length());
        }
    }
}
