/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types;


import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;

/**
 * This class provides methods for printing types as Strings. The TypesPrinter
 * is a singleton.
 */
public class BasicGenericsTypesPrinter extends BasicTypesPrinter {

  private static BasicGenericsTypesPrinter instance;

  /**
   * We have a singleton.
   */
  protected BasicGenericsTypesPrinter() {
  }

  /**
   * Returns the singleton instance.
   *
   * @return The instance.
   */
  private static BasicGenericsTypesPrinter getInstance() {
    if (instance == null) {
      instance = new BasicGenericsTypesPrinter();
    }
    return instance;
  }

  protected String doPrintType(ASTMCType type) {

    IndentPrinter printer = new IndentPrinter();

    MCCollectionTypesPrettyPrinter vi = new MCCollectionTypesPrettyPrinter(printer);
    return vi.prettyprint(type);
  }

}
