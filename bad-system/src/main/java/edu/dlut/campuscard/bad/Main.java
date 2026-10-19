package edu.dlut.campuscard.bad;

/**
 * 重构前系统的控制台入口（演示坏味道带来的可观测后果）。
 * 运行：mvn -q -pl bad-system exec:java
 */
public class Main {

    public static void main(String[] args) {
        CampusCardSystem sys = new CampusCardSystem();
        sys.addCard("C001", "张三", "STUDENT", true, 30000);
        sys.addCard("C002", "李四", "TEACHER", false, 8000);
        sys.addMerchant("M01", "一食堂", "canteen");
        sys.addMerchant("M02", "教育超市", "supermarket");
        sys.addMerchant("M03", "洗衣房", "laundry");
        sys.addMerchant("M04", "文印中心", "print");

        System.out.println("== 重构前（坏系统）演示 ==");
        print("张三食堂消费", sys.consume("C001", "M01", 1500, 12));
        print("张三洗衣房消费(应加收10%服务费)", sys.consume("C001", "M03", 2000, 15));
        print("李四文印消费(应加收5%)", sys.consume("C002", "M04", 1000, 16));
        print("张三挂失", sys.reportLoss("C001"));
        print("张三挂失后消费(应被拒)", sys.consume("C001", "M01", 500, 12));
        print("张三解挂", sys.recover("C001"));
        print("张三充值", sys.recharge("C001", 10000));
        System.out.println("报表: " + sys.report());
    }

    private static void print(String what, CampusCardSystem.Result r) {
        if ("OK".equals(r.status)) {
            System.out.println(what + " => OK, 实付=" + r.paid + ", 余额=" + r.balance);
        } else {
            System.out.println(what + " => 拒绝: " + r.reason + ", 余额=" + r.balance);
        }
    }
}
