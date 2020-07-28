package root.report.util;

import java.io.Serializable;
import java.util.List;

public class MenuNode implements Serializable {
    private String func_id;
    private String func_pid;
    private String func_name;
    private String func_url;
    private String func_icon;
    private List<MenuNode> children;

    public MenuNode() {
    }

    public MenuNode(String func_id, String func_pid, String func_name, String func_url, String func_icon) {
        super();
        this.func_id = func_id;
        this.func_pid = func_pid;
        this.func_name = func_name;
        this.func_url = func_url;
        this.func_icon = func_icon;
    }

    public String getFunc_id() {
        return func_id;
    }

    public void setFunc_id(String func_id) {
        this.func_id = func_id;
    }

    public String getFunc_pid() {
        return func_pid;
    }

    public void setFunc_pid(String func_pid) {
        this.func_pid = func_pid;
    }

    public String getFunc_name() {
        return func_name;
    }

    public void setFunc_name(String func_name) {
        this.func_name = func_name;
    }

    public String getFunc_url() {
        return func_url;
    }

    public void setFunc_url(String func_url) {
        this.func_url = func_url;
    }

    public String getFunc_icon() {
        return func_icon;
    }

    public void setFunc_icon(String func_icon) {
        this.func_icon = func_icon;
    }

    public List<MenuNode> getChildren() {
        return children;
    }

    public void setChildren(List<MenuNode> children) {
        this.children = children;
    }
}
