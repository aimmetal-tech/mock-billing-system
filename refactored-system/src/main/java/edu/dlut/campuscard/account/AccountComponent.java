package edu.dlut.campuscard.account;

/**
 * 账户组件（Composite 模式的 Component）。
 *
 * <p>个人账户与商户账户是叶子，账户群组是容器，二者可被统一地遍历与访问，
 * 取代重构前“对私账户、商户账户分别写 for 循环硬算”的重复逻辑。</p>
 */
public interface AccountComponent {

    /** 接受访问者。 */
    void accept(AccountVisitor visitor);
}
