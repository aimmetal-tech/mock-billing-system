package edu.dlut.campuscard.account;

/** 账户访问者（Visitor 模式）：把报表/结算等算法从账户结构中解耦出来。 */
public interface AccountVisitor {

    /** 访问个人账户。 */
    void visit(PersonalAccount account);

    /** 访问商户账户。 */
    void visit(MerchantAccount account);
}
