package net.sourceforge.pmd.examples.lombok.rules;

import net.sourceforge.pmd.test.SimpleAggregatorTst;

public class MyRuleTest extends SimpleAggregatorTst {

    @Override
    protected void setUp() {
        addRule("net/sourceforge/pmd/examples/lombok/rules/MyRule.xml", "MyRule");
    }
}
