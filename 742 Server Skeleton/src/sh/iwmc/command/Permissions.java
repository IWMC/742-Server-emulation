package sh.iwmc.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Brent on 01/26/2016.
 */
public class Permissions {

    private transient CommandSender owner;
    private Tree<String> permissionsTree = new Tree<>("permissions");

    public Permissions(List<String> permissions) {
        /**
         * Example of a permissions list
         * sh.iwmc.commands.administration.*
         * sh.iwmc.commands.teleport.Home
         * some.other.path.*
         */
        for (String permission : permissions) {
            Node<String> base = permissionsTree.getRoot();
            String[] split = permission.split("\\.");
            for (String element : split) {
                Optional<Node<String>> result = base.getChildren().stream().filter(el -> el.getData().equals(element)).findFirst();
                if (result.isPresent()) {
                    if(result.get().getChildren().stream().anyMatch( c -> c.getData().equals("*"))) {
                        break;
                    }
                    base = result.get();
                    continue;
                } else {
                    if(!element.equals("*")) {
                        Node<String> node = new Node(element);
                        base.getChildren().add(node);
                        base = node;
                    } else {
                        base.getChildren().add(new Node("*"));
                        break;
                    }
                }
            }
        }
    }

    public void nodeToString(int indentation, Node<?> node, StringBuilder sb) {

        String indent = "";
        for(int i = 0; i < indentation; i++) {
            indent += " ";
        }
        sb.append(indent).append("- ").append(node.getData()).append("\n");
        for(Node child : node.getChildren()) {
            nodeToString(indentation + 2, child, sb);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        nodeToString(0, permissionsTree.getRoot(), sb);
        return sb.toString();
    }

    public Tree<String> getPermissions() {
        return permissionsTree;
    }

    public boolean hasPermission(String perm) {
        String[] split = perm.split("\\.");

        Node<String> base = permissionsTree.getRoot();
        for (String element : split) {
            boolean star = base.getChildren().stream().anyMatch(n -> n.getData().equals("*"));
            if(star) {
                return true;
            }

            Optional<Node<String>> node = base.getChildren().stream().filter(n -> n.getData().equals(element)).findAny();
            if(node.isPresent()) {
                base = node.get();
                continue;
            } else {
                return false;
            }
        }

        return true;
    }

    public static class Node<T> {
        private T data;
        private Node<T> parent;
        private List<Node<T>> children = new ArrayList<>();

        public Node(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public Node<T> getParent() {
            return parent;
        }

        public void setParent(Node<T> parent) {
            this.parent = parent;
        }

        public List<Node<T>> getChildren() {
            return children;
        }
    }

    public class Tree<T> {
        private Node<T> root;

        public Tree(T rootData) {
            root = new Node<>(rootData);
            root.children = new ArrayList<>();
        }

        public Node<T> getRoot() {
            return root;
        }
    }
}
