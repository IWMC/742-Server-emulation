package sh.iwmc.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Brent on 01/26/2016.
 */
public class Permissions {

    private transient CommandSender owner;
    private Tree<String> permissionsTree = new Tree<>("root");

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
