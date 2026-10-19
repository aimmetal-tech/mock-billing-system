package edu.dlut.campuscard.account;

import java.util.ArrayList;
import java.util.List;

/**
 * 账户群组（Composite 模式的容器）：把个人账户与商户账户聚合成一棵树，
 * 对外可整体接受访问者的遍历。
 */
public class AccountGroup implements AccountComponent {

    private final List<AccountComponent> children = new ArrayList<>();

    /** 挂载子账户。 */
    public void add(AccountComponent child) {
        children.add(child);
    }

    @Override
    public void accept(AccountVisitor visitor) {
        for (AccountComponent child : children) {
            child.accept(visitor);
        }
    }
}
