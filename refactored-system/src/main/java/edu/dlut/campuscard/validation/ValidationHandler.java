package edu.dlut.campuscard.validation;

import edu.dlut.campuscard.model.Result;

/**
 * 校验处理器（Chain of Responsibility 模式）。
 *
 * <p>取代重构前 {@code consume()} 里内联的一大串 {@code if (...) return reject(...)}：
 * 每个规则独立成处理器，按装配顺序串成链，任一环节不通过即短路返回拒绝结果。</p>
 */
public abstract class ValidationHandler {

    /** 下一个处理器。 */
    private ValidationHandler next;

    /** 设置下一个处理器并返回它，便于链式装配。 */
    public ValidationHandler setNext(ValidationHandler next) {
        this.next = next;
        return next;
    }

    /**
     * 执行本环节校验，不通过则返回拒绝结果，通过则交给下一环节。
     *
     * @return 拒绝结果；整条链全部通过时返回 {@code null}
     */
    public Result handle(ConsumeContext context) {
        Result rejected = check(context);
        if (rejected != null) {
            return rejected;
        }
        return next == null ? null : next.handle(context);
    }

    /** 子类实现：不通过返回拒绝结果，通过返回 {@code null}。 */
    protected abstract Result check(ConsumeContext context);
}
