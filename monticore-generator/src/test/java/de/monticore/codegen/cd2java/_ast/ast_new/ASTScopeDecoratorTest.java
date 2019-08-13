package de.monticore.codegen.cd2java._ast.ast_new;

import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTScopeDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertOptionalOf;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static org.junit.Assert.*;

public class ASTScopeDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private List<ASTCDAttribute> attributes;

  private CDTypeFacade cdTypeFacade = CDTypeFacade.getInstance();

  private static final String AST_SCOPE = "de.monticore.codegen.ast.ast._symboltable.ASTScope";

  private static final String AST_I_SCOPE = "de.monticore.codegen.ast.ast._symboltable.IASTScope";

  private static final String SUPER_I_SCOPE= "de.monticore.codegen.ast.super._symboltable.ISuperScope";

  @Before
  public void setup() {
    this.cdTypeFacade = CDTypeFacade.getInstance();
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "ast", "AST");

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("service", new AbstractService(ast));

    ASTScopeDecorator decorator = new ASTScopeDecorator(this.glex, new SymbolTableService(ast));
    ASTCDClass clazz = getClassBy("A", ast);
    this.attributes = decorator.decorate(clazz);
  }

  @Test
  public void testAttributes() {
    assertFalse(attributes.isEmpty());
    assertEquals(4, attributes.size());
  }

  @Test
  public void testSpannedScopeAttribute() {
    Optional<ASTCDAttribute> symbolAttribute = attributes.stream().filter(x -> x.getName().equals("spannedASTScope")).findFirst();
    assertTrue(symbolAttribute.isPresent());
    assertDeepEquals(PROTECTED, symbolAttribute.get().getModifier());
    assertOptionalOf(AST_SCOPE, symbolAttribute.get().getType());
  }

  @Test
  public void testSpannedScope2Attribute() {
    Optional<ASTCDAttribute> symbolAttribute = attributes.stream().filter(x -> x.getName().equals("spannedScope2")).findFirst();
    assertTrue(symbolAttribute.isPresent());
    assertDeepEquals(PROTECTED, symbolAttribute.get().getModifier());
    assertOptionalOf(AST_I_SCOPE, symbolAttribute.get().getType());
  }

  @Test
  public void testEnclosingScope2AttributeInherited() {
    List<ASTCDAttribute> enclosingScope2 = attributes.stream().filter(x -> x.getName().equals("enclosingScope2")).collect(Collectors.toList());
    assertFalse(enclosingScope2.isEmpty());
    assertEquals(2, enclosingScope2.size());
    ASTCDAttribute scope = enclosingScope2.get(1);
    ASTModifier astModifier= PROTECTED.build();
    TransformationHelper.addStereotypeValue(astModifier, MC2CDStereotypes.INHERITED.toString());
    assertDeepEquals(astModifier, scope.getModifier());
    assertDeepEquals(SUPER_I_SCOPE, scope.getType());
  }

  @Test
  public void testEnclosingScope2Attribute() {
    List<ASTCDAttribute> enclosingScope2 = attributes.stream().filter(x -> x.getName().equals("enclosingScope2")).collect(Collectors.toList());
    assertFalse(enclosingScope2.isEmpty());
    assertEquals(2, enclosingScope2.size());
    ASTCDAttribute scope = enclosingScope2.get(0);
    assertDeepEquals(PROTECTED, scope.getModifier());
    assertDeepEquals(AST_I_SCOPE, scope.getType());
  }
}
