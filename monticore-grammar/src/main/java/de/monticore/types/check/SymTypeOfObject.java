/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.types.typesymbols._symboltable.OOTypeSymbolLoader;

/**
 * An objectType is a full qualified class name.
 * Therefore, we have the fullName, the baseName and the
 * Symbol behind that full qualified class name to retrieve
 */
public class SymTypeOfObject extends SymTypeExpression {

  /**
   * Constructor: with a TypeSymbolLoader that contains the name and enclosingScope
   */
  public SymTypeOfObject(OOTypeSymbolLoader typeSymbolLoader)
  {
    this.typeSymbolLoader = typeSymbolLoader;
  }

  public String getObjName() {
    return typeSymbolLoader.getName();
  }
  
  public void setObjName(String objname) {
    this.typeSymbolLoader.setName(objname);
  }
  
  /**
   * print: Umwandlung in einen kompakten String
   */
  @Override
  public String print() {
    return getObjName();
  }
  
  /**
   * printAsJson: Umwandlung in einen kompakten Json String
   */
  protected String printAsJson() {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    // Care: the following String needs to be adapted if the package was renamed
    jp.member(JsonDeSers.KIND, "de.monticore.types.check.SymTypeOfObject");
    jp.member("objName", getObjName());
    jp.endObject();
    return jp.getContent();
  }

  @Override
  public SymTypeOfObject deepClone() {
    return  new SymTypeOfObject(new OOTypeSymbolLoader(typeSymbolLoader.getName(), typeSymbolLoader.getEnclosingScope()));
  }

  /**
   * getFullName: get the Qualified Name including Package
   */
  public String getFullName() {
    return getObjName();
  }
  
  /**
   * getBaseName: get the unqualified Name (no ., no Package)
   */
  public String getBaseName() {
    String[] parts = getObjName().split("\\.");
    return parts[parts.length - 1];
  }

  @Override
  public boolean isObjectType() {
    return true;
  }


  @Override
  public boolean deepEquals(SymTypeExpression sym){
    if(!(sym instanceof SymTypeOfObject)){
      return false;
    }
    SymTypeOfObject symCon = (SymTypeOfObject) sym;
    if(this.typeSymbolLoader== null ||symCon.typeSymbolLoader==null){
      return false;
    }
    if(!this.typeSymbolLoader.getEnclosingScope().equals(symCon.typeSymbolLoader.getEnclosingScope())){
      return false;
    }
    if(!this.typeSymbolLoader.getName().equals(symCon.typeSymbolLoader.getName())){
      return false;
    }
    return this.print().equals(symCon.print());
  }

  // --------------------------------------------------------------------------
}
