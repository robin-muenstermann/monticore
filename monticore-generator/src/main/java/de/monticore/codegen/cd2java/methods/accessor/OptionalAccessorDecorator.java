package de.monticore.codegen.cd2java.methods.accessor;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.TypesHelper;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class OptionalAccessorDecorator extends AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> {

  private static final String GET = "get%s";

  protected static final String GET_OPT = "get%sOpt";

  private static final String IS_PRESENT = "isPresent%s";

  private String naiveAttributeName;

  public OptionalAccessorDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    //todo find better util than the DecorationHelper
    naiveAttributeName = StringUtils.capitalize(DecorationHelper.getNativeAttributeName(ast.getName()));
    ASTCDMethod get = createGetMethod(ast);
    ASTCDMethod getOpt = createGetOptMethod(ast);
    ASTCDMethod isPresent = createIsPresentMethod(ast);
    return new ArrayList<>(Arrays.asList(get, getOpt, isPresent));
  }

  private ASTCDMethod createGetMethod(final ASTCDAttribute ast) {
    String name = String.format(GET, naiveAttributeName);
    ASTType type = TypesHelper.getSimpleReferenceTypeFromOptional(ast.getType().deepClone());
    ASTCDMethod method = this.getCDMethodFactory().createMethod(PUBLIC, type, name);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.opt.Get", ast));
    return method;
  }

  protected ASTCDMethod createGetOptMethod(final ASTCDAttribute ast) {
    String name = String.format(GET_OPT, naiveAttributeName);
    ASTType type = ast.getType().deepClone();
    ASTCDMethod method = this.getCDMethodFactory().createMethod(PUBLIC, type, name);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.Get", ast));
    return method;
  }

  private ASTCDMethod createIsPresentMethod(final ASTCDAttribute ast) {
    String name = String.format(IS_PRESENT, naiveAttributeName);
    ASTCDMethod method = this.getCDMethodFactory().createMethod(PUBLIC, this.getCDTypeFactory().createBooleanType(), name);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.opt.IsPresent", ast));
    return method;
  }
}
