package root.report.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 构造目录JSON树
 * Created by fukang on 2017/5/26 0026.
 */
public class MenuTreeBuilder {

    List<MenuNode> nodes = new ArrayList<>();

    public List<MenuNode> buildTree(List<MenuNode> nodes) {

        MenuTreeBuilder treeBuilder = new MenuTreeBuilder(nodes);

        return treeBuilder.buildJSONTree();
    }

    public MenuTreeBuilder() {
    }

    public MenuTreeBuilder(List<MenuNode> nodes) {
        super();
        this.nodes = nodes;
    }

    // 构建JSON树形结构
    public List<MenuNode> buildJSONTree() {
        List<MenuNode> nodeTree = buildTree();
        return nodeTree;
//        return JSONObject.toJSONString(nodeTree);
    }

    // 构建树形结构
    public List<MenuNode> buildTree() {
        List<MenuNode> treeNodes = new ArrayList<>();
        List<MenuNode> rootNodes = getRootNodes();
        for (MenuNode rootNode : rootNodes) {
            buildChildNodes(rootNode);
            treeNodes.add(rootNode);
        }
        return treeNodes;
    }

    // 递归子节点
    public void buildChildNodes(MenuNode node) {
        List<MenuNode> children = getChildNodes(node);
        if (!children.isEmpty()) {
            for (MenuNode child : children) {
                buildChildNodes(child);
            }
            node.setChildren(children);
        }
    }

    // 获取父节点下所有的子节点
    public List<MenuNode> getChildNodes(MenuNode pnode) {
        List<MenuNode> childNodes = new ArrayList<>();
        for (MenuNode n : nodes) {
            if (pnode.getFunc_id().equals(n.getFunc_pid())) {
                childNodes.add(n);
            }
        }
        return childNodes;
    }

    // 判断是否为根节点
    public boolean rootNode(MenuNode node) {
        if("0".equals(node.getFunc_pid()))
        {
            return true;
        } else{
            return false;
        }

    }

    // 获取集合中所有的根节点
    public List<MenuNode> getRootNodes() {
        List<MenuNode> rootNodes = new ArrayList<>();
        for (MenuNode n : nodes) {
            if (rootNode(n)) {
                rootNodes.add(n);
            }
        }
        return rootNodes;
    }


}


