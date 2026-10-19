package edu.dlut.campuscard.service;

import edu.dlut.campuscard.account.AccountGroup;
import edu.dlut.campuscard.account.Ledger;
import edu.dlut.campuscard.account.MerchantAccount;
import edu.dlut.campuscard.account.PersonalAccount;
import edu.dlut.campuscard.account.ReportVisitor;
import edu.dlut.campuscard.config.AppConfig;
import edu.dlut.campuscard.gateway.PaymentGatewayFactory;
import edu.dlut.campuscard.model.Card;
import edu.dlut.campuscard.model.CardType;
import edu.dlut.campuscard.model.Merchant;
import edu.dlut.campuscard.model.Result;
import edu.dlut.campuscard.model.Transaction;
import edu.dlut.campuscard.notify.AppPushListener;
import edu.dlut.campuscard.notify.CardEvent;
import edu.dlut.campuscard.notify.EmailListener;
import edu.dlut.campuscard.notify.EventPublisher;
import edu.dlut.campuscard.notify.SmsListener;
import edu.dlut.campuscard.pricing.PriceCalculator;
import edu.dlut.campuscard.state.LostState;
import edu.dlut.campuscard.state.NormalState;
import edu.dlut.campuscard.validation.ValidationChain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 一卡通门面（Facade 模式）+ 领域存储。
 *
 * <p>重构前，所有能力（开户、消费、充值、挂失、报表……）都堆在一个上帝类里。
 * 重构后对外只暴露本门面，内部把计费、校验、状态、账户、通知、支付等子系统
 * 编排为清晰的分工，调用方无须了解任何子系统细节。</p>
 */
public class CardService {

    private final AppConfig config;

    private final Map<String, Card> cards = new LinkedHashMap<>();
    private final Map<String, Merchant> merchants = new LinkedHashMap<>();
    private final Map<String, MerchantAccount> merchantAccounts = new HashMap<>();
    private final AccountGroup accountBook = new AccountGroup();
    private final Ledger ledger = new Ledger();
    private final List<Transaction> transactions = new ArrayList<>();
    private final EventPublisher publisher = new EventPublisher();

    private final ValidationChain validationChain;
    private final PriceCalculator priceCalculator;
    private final PaymentGatewayFactory gatewayFactory;
    private final ConsumeFlow consumeFlow;

    private int txnSeq = 0;

    public CardService() {
        this.config = AppConfig.get();
        this.validationChain = new ValidationChain(config);
        this.priceCalculator = new PriceCalculator(config);
        this.gatewayFactory = new PaymentGatewayFactory();
        // 订阅三类通知渠道（Observer）
        publisher.subscribe(new SmsListener());
        publisher.subscribe(new AppPushListener());
        publisher.subscribe(new EmailListener());
        this.consumeFlow = new StandardConsumeFlow(this);
    }

    // ------------------------------------------------------------------
    // 开户
    // ------------------------------------------------------------------
    /** 新增卡片。 */
    public void addCard(String id, String holder, String type, boolean vip, long balance) {
        Card card = new Card(id, holder, CardType.from(type), vip, balance);
        cards.put(id, card);
        accountBook.add(new PersonalAccount(card));
    }

    /** 新增商户。 */
    public void addMerchant(String id, String name, String type) {
        merchants.put(id, new Merchant(id, name, type));
        MerchantAccount account = new MerchantAccount(id, type);
        merchantAccounts.put(id, account);
        accountBook.add(account);
    }

    // ------------------------------------------------------------------
    // 对外业务
    // ------------------------------------------------------------------
    /** 消费（委托给消费流程模板）。 */
    public Result consume(String cardId, String merchantId, long amount, int hour) {
        return consumeFlow.execute(cardId, merchantId, amount, hour);
    }

    /** 充值。 */
    public Result recharge(String cardId, long amount) {
        Card card = cards.get(cardId);
        if (card == null) {
            return Result.reject("卡不存在", 0);
        }
        String rejected = card.state().rejectRecharge();
        if (rejected != null) {
            return Result.reject(rejected, card.balance());
        }
        if (amount <= 0) {
            return Result.reject("金额非法", card.balance());
        }
        card.increaseBalance(amount);
        ledger.addRecharged(amount);
        return Result.ok(0, card.balance());
    }

    /** 挂失。 */
    public Result reportLoss(String cardId) {
        Card card = cards.get(cardId);
        if (card == null) {
            return Result.reject("卡不存在", 0);
        }
        if (!card.state().canReportLoss()) {
            return Result.reject("当前状态不可挂失", card.balance());
        }
        card.transitionTo(LostState.INSTANCE);
        publisher.publish(new CardEvent(
                CardEvent.EventType.LOSS, card.id(), card.holder(), "卡 " + card.id() + " 已挂失"));
        return Result.ok(0, card.balance());
    }

    /** 解挂。 */
    public Result recover(String cardId) {
        Card card = cards.get(cardId);
        if (card == null) {
            return Result.reject("卡不存在", 0);
        }
        if (!card.state().canRecover()) {
            return Result.reject("当前状态不可解挂", card.balance());
        }
        card.transitionTo(NormalState.INSTANCE);
        return Result.ok(0, card.balance());
    }

    /** 对账报表。 */
    public String report() {
        ReportVisitor visitor = new ReportVisitor(ledger);
        accountBook.accept(visitor);
        return visitor.toReportString();
    }

    // ------------------------------------------------------------------
    // 查询
    // ------------------------------------------------------------------
    /** 按卡号取卡（可能为 null）。 */
    public Card card(String cardId) {
        return cards.get(cardId);
    }

    /** 按商户号取商户（可能为 null）。 */
    public Merchant merchant(String merchantId) {
        return merchants.get(merchantId);
    }

    /** 全部卡号（升序）。 */
    public List<String> cardIds() {
        List<String> ids = new ArrayList<>(cards.keySet());
        ids.sort(String::compareTo);
        return ids;
    }

    // ------------------------------------------------------------------
    // 供消费流程访问（包内可见）
    // ------------------------------------------------------------------
    ValidationChain validationChain() {
        return validationChain;
    }

    PriceCalculator priceCalculator() {
        return priceCalculator;
    }

    PaymentGatewayFactory gatewayFactory() {
        return gatewayFactory;
    }

    Ledger ledger() {
        return ledger;
    }

    List<Transaction> transactions() {
        return transactions;
    }

    EventPublisher publisher() {
        return publisher;
    }

    MerchantAccount merchantAccount(String merchantId) {
        return merchantAccounts.get(merchantId);
    }

    int nextTxnSeq() {
        return ++txnSeq;
    }

    AppConfig config() {
        return config;
    }
}
