package edu.dlut.campuscard;

import edu.dlut.campuscard.app.SceneRunner;
import edu.dlut.campuscard.bad.BadScenarioRunner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 行为等价性验证：对同一场景脚本，比较重构前（bad-system）与重构后（refactored-system）
 * 两套执行器的归一化输出。
 *
 * <p>约定：除 {@code defect_unreachable_branch.json} 外，输出必须逐字符一致。
 * 该缺陷场景用于记录“重构暴露并修复的历史缺陷”，单独断言其差异符合预期。</p>
 */
class BehaviorEquivalenceTest {

    /** 场景脚本目录（相对模块工作目录）。 */
    private static final Path SCENARIOS = Path.of("..", "scenarios");

    /** 要求严格等价的场景。 */
    private static final List<String> EQUIVALENT_SCENARIOS = List.of(
            "basic_consume.json",
            "discount_stack.json",
            "lost_card.json",
            "boundary_reject.json",
            "report_unused_merchant.json"
    );

    private static String runBad(String scenarioName) throws IOException {
        return BadScenarioRunner.run(read(scenarioName));
    }

    private static String runGood(String scenarioName) throws IOException {
        return SceneRunner.run(read(scenarioName));
    }

    private static String read(String scenarioName) throws IOException {
        return Files.readString(SCENARIOS.resolve(scenarioName), StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("正常/有效场景：重构前后归一化输出逐字符一致")
    void normalScenariosAreEquivalent() throws IOException {
        for (String scenario : EQUIVALENT_SCENARIOS) {
            assertEquals(runBad(scenario), runGood(scenario),
                    "场景 " + scenario + " 的重构前后输出不一致");
        }
    }

    @Test
    @DisplayName("缺陷场景：差异仅限被修复的 laundry/print 计费分支")
    void defectScenarioDivergesAsDocumented() throws IOException {
        String bad = runBad("defect_unreachable_branch.json");
        String good = runGood("defect_unreachable_branch.json");

        // 差异确实存在（重构修复了不可达分支）
        assertNotEquals(bad, good, "缺陷场景应出现预期差异");

        // 重构前：洗衣房/文印按原价（分支不可达）
        assertTrue(bad.contains("consume C400 M03 2000 15 | OK paid=2000 balance=48000"),
                "重构前洗衣房应按原价计费");
        assertTrue(bad.contains("consume C400 M04 1000 16 | OK paid=1000 balance=47000"),
                "重构前文印应按原价计费");

        // 重构后：洗衣房 +10%、文印 +5%（分支真实可达）
        assertTrue(good.contains("consume C400 M03 2000 15 | OK paid=2200 balance=47800"),
                "重构后洗衣房应加收 10%");
        assertTrue(good.contains("consume C400 M04 1000 16 | OK paid=1050 balance=46750"),
                "重构后文印应加收 5%");
    }
}
