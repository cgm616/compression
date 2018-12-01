import java.util.Optional;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.Map.Entry;

public class Huffman {
    Node top;

    public Huffman(byte[] input) {
        this.top = build(input);
    }

    private Node build(byte[] input) {
        if (input.length < 1) {
            return null; // TODO: make this real error handling
        }

        TreeMap map = new TreeMap<Byte, Integer>();

        for (byte b : input) {
            if (map.containsKey(b)) {
                Integer frequency = (Integer) map.get(b) + 1;
                map.put(b, frequency);
            } else {
                map.put(b, 1);
            }
        }

        PriorityQueue queue = new PriorityQueue<Node>();

        while (!map.isEmpty()) {
            Entry e = map.pollFirstEntry();
            ArrayList values = new ArrayList<Byte>();
            values.add((Byte) e.getKey());
            Node node = new Node(values, (int) e.getValue());
        }

        while (!queue.isEmpty()) {
            Node last = queue.poll();
            if (queue.isEmpty()) {
                return last;
            } else {
                Node second = queue.poll();
                ArrayList values = new ArrayList();
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



    class Node {
        public Optional<Node> left;
        public Optional<Node> right;

        public int weight;

        public ArrayList<Byte> values;

        public Node(Optional<Node> left, Optional<Node> right, ArrayList<Byte> value, int weight) {
            this.left = left;
            this.right = right;

            this.weight = weight;
            this.values = value;
        }

        public Node(ArrayList<Byte> value, int weight) {
            new Node(Optional.empty(), Optional.empty(), Optional.empty(), weight, value);
        }
    }
}