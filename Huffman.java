import java.util.Optional;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.BitSet;

public class Huffman {
    Node top;

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
            Entry e = map.pollFirstEntry();
            ArrayList<Byte> values = new ArrayList<Byte>();
            values.add((Byte) e.getKey());
            Node node = new Node(values, (int) e.getValue());
        }

        while (!queue.isEmpty()) {
            Node last = queue.poll();
            if (queue.isEmpty()) {
                return last;
            } else {
                Node second = queue.poll();
                ArrayList<Byte> values = new ArrayList();
                values.addAll(last.values);
                values.addAll(second.values);
                int weight = last.weight + second.weight;
                Node parent = new Node(Optional.of(second), Optional.of(last), values, weight);
                queue.add(parent);
            }
        }

        return null; // This should never happen, but make it real error handling if it does
    }

    public static Huffman deserialize(byte[] input) {
        return new Huffman(new byte[1]); // TODO: make this work!
    }

    public byte[] serialize() {
        return new byte[1]; // TODO: do this!
    }

    public ArrayList<Byte> compress(byte[] input) {
        ArrayList<Boolean> output = new ArrayList<Boolean>();

        for (byte b : input) {
            Node parent = this.top;

            if (!parent.values.contains(b)) {
                return null; // TODO: error handling
            }

            while (true) {
                if (parent.values.size() == 1) {
                    break;
                }

                if (parent.left.values.contains(b)) {
                    output.add(false);
                    parent = parent.left;
                } else if (parent.right.values.contains(b)) {
                    output.add(true);
                    parent = parent.right;
                } else {
                    return null; // TODO: error handling
                    // This is a malformed tree
                }
            }
        }

        ArrayList<Byte> ret = new ArrayList<Byte>();
        byte next = 0;

        for (int i = 0; i < output.size(); i++) {
            if (output.get(i)) {
                byte mask = 1 << (7 - (i % 8));
                next = next | mask;
            }

            if (i % 8 == 7) {
                ret.add(next);
                next = 0;
            }
        }

        ret.add(next);

        return ret;
    }

    public byte[] expand(byte[] bytes) {
        return new byte[1];
    }

    class Node {
        public Node left;
        public Node right;

        public int weight;

        public ArrayList<Byte> values;

        public Node(Node left, Node right, ArrayList<Byte> value, int weight) {
            this.left = left;
            this.right = right;

            this.weight = weight;
            this.values = value;
        }

        public Node(ArrayList<Byte> values, int weight) {
            new Node(null, null, values, weight);
        }
    }
}