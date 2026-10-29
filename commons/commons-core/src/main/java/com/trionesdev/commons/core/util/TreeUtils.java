package com.trionesdev.commons.core.util;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class TreeUtils {
    private TreeUtils() {
    }

    /**
     * 按输入顺序构建树，校验通过后替换节点原有的子列表内容。
     * 子列表为 null 时创建空列表，并通过 childrenSetter 写回节点。
     * 已有子列表必须独立且可修改。
     * 时间、空间复杂度均为 O(n)。
     */
    public static <T, K> List<T> buildTree(
            List<T> nodes,
            Function<T, K> idGetter,
            Function<T, K> parentIdGetter,
            Function<T, List<T>> childrenGetter,
            BiConsumer<T, List<T>> childrenSetter,
            K rootParentId) {

        Objects.requireNonNull(nodes, "nodes 不能为空");
        Objects.requireNonNull(idGetter, "idGetter 不能为空");
        Objects.requireNonNull(parentIdGetter, "parentIdGetter 不能为空");
        Objects.requireNonNull(childrenGetter, "childrenGetter 不能为空");
        Objects.requireNonNull(childrenSetter, "childrenSetter 不能为空");

        Map<K, IndexedNode<T, K>> nodeMap = new LinkedHashMap<>();

        // 第一次遍历：建立 ID 索引
        for (T node : nodes) {
            Objects.requireNonNull(node, "节点不能为空");
            K id = idGetter.apply(node);
            if (id == null) {
                throw new IllegalArgumentException("节点 ID 不能为空");
            }
            if (nodeMap.containsKey(id)) {
                throw new IllegalArgumentException(
                        "存在重复节点 ID: " + id);
            }
            var children = childrenGetter.apply(node);
            if (children == null) {
                children = new ArrayList<>();
            }
            nodeMap.put(id, new IndexedNode<>(node, parentIdGetter.apply(node), children));
        }

        // 修改子列表之前，校验全部父节点和环路，避免非法输入产生部分构建结果。
        for (var entry : nodeMap.entrySet()) {
            K parentId = entry.getValue().parentId();
            if (!Objects.equals(parentId, rootParentId) && !nodeMap.containsKey(parentId)) {
                throw new IllegalArgumentException(
                        "找不到父节点，nodeId=" + entry.getKey() + ", parentId=" + parentId);
            }
        }

        Set<K> validated = new HashSet<>();
        Set<K> path = new HashSet<>();
        for (K id : nodeMap.keySet()) {
            K currentId = id;
            while (!validated.contains(currentId)) {
                if (!path.add(currentId)) {
                    throw new IllegalArgumentException("存在循环父子关系，nodeId=" + currentId);
                }
                K parentId = nodeMap.get(currentId).parentId();
                if (Objects.equals(parentId, rootParentId)) {
                    break;
                }
                currentId = parentId;
            }
            validated.addAll(path);
            path.clear();
        }

        nodeMap.values().forEach(node -> {
            node.children().clear();
            childrenSetter.accept(node.value(), node.children());
        });
        List<T> roots = new ArrayList<>();
        for (var node : nodeMap.values()) {
            if (Objects.equals(node.parentId(), rootParentId)) {
                roots.add(node.value());
            } else {
                nodeMap.get(node.parentId()).children().add(node.value());
            }
        }
        return roots;
    }

    private record IndexedNode<T, K>(T value, K parentId, List<T> children) {
    }
}
