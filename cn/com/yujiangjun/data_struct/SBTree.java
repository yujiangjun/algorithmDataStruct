package cn.com.yujiangjun.data_struct;

public class SBTree<K extends Comparable<K>, V> {

    private SBNode<K, V> root;

    public static class SBNode<K extends Comparable<K>, V> {
        private K key;
        private V value;
        private SBNode<K, V> left;
        private SBNode<K, V> right;
        private int size;

        public SBNode(K key, V value) {
            this.key = key;
            this.value = value;
            size = 1;
        }
    }

    private SBNode<K, V> leftRotate(SBNode<K, V> cur) {
        SBNode<K, V> right = cur.right;
        cur.right = right.left;
        right.left = cur;
        right.size = cur.size;
        cur.size = (cur.left != null ? cur.left.size : 0) + (cur.right != null ? cur.right.size : 0) + 1;
        return right;
    }

    private SBNode<K, V> rightRotate(SBNode<K, V> cur) {
        SBNode<K, V> left = cur.left;
        cur.left = left.right;
        left.right = cur;
        left.size = cur.size;
        cur.size = (cur.left != null ? cur.left.size : 0) + (cur.right != null ? cur.right.size : 0) + 1;
        return left;
    }

    private SBNode<K, V> maintain(SBNode<K, V> cur) {
        if (cur == null) {
            return null;
        }
        int leftSize = cur.left != null ? cur.left.size : 0;
        int leftLeftSize = cur.left != null && cur.left.left != null ? cur.left.left.size : 0;
        int leftRightSize = cur.left != null && cur.left.right != null ? cur.left.right.size : 0;
        int rightSize = cur.right != null ? cur.right.size : 0;
        int rightLeftSize = cur.right != null && cur.right.left != null ? cur.right.left.size : 0;
        int rightRightSize = cur.right != null && cur.right.right != null ? cur.right.right.size : 0;

        if (leftLeftSize > rightSize) {
            cur = rightRotate(cur);
            cur.right = maintain(cur.right);
            cur = maintain(cur);
        } else if (leftRightSize > rightSize) {
            cur.left = leftRotate(cur.left);
            cur = rightRotate(cur);
            cur.left = maintain(cur.left);
            cur.right = maintain(cur.right);
            cur = maintain(cur);
        } else if (rightRightSize > leftSize) {
            cur = leftRotate(cur);
            cur.left = maintain(cur.left);
            cur = maintain(cur);
        } else if (rightLeftSize > leftSize) {
            cur.right = rightRotate(cur.right);
            cur = leftRotate(cur);
            cur.left = maintain(cur.left);
            cur.right = maintain(cur.right);
            cur = maintain(cur);
        }
        return cur;
    }

    public SBNode<K, V> add(SBNode<K, V> cur, K key, V value) {
        if (cur == null) {
            return new SBNode<>(key, value);
        }
        cur.size++;
        if (key.compareTo(cur.key) < 0) {
            cur.left = add(cur.left, key, value);
        } else if (key.compareTo(cur.key) > 0) {
            cur.right = add(cur.right, key, value);
        }
        return maintain(cur);
    }

    public SBNode<K, V> delete(SBNode<K, V> cur, K key) {
        cur.size--;
        if (key.compareTo(cur.key) < 0) {
            cur = delete(cur.left, key);
        } else if (key.compareTo(cur.key) > 0) {
            cur = delete(cur.right, key);
        } else {
            if (cur.left == null && cur.right == null) {
                cur = null;
            } else if (cur.left == null && cur.right != null) {
                cur = cur.right;
            } else if (cur.left != null && cur.right == null) {
                cur = cur.left;
            } else {
                SBNode<K, V> pre = null;
                SBNode<K, V> des = cur.right;
                des.size--;
                while (des.left != null) {
                    pre = des;
                    des = des.left;
                    des.size--;
                }
                if (pre != null) {
                    pre.left = des.right;
                    des.right = cur.right;
                }
                des.left = cur.left;
                des.size = (des.left != null ? des.left.size : 0) + (des.right != null ? des.right.size : 0) + 1;
                cur = des;
            }
        }
        return cur;
    }

