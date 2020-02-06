<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "referencedSymbolType")}
<#assign service = glex.getGlobalVar("service")>
    if (${attributeName}Symbol.isPresent() && ${attributeName}Symbol.get().isPresentAstNode()) {
      return ${attributeName}Symbol.get().getAstNode();
    } else if (isPresent${attributeName?cap_first}Symbol()) {
      ${referencedSymbolType} symbol = get${attributeName?cap_first}Symbol();
      if (symbol.isPresentAstNode()) {
        return symbol.getAstNode();
      }
    }
    Log.error("0xA7003${service.getGeneratedErrorCode(attributeName+referencedSymbolType)} ${attributeName}Definition can't return a value. It is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();