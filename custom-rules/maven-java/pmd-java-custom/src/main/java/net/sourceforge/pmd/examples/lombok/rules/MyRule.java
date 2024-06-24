package net.sourceforge.pmd.examples.lombok.rules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;

import org.checkerframework.checker.nullness.qual.NonNull;

import br.com.agrotis.collection.CircularDependencyChecker;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassType;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;

public class MyRule extends AbstractJavaRule {
    private static BinaryOperator<String> toArrowString = ((clazzes, clazz) -> clazzes + "->" + clazz);
    Map<String, ASTClassDeclaration> classRelationshipsMap = new HashMap<>();
    CircularDependencyChecker classRelationships = new CircularDependencyChecker();

    private static final PropertyDescriptor<String> BAD_NAME = PropertyFactory.stringProperty("badName")
            .defaultValue("foo")
            .desc("The variable name that should not be used.")
            .build();

    public MyRule() {
        definePropertyDescriptor(BAD_NAME);
    }

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTClassDeclaration.class);
    }

    @Override
    public Object visit(ASTClassDeclaration node, Object data) {
        if (isLombokDataAnnotated(node)) {
            classRelationshipsMap.put(node.getSimpleName(), node);
            for (ASTFieldDeclaration field : node.descendants(ASTFieldDeclaration.class).toList()) {
                ASTClassType fieldType = field.descendants(ASTClassType.class).first();
                if (fieldType != null) {
                    String fieldTypeImage = fieldType.getSimpleName();
                    classRelationships.addDependency(node.getSimpleName(), fieldTypeImage);
                }
            }
        }
        return data;
    }

    @Override
    public void end(RuleContext ctx) {
        List<List<String>> cycles = classRelationships.getAllCircularDependencies();
        for (List<String> cycle : cycles) {
            ctx.addViolation(classRelationshipsMap.get(cycle.get(0)), cycle.stream().reduce("", toArrowString));
        }
        super.end(ctx);
    }

    private boolean isLombokDataAnnotated(ASTClassDeclaration node) {
        for (ASTAnnotation annotation : node.descendants(ASTAnnotation.class).toList()) {
            if ("Data".equals(annotation.getSimpleName())) {
                return true;
            }
        }
        return false;
    }
}