    private SBNode<K, V> findLastIndex(K key) {
        SBNode<K, V> cur = root;
        SBNode<K, V> pre = root;
        while (cur != null) {
            pre = cur;
            if (key.compareTo(cur.key) == 0) {
                break;
            } else if (key.compareTo(cur.key) < 0) {
                cur = cur.left;
            } else {
                cur = cur.right;
            }
        }
        return pre;
    }

    public void put(SBNode<K, V> cur, K key, V value) {
        if (key==null){
            throw new RuntimeException("invalid parameter");
        }
        SBNode<K, V> lastIndex = findLastIndex(key);
        if (lastIndex!=null&&lastIndex.key.compareTo(key)==0){
            lastIndex.value=value;
        }else {
            root=add(root,key,value);
        }
    }

    /**
     * 小于等于key
     * @param key
     * @return
     */
    private SBNode<K,V> findLastNoBigIndex(K key){
        SBNode<K,V> cur=root;
        SBNode<K,V> ans=null;
        while (cur!=null){
            if (key.compareTo(cur.key)==0){
                ans=cur;
                break;
            } else if (key.compareTo(cur.key) < 0) {
                cur=cur.left;
            }else {
                ans=cur;
                cur=cur.right;
            }
        }
        return ans;
    }

    /**
     * 大于等于
     * @param key
     * @return
     */
    private SBNode<K,V> findLastNoSmallIndex(K key){
        SBNode<K,V> cur=root;
        SBNode<K,V> ans=null;
        while (cur!=null){
            if (key.compareTo(cur.key)==0){
                ans=cur;
                break;
            } else if (key.compareTo(cur.key) < 0) {
                ans=cur;
                cur=cur.left;
            }else {
                cur=cur.right;
            }
        }
        return ans;
    }

    public int size(){
        return root==null?0:root.size;
    }

    private SBNode<K, V> getIndex(SBNode<K, V> cur, int kth) {
        if (kth == (cur.left != null ? cur.left.size : 0) + 1) {
            return cur;
        } else if (kth <= (cur.left != null ? cur.left.size : 0)) {
            return getIndex(cur.left, kth);
        } else {
            return getIndex(cur.right, kth - (cur.left != null ? cur.left.size : 0) - 1);
        }
    }

    public boolean containsKey(K key){
        if (key==null){
            throw new RuntimeException("invalid parameter");
        }
        SBNode<K, V> lastIndex = findLastIndex(key);
        return lastIndex!=null&&lastIndex.key.compareTo(key)==0;
    }

    public void remove(K key){
        if (key==null){
            throw new RuntimeException("invalid parameter");
        }
        if (containsKey(key)){
            root=delete(root,key);
        }
    }

    public K getIndexKey(int index){
        if (index<0||index>=this.size()){
            throw new RuntimeException("Invalid parameter");
        }
        return getIndex(root,index+1).key;
    }

    public V getIndexValue(int index){
        if (index<0||index>=this.size()){
            throw new RuntimeException("Invalid parameter");
        }
        return getIndex(root,index+1).value;
    }

    public V get(K key){
        if (key==null){
            throw new RuntimeException("Invalid parameter");
        }
        SBNode<K, V> lastIndex = findLastIndex(key);
        if (lastIndex!=null && lastIndex.key.compareTo(key)==0){
            return lastIndex.value;
        }else {
            return null;
        }
    }

    public K firstKey(){
        if (root==null){
            return null;
        }
        SBNode<K,V> cur=root;
        while (cur.left!=null){
            cur=cur.left;
        }
        return cur.key;
    }

    public K lastKey(){
        if (root==null){
            return null;
        }
        SBNode<K,V> cur=root;
        while (cur.right!=null){
            cur=cur.right;
        }
        return cur.key;
    }

    public K floorKey(K key){
        if (key==null){
            throw new RuntimeException("Invalid parameter");
        }
        SBNode<K, V> lastNoBigIndex = findLastNoBigIndex(key);
        return lastNoBigIndex==null?null:lastNoBigIndex.key;
    }
    public K ceilingKey(K key){
        if (key==null){
            throw new RuntimeException("Invalid parameter");
        }
        SBNode<K, V> lastNoBigIndex = findLastNoSmallIndex(key);
        return lastNoBigIndex==null?null:lastNoBigIndex.key;
    }
}
