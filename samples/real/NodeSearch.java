public class NodeSearch {
    public Node find(Node root, String target) {
        if (root != null) {
            if (root.val.equals(target)) {
                return root;
            } else {
                if (root.left != null) {
                    Node l = find(root.left, target);
                    if (l != null) {
                        return l;
                    }
                }
                if (root.right != null) {
                    return find(root.right, target);
                }
            }
        }
        return null;
    }
}
