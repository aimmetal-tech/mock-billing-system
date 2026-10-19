package edu.dlut.campuscard.model;

/**
 * 商户（不可变）。
 *
 * <p>{@code type} 取值：canteen / supermarket / laundry / print / online，
 * 是计费策略工厂的路由键，同时作为报表分组键。</p>
 */
public final class Merchant {

    private final String id;
    private final String name;
    private final String type;

    public Merchant(String id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String type() {
        return type;
    }
}
