// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mcvardeclarationstatements._symboltable;

import com.google.common.collect.Lists;
import de.monticore.statements.mccommonstatements._ast.ASTJavaModifier;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCModifier;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTLocalVariableDeclaration;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.types.check.SymTypeArray;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfNull;
import de.monticore.types.check.SynthesizeSymTypeFromMCFullGenericTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.typesymbols._symboltable.OOTypeSymbolLoader;

import java.util.Deque;
import java.util.List;

import static de.monticore.statements.mccommonstatements._ast.ASTConstantsMCCommonStatements.*;

public class MCVarDeclarationStatementsSymbolTableCreator extends MCVarDeclarationStatementsSymbolTableCreatorTOP {
  public MCVarDeclarationStatementsSymbolTableCreator(IMCVarDeclarationStatementsScope enclosingScope) {
    super(enclosingScope);
  }

  public MCVarDeclarationStatementsSymbolTableCreator(Deque<? extends IMCVarDeclarationStatementsScope> scopeStack) {
    super(scopeStack);
  }

  public void endVisit(ASTLocalVariableDeclaration ast) {
    List<VarDeclSymbol> symbols = Lists.newArrayList();
    for (ASTVariableDeclarator v : ast.getVariableDeclaratorList()) {
      SymTypeExpression simpleType = createTypeLoader(ast.getMCType());
      if (v.getDeclaratorId().getDimList().size() > 0) {
        if (simpleType instanceof SymTypeArray) {
          SymTypeArray arraySymType = (SymTypeArray) simpleType;
          arraySymType.setDim(arraySymType.getDim() + v.getDeclaratorId().getDimList().size());
        } else {
          simpleType = new SymTypeArray(new OOTypeSymbolLoader(v.getDeclaratorId().getName(), v.getDeclaratorId().getEnclosingScope()),
                  v.getDeclaratorId().getDimList().size(), simpleType);
        }
      }
      v.getDeclaratorId().getSymbol().setType(simpleType);
      symbols.add(v.getDeclaratorId().getSymbol());
    }
    addModifiersToVariables(symbols, ast.getMCModifierList());
  }

  protected void addModifiersToVariables(List<VarDeclSymbol> symbols, Iterable<? extends ASTMCModifier> modifiers) {
    for (VarDeclSymbol symbol : symbols) {
      for (ASTMCModifier modifier : modifiers) {
        if (modifier instanceof ASTJavaModifier) {
          // visibility
          switch (((ASTJavaModifier) modifier).getModifier()) {
            case PUBLIC:
              symbol.setIsPublic(true);
              break;
            case PROTECTED:
              symbol.setIsProtected(true);
              break;
            case PRIVATE:
              symbol.setIsPrivate(true);
              // other variable modifiers as in jls7 8.3.1 Field Modifiers
              break;
            case STATIC:
              symbol.setIsStatic(true);
              break;
            case FINAL:
              symbol.setIsFinal(true);
              break;
            default:
              break;
          }
        }
      }
    }
  }

  private SymTypeExpression createTypeLoader(ASTMCType ast) {
    SynthesizeSymTypeFromMCFullGenericTypes syn = new SynthesizeSymTypeFromMCFullGenericTypes();
    // Start visitor and set enclosingScope
    ast.accept(getRealThis());
    ast.accept(syn);
    return syn.getResult().orElse(new SymTypeOfNull());
  }

}
