import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.TreeMap;

/**
 * A representation of a Huffman tree, holding possible values and weights. It
 * can be serialized and deserialized in a minimal form from a byte stream, and
 * it can compress and expand byte streams once constructed.
 */
public class Huffman {
    private Node top;

    /**
     * This method uses a byte array to construct a Huffman tree according to the
     * bytes present.
     * 
     * @param input The byte array input containing every character the tree must
     *              know
     * @return A constructed tree that can be used for compression, expansion,
     *         serialization, and deserialization
     * @exception Exception Thrown when the Huffman tree errors on building, and
     *                      contains an explanation message
     */
    public Huffman(byte[] input) throws Exception {
        // Run the build function and set the Node returned to the top of the tree
        this.top = build(input);
    }

    /**
     * Given a node, this constructor makes a new instance of Huffman with that node
     * as its head.
     * 
     * @param top The node to become the head of the tree
     * @return A tree with the node given set as the head
     */
    private Huffman(Node top) {
        this.top = top;
    }

    /**
     * This method uses a byte array to construct a tree, first finding the
     * frequencies of each character in the input and then using those to build a
     * balanced tree out of Nodes
     * 
     * @param input The byte array input containing every character the tree must
     *              know
     * @return The top node of the tree, with children node representing a fully
     *         built tree
     * @exception Exception Thrown when the input has a length of zero or when the
     *                      tree cannot be built due to a logic error, with a
     *                      message differentiating the two
     */
    private Node build(byte[] input) throws Exception {
        // First, we immediately throw an exception if the input is less than one byte
        if (input.length < 1) {
            throw new Exception("The input has no bytes");
        }

        // Next, we map byte keys -> frequencies in the file
        TreeMap<Byte, Integer> map = new TreeMap<Byte, Integer>();

        for (byte b : input) {
            // Iterate over the input and check if the map already contains the current byte
            // or not
            if (map.containsKey(b)) {
                // If it does, increment the frequency stored in the map
                Integer frequency = map.get(b) + 1;
                map.put(b, frequency);
            } else {
                // If not, put the byte -> frequency pair into the map with an initial frequency
                // of 1
                map.put(b, 1);
            }
        }

        // Now, we construct a priority queue to order the Nodes while constructing a
        // balanced tree. This allows us to take the lowest frequency values and merge
        // them
        PriorityQueue<Node> queue = new PriorityQueue<Node>();

        while (!map.isEmpty()) {
            // While there are still byte -> frequency pairs in the map, remove one and
            // extract its key and value
            Entry<Byte, Integer> e = map.pollFirstEntry();
            // Create a new byte array of length one and set its only index to the key of
            // the mapping. This is the value that the newly constructed Node will represent
            byte[] values = new byte[1];
            values[0] = e.getKey();
            // Allocate a new Node using the byte array from the last step and the frequency
            // as its weight
            Node node = new Node(values, (int) e.getValue());
            // Add the node to the priority queue
            queue.add(node);
        }

        while (!queue.isEmpty()) {
            // While there is still at least one Node in the queue, take it out
            Node last = queue.poll();
            // Check if there's another Node in the queue
            if (queue.isEmpty()) {
                // If there isn't, the one we already took out is the top Node of the tree and
                // so we return it
                return last;
            } else {
                // If there's another Node in the queue, remove it
                Node second = queue.poll();

                // Construct a new byte array big enough to hold all of the values of its two
                // children so we can use it to search through the tree while compressing
                int len = last.values.length + second.values.length;
                byte[] values = new byte[len];

                // Add the values from the first Node
                for (int i = 0; i < last.values.length; i++) {
                    values[i] = last.values[i];
                }
                // Add the values from the second Node
                for (int i = 0; i < second.values.length; i++) {
                    values[i + last.values.length] = second.values[i];
                }

                // Sort the array of contained values to aid in searching
                Arrays.sort(values);

                // Compute the weight of the new Node as a sum of its children
                int weight = last.weight + second.weight;
                // Create a new Node with the previous two as its children and the new values
                // and weight
                Node parent = new Node(second, last, values, weight);
                // Add this new Node to the queue and repeat
                queue.add(parent);
            }
        }

        // If this point of the function is reached, then for some reason a Node wasn't
        // returned while emptying the priority queue. This should never happen
        throw new Exception("Reached the end of Huffman.build without returning a node");
    }

