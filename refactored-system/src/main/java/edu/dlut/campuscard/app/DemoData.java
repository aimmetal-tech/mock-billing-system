package edu.dlut.campuscard.app;

import edu.dlut.campuscard.service.CardService;

/**
 * 演示数据：为 REST 壳与前端对比页准备一组固定的卡片与商户。
 *
 * <p>与重构前 {@code bad.BadApiServer} 使用的演示数据完全一致，保证并排对比时输入相同。</p>
 */
final class DemoData {

    private DemoData() {
    }

    /** 构建带演示数据的一卡通服务。 */
    static CardService build() {
        CardService service = new CardService();
        service.addCard("C001", "张三", "STUDENT", true, 30000);
        service.addCard("C002", "李四", "TEACHER", false, 8000);
        service.addCard("C003", "王五", "STAFF", false, 12000);
        service.addMerchant("M01", "一食堂", "canteen");
        service.addMerchant("M02", "教育超市", "supermarket");
        service.addMerchant("M03", "洗衣房", "laundry");
        service.addMerchant("M04", "文印中心", "print");
        service.addMerchant("M05", "校园网", "online");
        return service;
    }
}
