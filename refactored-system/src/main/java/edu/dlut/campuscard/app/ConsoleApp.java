package edu.dlut.campuscard.app;

import edu.dlut.campuscard.model.Result;
import edu.dlut.campuscard.service.CardService;

/**
 * 重构后系统的控制台入口（与重构前 Main 演示同一组操作）。
 *
 * <p>运行：{@code mvn -q -pl refactored-system exec:java}</p>
 */
public class ConsoleApp {

    public static void main(String[] args) {
        CardService service = new CardService();
        service.addCard("C001", "张三", "STUDENT", true, 30000);
        service.addCard("C002", "李四", "TEACHER", false, 8000);
        service.addMerchant("M01", "一食堂", "canteen");
        service.addMerchant("M02", "教育超市", "supermarket");
        service.addMerchant("M03", "洗衣房", "laundry");
        service.addMerchant("M04", "文印中心", "print");

        System.out.println("== 重构后（清晰结构）演示 ==");
        print("张三食堂消费", service.consume("C001", "M01", 1500, 12));
        print("张三洗衣房消费(应收10%服务费)", service.consume("C001", "M03", 2000, 15));
        print("李四文印消费(应收5%)", service.consume("C002", "M04", 1000, 16));
        print("张三挂失", service.reportLoss("C001"));
        print("张三挂失后消费(应被拒)", service.consume("C001", "M01", 500, 12));
        print("张三解挂", service.recover("C001"));
        print("张三充值", service.recharge("C001", 10000));
        System.out.println("报表: " + service.report());
    }

    private static void print(String what, Result r) {
        if (r.isOk()) {
            System.out.println(what + " => OK, 实付=" + r.paid + ", 余额=" + r.balance);
        } else {
            System.out.println(what + " => 拒绝: " + r.reason + ", 余额=" + r.balance);
        }
    }
}