    /**
     * This method deserializes a tree that can be used for expansion from a byte
     * sequence
     * 
     * @param input The byte array input containing the serialized tree
     * @return A new tree built from the byte sequence
     * @exception Exception Thrown when the deserialization fails
     */
    public static Huffman deserialize(byte[] input) throws Exception {
        // Construct a BitArray from the bytestream to more easily do bit-fiddling
        BitArray bits = BitArray.fromBytes(input);
        // Create a Node to serve as the top of the tree
        Node head = new Node(null, null);
        // Call the internally-recursive method that actually deserializes the tree on
        // the Node just created and the bitstream, with a initial index of 0. The
        // method will build the tree beneath the Node passed in
        deserializeInternal(head, 0, bits);
        // Create a new Huffman tree with the top node
        return new Huffman(head);
    }

    /**
     * This internally recursive method consumes bits from a BitArray and builds a
     * tree underneath a Node given
     * 
     * @param head     The Node to construct the tree under
     * @param bitIndex The index to the next valid bit of the BitArray
     * @param bits     The bitstream containing the tree information
     * @return An integer representing the next valid bit after the function runs
     * @exception Exception Thrown when the deserialization fails
     */
    private static int deserializeInternal(Node head, int bitIndex, BitArray bits) throws Exception {
        // Create a new variable to modify over the course of the function representing
        // the current, valid index
        int newIndex = bitIndex;

        // If the supposedly valid index into the BitArray is past the end of the array,
        // throw an exception. This should never happen
        if (bitIndex > bits.length()) {
            throw new Exception("Tried to deserialize past the end of the available data");
        }

        // Get the current bit from the BitArray
        if (bits.get(bitIndex)) { // The bit is a 1
            // In the tree serialization, a 1 represents an internal Node

            // Increment the bit index, consuming the bit
            newIndex += 1;

            // Create a new Node representing the left child of the current Node. Set the
            // current Node's left child, and then recurse and call this method on that
            // Node. That method will return the next valid bit index, so set the valid
            // index for this function to that return value
            Node left = new Node(null, null);
            head.left = left;
            newIndex = deserializeInternal(left, newIndex, bits);

            // Do the same for the right child
            Node right = new Node(null, null);
            head.right = right;
            newIndex = deserializeInternal(right, newIndex, bits);

            // Propagate the values from the children into this Node
            int length = left.values.length + right.values.length;
            byte[] values = Arrays.copyOf(left.values, length);
            for (int i = 0; i < right.values.length; i++) {
                values[left.values.length + i] = right.values[i];
            }
            head.values = values;

            // Return the next valid bit index
            return newIndex;
        } else { // The bit is a 0
            // In the tree serialization, a 0 represents a leaf Node, with the next 8 bits
            // being the byte value of that Node

            // Increment the bit index, consuming the bit
            newIndex += 1;

            // Make sure we don't try to access past the end of the available data. If we
            // try to, throw an exception
            if (newIndex + 7 > bits.length()) {
                throw new Exception("Tried to deserialize past the end of the available data");
            }

            // Create a new byte array to store the leaf Node value and then read a byte
            // from the bitstream, storing it in the array
            head.values = new byte[1];
            head.values[0] = bits.readByte(newIndex);

            // Increment the bit index to consume 8 bits
            newIndex += 8;

            // Return the next valid bit index
            return newIndex;
        }
    }

    /**
     * This internally recursive method serializes the current tree into a byte
     * array
     * 
     * @return A byte array containing the serialized tree, complete with enough
     *         information to expand sequences compressed by the tree
     */
    public byte[] serialize() {
        // Create a new BitArray to hold the serialized bits
        BitArray bits = new BitArray();
        // Start the recursion with the top of the tree and the bitstream
        serializeInternal(this.top, bits);
        // Return the byte array from the bitstream
        return bits.toArray();
    }

