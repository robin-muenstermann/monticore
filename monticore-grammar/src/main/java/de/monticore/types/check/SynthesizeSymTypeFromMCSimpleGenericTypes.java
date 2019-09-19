// (c) https://github.com/MontiCore/monticore

package de.monticore.types.check;

import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Visitor for Derivation of SymType from MCSimpleGenericTypes
 * i.e. for
 *    types/MCSimpleGenericTypes.mc4
 */
public class SynthesizeSymTypeFromMCSimpleGenericTypes extends  SynthesizeSymTypeFromMCCollectionTypes
                                                    implements MCSimpleGenericTypesVisitor {

  public SynthesizeSymTypeFromMCSimpleGenericTypes() { }
  
  /**
   * Using the visitor functionality to calculate the SymType Expression
   */

  // ----------------------------------------------------------  realThis start
  // setRealThis, getRealThis are necessary to make the visitor compositional
  //
  // (the Vistors are then composed using theRealThis Pattern)
  //
  MCSimpleGenericTypesVisitor realThis = this;
  
  @Override
  public void setRealThis(MCSimpleGenericTypesVisitor realThis) {
    this.realThis = realThis;
    super.realThis = realThis;  // not necessarily needed, but to be safe ...
  }
  
  @Override
  public MCSimpleGenericTypesVisitor getRealThis() {
    return realThis;
  }
  // ---------------------------------------------------------- realThis end
  
  /**
   * Storage in the Visitor: result of the last endVisit
   * is inherited
   * This attribute is synthesized upward.
   */

  /**
   * We use mainly endVisit, because the result is synthesized along the
   * tree, when walking upwards
   */

  public void traverse(ASTMCBasicGenericType genericType) {

    List<SymTypeExpression> arguments = new LinkedList<SymTypeExpression>();
    for(ASTMCTypeArgument arg : genericType.getMCTypeArgumentList()) {
      if(null != arg) {
        arg.accept(getRealThis());
      }

      if(!result.isPresent()) {
        Log.error("0xE9CDA Internal Error: SymType argument missing for generic type. "
                + " Probably TypeCheck mis-configured.");
      }
      arguments.add(result.get());
    }

    SymTypeExpression tex =
            SymTypeExpressionFactory.createGenerics(
                    genericType.getName(), arguments);
    result = Optional.of(tex);

  }
  
}
