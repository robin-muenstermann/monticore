/*******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, 2016, MontiCore, All rights reserved.
 *  
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.se_rwth.langeditor.texteditor.contentassist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IStorage;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import com.google.inject.Inject;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.langeditor.injection.TextEditorScoped;
import de.se_rwth.langeditor.language.Language;
import de.se_rwth.langeditor.modelstates.ModelState;
import de.se_rwth.langeditor.modelstates.ObservableModelStates;

@TextEditorScoped
public class ContentAssistProcessorImpl implements IContentAssistProcessor {
  
  private Language language;
  
  private Optional<ModelState> currentModelState = Optional.empty();
  
  @Inject
  public ContentAssistProcessorImpl(
      IStorage storage,
      ObservableModelStates observableModelStates,
      Language language) {
    this.language = language;
    observableModelStates.getModelStates().stream()
        .filter(modelState -> modelState.getStorage().equals(storage))
        .forEach(this::acceptModelState);
    observableModelStates.addStorageObserver(storage, this::acceptModelState);
  }
  
  @Override
  public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
    String incomplete = getIncomplete(viewer.getDocument(), offset);
    int start = offset - incomplete.length();
    List<ICompletionProposal> proposalList = new ArrayList<>();
    // Add types from symbol table
    if (currentModelState.isPresent()) {
      if (currentModelState.get().getLastLegalRootNode()
          .isPresent()) {
        ASTNode rootNode = currentModelState.get().getLastLegalRootNode().get();
        Optional<GlobalScope> scope = language.getScope(rootNode);
        if (scope.isPresent()) {
          for (String symbol : getSymbols(scope.get())) {
            if (findMatch(incomplete, symbol)) {
              proposalList
                  .add(new CompletionProposal(symbol, start, incomplete.length(),
                      symbol.length()));
            }
          }
        }
      }
    }
    
    // Add keywords
    for (String keyword : language.getKeywords()) {
      if (findMatch(incomplete, keyword)) {
        proposalList
            .add(new CompletionProposal(keyword, start, incomplete.length(), keyword.length()));
      }
    }
    return proposalList.toArray(new ICompletionProposal[0]);
  }
  
  /**
   * TODO: Write me!
   * 
   * @param rootNode
   * @return
   */
  private List<String> getSymbols(Scope rootScope) {
    Collection<String> names = new HashSet<>();
    if (rootScope.exportsSymbols()) {
      for (Symbol symbol : rootScope.getSymbols()) {
        if (language.getCompletionKinds().contains(symbol.getKind())) {
          names.add(symbol.getName());
        }
      }
    }
    List<? extends Scope> subScopes = rootScope.getSubScopes();
    for (Scope scope : subScopes) {
      names.addAll(getSymbols(scope));
    }
    return names.stream().sorted().collect(Collectors.toList());
  }
  
  private void acceptModelState(ModelState modelState) {
    if (modelState.isLegal()) {
      currentModelState = Optional.of(modelState);
    }
  }
  
  private boolean findMatch(String incomplete, String proposal) {
    return proposal.startsWith(incomplete);
  }
  
  @Override
  public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
    return null;
  }
  
  @Override
  public char[] getCompletionProposalAutoActivationCharacters() {
    return null;
  }
  
  @Override
  public char[] getContextInformationAutoActivationCharacters() {
    return null;
  }
  
  @Override
  public String getErrorMessage() {
    return "error during content assist";
  }
  
  @Override
  public IContextInformationValidator getContextInformationValidator() {
    return null;
  }
  
  private String getIncomplete(IDocument document, int offset) {
    try {
      String incompleted = "";
      boolean done = false;
      while (0 < offset && !done) {
        char precedingChar = document.getChar(--offset);
        if ('a' <= precedingChar && precedingChar <= 'z'
            || 'A' <= precedingChar && precedingChar <= 'Z') {
          incompleted = precedingChar + incompleted;
        }
        else {
          done = true;
        }
      }
      return incompleted;
    }
    catch (BadLocationException e) {
      throw new RuntimeException(e);
    }
  }
  
}
