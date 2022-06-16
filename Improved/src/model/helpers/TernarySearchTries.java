package model.helpers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TernarySearchTries<Value> implements Serializable {
    private Node root; // root of trie

    private class Node implements Serializable
    {
        char c; // character
        Node left, mid, right; // left, middle, and right subtries
        Value val; // value associated with string
    }
    private class NodeWithPrekey {
        Node node;
        String Prekey;
        public NodeWithPrekey(Node node, String prekey) {
            this.node = node;
            Prekey = prekey;
        }

    }
    public List<Value> getPrefixes(String prefix) {
        if(prefix == null) return null;
        List<Value> list = new ArrayList<>();
        getPrefixes(root, prefix, 0, list);
        return list;
    }
    private void getPrefixes(Node node, String prefix, int d, List<Value> list) {
        if(node == null) return;
        if(d >= prefix.length()){
            getPrefixes(node.left, prefix, d, list);
            if(node.val != null){ list.add(node.val); }
            getPrefixes(node.mid, prefix, d++, list);
            getPrefixes(node.right, prefix, d, list);
            return;
        }
        char c = prefix.charAt(d);
        if (c < node.c) getPrefixes(node.left, prefix, d, list);
        else if (c > node.c) getPrefixes(node.right, prefix, d, list);
        else getPrefixes(node.mid, prefix, d+1, list);
    }


    public Value get(String key){
        Node x = get(root, key.toLowerCase(), 0);
        if (x == null) return null;
        return x.val;
    }
    private Node get(Node x, String key, int d) {
        if (x == null) return null;
        char c = key.charAt(d);
        if (c < x.c) return get(x.left, key, d);
        else if (c > x.c) return get(x.right, key, d);
        else if (d < key.length() - 1)
            return get(x.mid, key, d+1);
        else return x;
    }


    public List<String> getKeySet(){
        List<String> list = new ArrayList<>();
        getKeySet(root, "", list);
        return list;
    }
    private void getKeySet(Node node, String preKey, List<String> list) {
        if(node == null) return;
        getKeySet(node.left, preKey, list);
        getKeySet(node.right, preKey, list);
        String curr = preKey + node.c;
        if(node.val != null) list.add(curr);
        getKeySet(node.mid, curr, list);
    }

    public List<String> getKeySetWithprefix(String prefix) {
        List<String> list = new ArrayList<>();
        NodeWithPrekey best = new NodeWithPrekey(root, "");
        getBestCorrect(root, best, prefix, 0);
        if(best.node == null) return list;
        if(best.node.val != null && best.Prekey.equals(prefix)) list.add(prefix);
        getKeySet(best.node.mid, best.Prekey, list);
        return list;
    }

    private void getBestCorrect(Node node, NodeWithPrekey nodeWithPrekey, String key, int d) {
        if (node == null || d >= key.length()) return;
        char c = key.charAt(d);
        if (c < node.c) {
            if (node.left == null) nodeWithPrekey.node = null;
            getBestCorrect(node.left, nodeWithPrekey, key, d);
        } else if (c > node.c){
            if(node.right == null) nodeWithPrekey.node = null;
            getBestCorrect(node.right, nodeWithPrekey, key, d);
        }
        else if(d <= key.length() -1){
            nodeWithPrekey.Prekey = nodeWithPrekey.Prekey+c;
            nodeWithPrekey.node = node;
            getBestCorrect(node.mid, nodeWithPrekey, key, d+1);
        }
    }


    public List<Value> getValueSet(){
        List<Value> list = new ArrayList<>();
        getValueSet(root, list);
        return list;
    }
    private void getValueSet(Node node, List<Value> list) {
        if(node == null) return;
        getValueSet(node.left, list);
        getValueSet(node.right, list);
        if(node.val != null) list.add(node.val);
        getValueSet(node.mid, list);
    }


    public void put(String key, Value val) {
        root = put(root, key.toLowerCase(), val, 0);
    }
    private Node put(Node x, String key, Value val, int d)
    {
        char c = key.charAt(d);
        if (x == null) { x = new Node(); x.c = c; }
        if (c < x.c) x.left = put(x.left, key, val, d);
        else if (c > x.c) x.right = put(x.right, key, val, d);
        else if (d < key.length() - 1)
            x.mid = put(x.mid, key, val, d+1);
        else x.val = val;
        return x;
    }

    public List<String> keysetContaining(String key){
        List<String> list = new ArrayList<>();
        for(char i=97; i<=122; i++){
            list.addAll(getKeySetWithprefix(i+key));
        }
        return list;
    }

}
