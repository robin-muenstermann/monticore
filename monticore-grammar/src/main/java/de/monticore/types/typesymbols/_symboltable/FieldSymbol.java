/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.typesymbols._symboltable;

public class FieldSymbol extends FieldSymbolTOP {

  public FieldSymbol(String name){
    super(name);
  }

  /**
   * returns a clone of this
   */
  public FieldSymbol deepClone(){
    FieldSymbol clone = new FieldSymbol(name);
    clone.setAccessModifier(this.accessModifier);
    clone.setEnclosingScope(this.enclosingScope);
    clone.setFullName(this.fullName);
    clone.setIsStatic(this.isStatic);
    clone.setIsParameter(this.isParameter);
    if(isPresentAstNode()) {
      clone.setAstNode(this.getAstNode());
    }
    if(getType()!=null){
      clone.setType(this.getType().deepClone());
    }
    return clone;
  }

}