    /**
     * This internally recursive method serializes the current tree into a bitstream
     * 
     * @param n      The node to serialize (the method will also serialize all of
     *               its child nodes)
     * @param output The bitstream to output serialized bits into
     */
    private void serializeInternal(Node n, BitArray output) {
        // Check if the Node is a leaf node (the value array has a length of 1) or an
        // internal Node (the value array has more elements)
        if (n.values.length == 1) {
            // The Node is leaf, so push a 0 and then value of the leaf node to the
            // bitstream
            output.push(false);
            output.pushByte(n.values[0]);
            return;
        } else {
            // The Node is internal, so push a 1 to the bitstream and then recursively call
            // this method to serialize each child, left first and then right
            output.push(true);
            serializeInternal(n.left, output);
            serializeInternal(n.right, output);
        }
    }

    /**
     * This method compresses a byte array into another using a built Huffman tree
     * 
     * @param input The byte array input containing the data to be compressed
     * @return A byte array of compressed data
     * @exception Exception Thrown when a byte in the input data isn't found in the
     *                      tree, or when the tree is malconstructed
     */
    public byte[] compress(byte[] input) throws Exception {
        // Create a new bitstream to push bits too
        BitArray output = new BitArray();

        // Push a 4 byte integer to the bitstream, giving an expansion algorithm the
        // length of bytes it should find in the compressed data
        output.pushInt(input.length);

        // Iterate over each byte in the input
        for (byte b : input) {
            // Create a Node variable to allow the algorithm to walk the tree
            Node parent = this.top;

            while (true) {
                // Check if the current Node is a leaf node. If it is, we are done compressing
                // this one, so we move to the next byte
                if (parent.values.length == 1) {
                    break;
                }

                // Check which child to follow for the current byte
                if (Arrays.binarySearch(parent.left.values, b) >= 0) {
                    // If the left child contains the byte, output a 0 and set the next node to the
                    // left child
                    output.push(false);
                    parent = parent.left;
                } else if (Arrays.binarySearch(parent.right.values, b) >= 0) {
                    // If the right child contains the byte, output a 0 and set the next node to the
                    // right child
                    output.push(true);
                    parent = parent.right;
                } else {
                    // If neither child contains the byte, throw an exception because the data
                    // cannot be serialized
                    throw new Exception("Byte not found in tree");
                }
            }
        }

        // Convert the bitstream to a byte array and return
        return output.toArray();
    }

    /**
     * This method expands a compressed byte array into another using a built
     * Huffman tree
     * 
     * @param input The byte array input containing the data to be expanded
     * @return A byte array of the original, uncompressed data
     */
    public byte[] expand(byte[] input) {
        // Construct a bitstream from the byte input
        BitArray bits = BitArray.fromBytes(input);
        // Create an output ArrayList to store expanded bytes with an initial capacity
        // of roughly twice the input length, corresponding to the rough compression
        // ratio of the algorithm. This should prevent unneeded allocations
        ArrayList<Byte> output = new ArrayList<Byte>(2 * input.length);
        // Read the first 32 bits of the input bitstream, which represent the length of
        // the original data
        int length = bits.getInt(0);

        // Start an index into the bitstream at the 33rd bit, skipping the first 32
        int i = 32;
        // Start a counter of how many bits are output
        int bytesOutput = 0;

        // Continue decompressing while the number of bytes output is less than the
        // supposed compressed bytes in the expanded data
        while (bytesOutput < length) {
            // Make sure we don't try to index past the end of the file. If we do, don't
            // throw an exception and simply output what we've got so far
            if (i >= bits.bitLength) {
                break;
            }

            // Create a Node variable to walk the tree
            Node current = this.top;

            // Continue iterating over the tree until at a leaf Node, represented by a
            // values array of length 1
            while (current.values.length > 1) {
                // Check the next valid bit
                if (bits.get(i)) { // The bit is a 1
                    // Walk down the right path of the tree
                    current = current.right;
                } else { // The bit is a 0
                    // Walk down the left of the tree
                    current = current.left;
                }

                // Increment the bitindex to consume a bit of input
                i += 1;
            }

            // Now at a leaf node, output the value of the Node
            output.add(current.values[0]);
            // Increment the bytes output
            bytesOutput += 1;
        }

        // Construct a new byte array output and copy (slowly :( ) every byte of the
        // ArrayList from before into it
        byte[] ret = new byte[output.size()];
        int index = 0;

        for (byte b : output) {
            ret[index] = b;
            index += 1;
        }

        // Return the constructed output array of expanded data
        return ret;
    }

