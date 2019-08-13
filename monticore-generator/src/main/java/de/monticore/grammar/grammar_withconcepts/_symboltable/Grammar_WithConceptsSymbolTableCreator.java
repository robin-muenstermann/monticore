/* (c) https://github.com/MontiCore/monticore */
/* generated by template symboltable.SymbolTableCreator*/




package de.monticore.grammar.grammar_withconcepts._symboltable;

import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Optional;

public class Grammar_WithConceptsSymbolTableCreator extends Grammar_WithConceptsSymbolTableCreatorTOP {

  public Grammar_WithConceptsSymbolTableCreator(IGrammar_WithConceptsScope enclosingScope) {
    super(enclosingScope);
  }

  public Grammar_WithConceptsSymbolTableCreator(Deque<? extends IGrammar_WithConceptsScope> scopeStack) {
    super(scopeStack);
  }

  /**
    * Creates the symbol table starting from the <code>rootNode</code> and
    * returns the first scope that was created.
    *
    * @param rootNode the root node
    * @return the first scope that was created
    */
  public Grammar_WithConceptsArtifactScope createFromAST(de.monticore.grammar.grammar._ast.ASTMCGrammar rootNode) {
    Log.errorIfNull(rootNode, "0xA7004x630 Error by creating of the Grammar_WithConceptsSymbolTableCreator symbol table: top ast node is null");
    Grammar_WithConceptsArtifactScope artifactScope = new Grammar_WithConceptsArtifactScope(Optional.empty(), Names.getQualifiedName(rootNode.getPackageList()), new ArrayList<>());
    putOnStack(artifactScope);
    rootNode.accept(getRealThis());
    return artifactScope;
  }


}
