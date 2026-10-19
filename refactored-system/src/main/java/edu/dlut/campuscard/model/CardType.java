package edu.dlut.campuscard.model;

/**
 * 持卡人类型。
 *
 * <p>重构前用裸字符串比较（{@code c.type.equals("STUDENT")}），
 * 重构后用枚举承载，非法值统一落到 {@link #STAFF}（与重构前 else 分支语义一致）。</p>
 */
public enum CardType {

    /** 学生。 */
    STUDENT,
    /** 教师。 */
    TEACHER,
    /** 教职工/其他。 */
    STAFF;

    /** 由字符串解析，未知类型归为 {@link #STAFF}。 */
    public static CardType from(String code) {
        if ("STUDENT".equals(code)) {
            return STUDENT;
        }
        if ("TEACHER".equals(code)) {
            return TEACHER;
        }
        return STAFF;
    }
}
