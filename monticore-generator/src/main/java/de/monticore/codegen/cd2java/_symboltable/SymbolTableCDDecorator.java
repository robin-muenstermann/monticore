package de.monticore.codegen.cd2java._symboltable;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java._symboltable.scope.*;
import de.monticore.codegen.cd2java._symboltable.symbol.SymbolBuilderDecorator;
import de.monticore.codegen.cd2java._symboltable.symbol.SymbolDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.PACKAGE;
import static de.monticore.codegen.cd2java.CoreTemplates.createPackageHookPoint;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SYMBOL_TABLE_PACKAGE;

public class SymbolTableCDDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  protected final SymbolDecorator symbolDecorator;

  protected final SymbolBuilderDecorator symbolBuilderDecorator;

  protected final SymbolTableService symbolTableService;

  protected final ScopeClassDecorator scopeClassDecorator;

  protected final ScopeClassBuilderDecorator scopeClassBuilderDecorator;

  protected final ScopeInterfaceDecorator scopeInterfaceDecorator;

  protected final GlobalScopeInterfaceDecorator globalScopeInterfaceDecorator;

  protected final GlobalScopeClassDecorator globalScopeClassDecorator;

  protected final GlobalScopeClassBuilderDecorator globalScopeClassBuilderDecorator;

  protected final ArtifactScopeDecorator artifactScopeDecorator;

  protected final ArtifactScopeBuilderDecorator artifactScopeBuilderDecorator;

  public SymbolTableCDDecorator(final GlobalExtensionManagement glex,
                                final SymbolTableService symbolTableService,
                                final SymbolDecorator symbolDecorator,
                                final SymbolBuilderDecorator symbolBuilderDecorator,
                                final ScopeClassDecorator scopeClassDecorator,
                                final ScopeClassBuilderDecorator scopeClassBuilderDecorator,
                                final ScopeInterfaceDecorator scopeInterfaceDecorator,
                                final GlobalScopeInterfaceDecorator globalScopeInterfaceDecorator,
                                final GlobalScopeClassDecorator globalScopeClassDecorator,
                                final GlobalScopeClassBuilderDecorator globalScopeClassBuilderDecorator,
                                final ArtifactScopeDecorator artifactScopeDecorator,
                                final ArtifactScopeBuilderDecorator artifactScopeBuilderDecorator) {
    super(glex);
    this.symbolDecorator = symbolDecorator;
    this.symbolBuilderDecorator = symbolBuilderDecorator;
    this.symbolTableService = symbolTableService;
    this.scopeClassDecorator = scopeClassDecorator;
    this.scopeClassBuilderDecorator = scopeClassBuilderDecorator;
    this.scopeInterfaceDecorator = scopeInterfaceDecorator;
    this.globalScopeInterfaceDecorator = globalScopeInterfaceDecorator;
    this.globalScopeClassDecorator = globalScopeClassDecorator;
    this.globalScopeClassBuilderDecorator = globalScopeClassBuilderDecorator;
    this.artifactScopeDecorator = artifactScopeDecorator;
    this.artifactScopeBuilderDecorator = artifactScopeBuilderDecorator;
  }

  @Override
  public ASTCDCompilationUnit decorate(ASTCDCompilationUnit ast) {
    List<String> symbolTablePackage = new ArrayList<>(ast.getPackageList());
    symbolTablePackage.addAll(Arrays.asList(ast.getCDDefinition().getName().toLowerCase(), SYMBOL_TABLE_PACKAGE));

    List<ASTCDType> symbolProds = symbolTableService.getSymbolDefiningProds(ast.getCDDefinition());

    List<ASTCDClass> decoratedSymbolClasses = createSymbolClasses(symbolProds);
    ASTCDClass scopeClass = createScopeClass(ast);

    ASTCDDefinition astCD = CD4AnalysisMill.cDDefinitionBuilder()
        .setName(ast.getCDDefinition().getName())
        .addAllCDClasss(decoratedSymbolClasses)
        .addAllCDClasss(createSymbolBuilderClasses(decoratedSymbolClasses))
        .addCDClass(scopeClass)
        .addCDClass(createScopeClassBuilder(scopeClass))
        .addCDInterface(createScopeInterface(ast))
        .build();
    if (symbolTableService.hasProd(ast.getCDDefinition())) {
      astCD.addCDInterface(createGlobalScopeInterface(ast));
      ASTCDClass globalScopeClass = createGlobalScopeClass(ast);
      astCD.addCDClass(globalScopeClass);
      astCD.addCDClass(createGlobalScopeClassBuilder(globalScopeClass));
      ASTCDClass artifactScope = createArtifactScope(ast);
      astCD.addCDClass(artifactScope);
      astCD.addCDClass(createArtifactBuilderScope(artifactScope));
    }

    for (ASTCDClass cdClass : astCD.getCDClassList()) {
      this.replaceTemplate(PACKAGE, cdClass, createPackageHookPoint(symbolTablePackage));
    }

    for (ASTCDInterface cdInterface : astCD.getCDInterfaceList()) {
      this.replaceTemplate(CoreTemplates.PACKAGE, cdInterface, createPackageHookPoint(symbolTablePackage));
    }

    for (ASTCDEnum cdEnum : astCD.getCDEnumList()) {
      this.replaceTemplate(CoreTemplates.PACKAGE, cdEnum, createPackageHookPoint(symbolTablePackage));
    }

    return CD4AnalysisMill.cDCompilationUnitBuilder()
        .setPackageList(symbolTablePackage)
        .setCDDefinition(astCD)
        .build();
  }

  protected List<ASTCDClass> createSymbolClasses(List<? extends ASTCDType> astcdTypeList) {
    return astcdTypeList
        .stream()
        .map(symbolDecorator::decorate)
        .collect(Collectors.toList());
  }

  protected List<ASTCDClass> createSymbolBuilderClasses(List<ASTCDClass> symbolASTClasses) {
    return symbolASTClasses
        .stream()
        .map(symbolBuilderDecorator::decorate)
        .collect(Collectors.toList());
  }

  protected ASTCDClass createScopeClass(ASTCDCompilationUnit astcdCompilationUnit) {
    return scopeClassDecorator.decorate(astcdCompilationUnit);
  }

  protected ASTCDClass createScopeClassBuilder(ASTCDClass scopeClass) {
    return scopeClassBuilderDecorator.decorate(scopeClass);
  }

  protected ASTCDInterface createScopeInterface(ASTCDCompilationUnit compilationUnit) {
    return scopeInterfaceDecorator.decorate(compilationUnit);
  }

  protected ASTCDInterface createGlobalScopeInterface(ASTCDCompilationUnit compilationUnit) {
    return globalScopeInterfaceDecorator.decorate(compilationUnit);
  }

  protected ASTCDClass createGlobalScopeClass(ASTCDCompilationUnit compilationUnit) {
    return globalScopeClassDecorator.decorate(compilationUnit);
  }

  protected ASTCDClass createGlobalScopeClassBuilder(ASTCDClass globalScopeClass) {
    return globalScopeClassBuilderDecorator.decorate(globalScopeClass);
  }

  protected ASTCDClass createArtifactScope(ASTCDCompilationUnit compilationUnit) {
    return artifactScopeDecorator.decorate(compilationUnit);
  }

  protected ASTCDClass createArtifactBuilderScope(ASTCDClass artifactScopeClass) {
    return artifactScopeBuilderDecorator.decorate(artifactScopeClass);
  }
}
