<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className")}

<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign grammarName = ast.getName()?cap_first>
<#assign package = genHelper.getTargetPackage()?lower_case>
<#assign topAstName = genHelper.getQualifiedStartRuleName()>
<#assign skipSTGen = glex.getGlobalVar("skipSTGen")>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${package};

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.ResolvingConfiguration;

<#if !skipSTGen>
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
</#if>

public class ${className} implements de.monticore.modelloader.IModelLoader<${topAstName}, I${grammarName}Scope> {

  public ${className}(${grammarName}Language language) {
    super(language);
  }

  @Override
  protected void createSymbolTableFromAST(final ${topAstName} ast, final String modelName, final I${grammarName}Scope enclosingScope) {
    <#if !skipSTGen>
    final ${grammarName}SymbolTableCreator symbolTableCreator =
            getModelingLanguage().getSymbolTableCreator(enclosingScope);

    if (symbolTableCreator != null) {
      Log.debug("Start creation of symbol table for model \"" + modelName + "\".",
          ${grammarName}ModelLoader.class.getSimpleName());
      final I${grammarName}Scope scope = symbolTableCreator.createFromAST(ast);

      if (!(scope instanceof ${grammarName}ArtifactScope)) {
        Log.warn("0xA7001${genHelper.getGeneratedErrorCode(ast)} Top scope of model " + modelName + " is expected to be an artifact scope, but"
          + " is scope \"" + scope.getName() + "\"");
      }

      Log.debug("Created symbol table for model \"" + modelName + "\".", ${grammarName}ModelLoader.class.getSimpleName());
    }
    else {
      Log.warn("0xA7002${genHelper.getGeneratedErrorCode(ast)} No symbol created, because '" + getModelingLanguage().getName()
        + "' does not define a symbol table creator.");
    }
    </#if>
  }

  @Override
  public ${grammarName}Language getModelingLanguage() {
    return (${grammarName}Language) super.getModelingLanguage();
  }
  
  public Collection<${topAstName}> loadModelsIntoScope(final String qualifiedModelName,
      final ModelPath modelPath, I${grammarName}Scope enclosingScope) {
    
    if (!loadSymbolsIntoScope(qualifiedModelName, modelPath, enclosingScope)) {
      final Collection<${topAstName}> asts = loadModels(qualifiedModelName, modelPath);
      for (${topAstName} ast : asts) {
        createSymbolTableFromAST(ast, qualifiedModelName, enclosingScope);
      }
      return asts;
    }
    return Collections.EMPTY_SET;
  }
  
  public Collection<${topAstName}> loadModels(final String qualifiedModelName, ModelPath modelPath){
    Preconditions.checkArgument(!Strings.isNullOrEmpty(qualifiedModelName));
    
    final Collection<${topAstName}> foundModels = new ArrayList<>();
    
    final ModelCoordinate resolvedCoordinate = resolve(qualifiedModelName, modelPath);
    if (resolvedCoordinate.hasLocation()) {
      final ${topAstName} ast = astProvider.getRootNode(resolvedCoordinate);
      Reporting.reportOpenInputFile(Optional.of(resolvedCoordinate.getParentDirectoryPath()),
          resolvedCoordinate.getQualifiedPath());
      foundModels.add(ast);
    }
    
    return foundModels;
  }
  
  public boolean loadSymbolsIntoScope(final String qualifiedModelName,
      final ModelPath modelPath, final I${grammarName}Scope enclosingScope)  {
    
    final ModelCoordinate resolvedCoordinate = resolveSymbol(qualifiedModelName, modelPath);
    if (resolvedCoordinate.hasLocation()) {
      // TODO AB
    }
    return false;
  }
  
  /**
   * @param qualifiedModelName example: "de.mc.statechartOne"
   * @return the resolved coordinate (the location of the model is set if
   * successful)
   */
  // TODO move to interface?
  private ModelCoordinate resolve(final String qualifiedModelName, final ModelPath modelPath){
    String simpleName = Names.getSimpleName(qualifiedModelName);
    Path qualifiedPath = Paths.get(
        Names.getPathFromQualifiedName(qualifiedModelName)).resolve(
        simpleName + "." + getModelingLanguage().getFileExtension());
    ModelCoordinate qualifiedModel = ModelCoordinates.createQualifiedCoordinate(qualifiedPath);
    
    return modelPath.resolveModel(qualifiedModel);
  }
  
      // TODO move to Interface?
  private ModelCoordinate resolveSymbol(final String qualifiedModelName, final ModelPath modelPath){
    String simpleName = Names.getSimpleName(qualifiedModelName);
    Path qualifiedPath = Paths.get(
        Names.getPathFromQualifiedName(qualifiedModelName)).resolve(
        simpleName + ".json");
    ModelCoordinate qualifiedModel = ModelCoordinates.createQualifiedCoordinate(qualifiedPath);
    
    return modelPath.resolveModel(qualifiedModel);
  }
  
}