    /**
     * This method creates a graphviz representation of the Huffman tree for
     * education purposes
     * 
     * @param writer The Writer to write the output graph into
     * @exception IOException Thrown when the Writer throws an IOException. This
     *                        function simply bubbles the exception up
     */
    public void writeToGraph(Writer writer) throws IOException {
        // Emit the graph title and form
        writer.write("digraph HT {\n");
        // Call the recursive method, starting it at the top of the tree
        writeToGraphInternal(writer, this.top);
        // Emit the end of the graph
        writer.write("}\n");
        // Flush the writer ensure all data is written
        writer.flush();
    }

    /**
     * This method creates a graphviz representation of the Huffman tree for
     * education purposes
     * 
     * @param writer The Writer to write the output graph into
     * @param head   The Node to emit to the graph
     * @exception IOException Thrown when the Writer throws an IOException. This
     *                        function simply bubbles the exception up
     */
    private void writeToGraphInternal(Writer writer, Node head) throws IOException {
        // Check if the current node is a leaf Node or an internal Node
        if (head.values.length == 1) {
            // The current Node is a leaf Node

            // Construct a label for the node in the following format:
            // 0x[value]<: weight>
            String label = "0x" + String.format("%02X", head.values[0]);
            if (head.weight != null) {
                // The weight might not be present (if, for example, the tree was deserialized
                // from a byte representation), so we allow labels that do not include the
                // weight
                label += ": " + head.weight;
            }

            // Emit a marker that maps a unique identifier (Node.toString()) to the Node's
            // label
            writer.write("\"" + head.toString() + "\" [ label = \"" + label + "\" ];\n");
        } else {
            // The current Node is an internal Node

            // Construct a label for the Node, but only add anything to it if the weight is
            // present in the tree. No other information is super useful or easy to
            // visualize for internal Nodes
            String label = "";
            if (head.weight != null) {
                label += head.weight;
            }

            // Emit a marker that maps a unique identifier (Node.toString()) to the Node's
            // label
            writer.write("\"" + head.toString() + "\" [ label = \"" + label + "\" ];\n");

            // Emit a connection from the parent Node to its left child
            writer.write("\"" + head.toString() + "\" -> \"" + head.left.toString() + "\" [ label = 0 ];\n");
            // Recursively call the function on the left child
            writeToGraphInternal(writer, head.left);

            // Emit a connection from the parent Node to its right child
            writer.write("\"" + head.toString() + "\" -> \"" + head.right.toString() + "\" [ label = 1 ];\n");
            // Recursively call the function on the right child
            writeToGraphInternal(writer, head.right);
        }
    }

    /**
     * A node of the Huffman tree, containing two children, a weight, and a list of
     * values
     */
    static class Node implements Comparable<Node> {
        // The left child of the Node
        public Node left;
        // The right child of the Node
        public Node right;

        // The weight of the Node
        public Integer weight;
        // The values that this Node and its children (if any) contain
        public byte[] values;

        /**
         * This method constructs a new Node
         * 
         * @param left   The left child of this Node
         * @param right  The right child of the Node
         * @param values The values this Node contains
         * @param weight The weight of this Node in the tree
         */
        public Node(Node left, Node right, byte[] values, Integer weight) {
            this.left = left;
            this.right = right;

            this.values = values;
            this.weight = weight;
        }

        /**
         * This method constructs a new Node without set children
         * 
         * @param values The values this Node contains
         * @param weight The weight of this Node in the tree
         */
        public Node(byte[] values, Integer weight) {
            this(null, null, values, weight);
        }

        /**
         * This method compares a Node to another, sorting by weight
         * 
         * @param other The other Node to compare to
         * @return The ordering of this Node versus the other
         */
        public int compareTo(Node other) {
            // Call the compareTo() method on the Integer weight
            return this.weight.compareTo(other.weight);
        }

        /**
         * This method creates a unique string representation of a Node
         * 
         * @return A string representation of the Node
         */
        public String toString() {
            // Call the builtin toString() method on the superclass (Object), which has a
            // form similar to Huffman$Node@xxxxxx, where the x's represent an alphanumeric
            // unique ID
            String full = super.toString();
            // Return only the portion of that string after the @ sign, erasing unneeded
            // data
            return full.substring(full.indexOf("@") + 1, full.length());
        }
    }
}
