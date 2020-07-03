/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.basictypesymbols._symboltable.FunctionSymbol;
import de.monticore.types.basictypesymbols._symboltable.TypeSymbol;
import de.monticore.types.basictypesymbols._symboltable.TypeVarSymbol;
import de.monticore.types.basictypesymbols._symboltable.VariableSymbol;
import de.monticore.types.typesymbols.TypeSymbolsMill;
import de.monticore.types.typesymbols._symboltable.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DefsTypeBasic offers one Symbol-Infrastructure
 * including Scopes etc. that is used to provide relevant Symbols.
 *
 * This infrastructure cn also be used for testing, but is basically meant for
 * shipping along with the appropriate Types grammar:
 *    types/ MCBasicTypes.mc4
 */

public class DefsTypeBasic {

  // Original initialization
  static {
    setup();
  }
  
  /**
   * Initialization of the structure (can be called again to reinitialize).
   * Setup comes in two phases:
   * a) Build the new objects
   * b) add the linkage (and that is only necessary with complex objects
   */
  public static void setup() {
    // Phase A ----- create objects
    set_Void();  // only A
    set_Null();  // only A
    set_thePrimitives();   // only A
    set_array();
    set_String();
    set_Object();
    // Phase B ---- links
    link_array();
    link_String();
    link_Object();

    // TODO: A SymbolTable Structure that correctly reflects
    // the above objects could be helpful?
  }
  
  
  /*********************************************************************/
  
  /**
   * Helpers that efficiently create Symbols
   * (which by the way can also later be extended)
   */
  public static OOTypeSymbol type(String name) {
    return type(name,name);
  }
  
  /** create TypeSymbols (some defaults apply)
   */
  public static OOTypeSymbol type(String name, String fullName) {
    return TypeSymbolsMill.oOTypeSymbolBuilder()
            .setSpannedScope(TypeSymbolsMill.typeSymbolsScopeBuilder().build())
            .setName(name)
            .setFullName(fullName)
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setTypeParameterList(new ArrayList<>())
            .setFieldList(new ArrayList<>())
            .setMethodList(new ArrayList<>())
            .build();
  }

  public static OOTypeSymbol type(String name, List<SymTypeExpression> superTypes){
    return TypeSymbolsMill.oOTypeSymbolBuilder()
            .setSpannedScope(TypeSymbolsMill.typeSymbolsScopeBuilder().build())
            .setName(name)
            .setFullName(name)
            .setSuperTypeList(superTypes)
            .build();
  }

  public static OOTypeSymbol type(String name, List<SymTypeExpression> superTypes, List<TypeVarSymbol> typeArguments){
    TypeSymbolsScope spannedScope = TypeSymbolsMill.typeSymbolsScopeBuilder().build();
    return TypeSymbolsMill.oOTypeSymbolBuilder()
            .setSpannedScope(spannedScope)
            .setName(name)
            .setFullName(name)
            .setSuperTypeList(superTypes)
            .setTypeParameterList(typeArguments)
            .build();
  }

  public static OOTypeSymbol type(String name, List<MethodSymbol> methodList, List<FieldSymbol> fieldList,
                                  List<SymTypeExpression> superTypeList, List<TypeVarSymbol> typeVariableList){
    return TypeSymbolsMill.oOTypeSymbolBuilder()
          .setSpannedScope(TypeSymbolsMill.typeSymbolsScopeBuilder().build())
          .setName(name)
          .setFullName(name)
          .setTypeParameterList(typeVariableList)
          .setSuperTypeList(superTypeList)
          .setMethodList(methodList)
          .setFieldList(fieldList)
          .build();
  }

  public static OOTypeSymbol type(String name, List<MethodSymbol> methodList, List<FieldSymbol> fieldList,
                                  List<SymTypeExpression> superTypeList, List<TypeVarSymbol> typeVariableList,
                                  ITypeSymbolsScope enclosingScope){
    OOTypeSymbol t = TypeSymbolsMill.oOTypeSymbolBuilder()
        .setEnclosingScope(enclosingScope)
        .setSpannedScope(TypeSymbolsMill.typeSymbolsScopeBuilder().build())
        .setName(name)
        .setFullName(name)
        .setTypeParameterList(typeVariableList)
        .setSuperTypeList(superTypeList)
        .setMethodList(methodList)
        .setFieldList(fieldList)
        .build();

    t.getSpannedScope().setEnclosingScope(enclosingScope);

    for(MethodSymbol method: t.getMethodList()){
      method.getSpannedScope().setEnclosingScope(t.getSpannedScope());
    }
    return t;
  }

  /**
   * create TypeVariableSymbols (some defaults apply)
   */
  public static TypeVarSymbol typeVariable(String name){
    return TypeSymbolsMill.typeVarSymbolBuilder()
        .setName(name)
        .setFullName(name)
        .build();
  }
  
  public static OOTypeSymbol add(OOTypeSymbol t, FieldSymbol f) {
    t.addFieldSymbol(f);
    return t;
  }
  
  public static OOTypeSymbol add(OOTypeSymbol t, MethodSymbol m) {
    t.addMethodSymbol(m);
    return t;
  }
  
  /** create MethodSymbols (some defaults apply)
   */
  public static MethodSymbol method(String name, SymTypeExpression returnType) {
    MethodSymbol m = TypeSymbolsMill.methodSymbolBuilder()
            .setSpannedScope(TypeSymbolsMill.typeSymbolsScopeBuilder().build())
            .setName(name)
            .setFullName(name)  // can later be adapted, when fullname of Type is known
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setReturnType(returnType)
            .build();
    m.setSpannedScope(TypeSymbolsMill.typeSymbolsScopeBuilder().build());
    return m;
  }
  
  public static MethodSymbol add(MethodSymbol m, FieldSymbol f) {
    f.setIsParameter(true);
    m.getSpannedScope().add(f);
    return m;
  }

  /** create FieldSymbols (some defaults apply)
   */
  public static FieldSymbol field(String name, SymTypeExpression type) {
    return TypeSymbolsMill.fieldSymbolBuilder()
            .setName(name)
            .setFullName(name)  // can later be adapted, when fullname of Type is known
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setType(type)
            .build();
  }
  
  /** add a Type to a Scope (bidirectional)
   */
  public static void add2scope(ITypeSymbolsScope p, OOTypeSymbol s) {
    s.setEnclosingScope(p);
    p.add(s);
  }
  
  /** add a Filed (e.g. a Variable) to a Scope (bidirectional)
   */
  public static void add2scope(ITypeSymbolsScope p, FieldSymbol s) {
    s.setEnclosingScope(p);
    p.add(s);
  }

  /** add a Method to a Scope (bidirectional)
   */
  public static void add2scope(ITypeSymbolsScope p, MethodSymbol s){
    s.setEnclosingScope(p);
    p.add(s);
  }

  /**
   * add a Method to a Scope (bidirectional)
   */
  public static void add2scope(ITypeSymbolsScope p, TypeVarSymbol s) {
    s.setEnclosingScope(p);
    p.add(s);
  }


  /**
   * It is tedious to allways add name and fullNamee individually:
   * So this functions does that for Types,Methods,Fields afterwards
   * (only the Type needs a full name, the rest is added)
   */
  public static void completeFullnames(OOTypeSymbol s) {
    // in the class Fullname must already set
    String prefix = s.getPackageName();
    for (MethodSymbol m : s.getMethodList()) {
      completeFullnames(m, prefix);
    }
    for (FieldSymbol f : s.getFieldList()) {
      completeFullnames(f, prefix);
    }
  }
  public static void completeFullnames(MethodSymbol s, String prefix) {
    if(s.getFullName() == null || !s.getFullName().contains(".")) {
      s.setFullName(prefix + "." + s.getName());
    }
  }
  public static void completeFullnames(FieldSymbol s, String prefix) {
    if(s.getFullName() == null || !s.getFullName().contains(".")) {
      s.setFullName(prefix + "." + s.getName());
    }
  }
  /*********************************************************************/

  /*********************************************************************/
  
  /**
   * This is a predefined Symbol describing the
   * externally accessible Fields, Methods, etc. of an      array
   *
   * We deliberately choose Java 10 here and take methods from:
   * https://docs.oracle.com/javase/10/docs/api/java/util/Arrays.html
   *
   * TODO RE, ggf. mit ND zusammen
   * TODO RE,AB: 1) Besseres Verfahren überlegen, wie man Vordefinierte Signaturen einliest
   *          (zB durch handgeschriebene Symtabs, die man der RTE beilegt,
   *          oder auch doch durch Java Code wie den hier?)
   *          Das untenstehende beispiel ist grausam schlecht)
   *
   * TODO RE: 2) Die Liste ist unvollständig --> irgendwie vervollstänndigen
   *       wenige übersetzte beispiele:
   *           int i[]; i.length; i.toString(); i.equals(Object i); i.wait(long 3,int 3);
   *
   * TODO RE,AB: (mittelfristig) 2) Konfigurierbarkeit ist zB notwendig
   *        mit der Target-Java-Version (denn die Checks
   *        müssen nicht nach Java 10 gehen, sondern evtl. eine frühere Version ...)
   *
   */
  public static OOTypeSymbol _array;
  // SymTypeExpression _arraySymType  cannot be defined, because Arrays are generics and have varying arguments
  
  public static void set_array() {
    _array = type("ArrayType");
  }
  
  public static void link_array() {
     // TODO: Offen, Array ist eigentlich generisch?
     // zur Zeit aber, leerer Typparameter: .setTypeParameter(new ArrayList<>());
     // die aktuell gelisteten Methoden haben auch keine generischen argumente

    MethodSymbol m; FieldSymbol f;

    // toString()
    add(_array, method("toString", _StringSymType))
            .setFullName("java.lang.Object.toString");

    // equals(Object)
    m = method("equals", _booleanSymType);
    m.setFullName("java.lang.Object.equals");
    add(m, field("o", _ObjectSymType));
    add(_array, m);

    // length field
    add(_array, field("length", _intSymType));

    // TODO RE: this function is very incomplete; ersetzen oder komplettieren

    completeFullnames(_array);

  }
  
  /*********************************************************************/
  
  /**
   * This is a predefined Symbol describing the
   * externally accessible Fields, Methods, etc. of a String
   *
   * We deliberately choose Java 10 here and take methods from:
   * https://docs.oracle.com/javase/10/docs/api/java/lang/String.html
   */
  public static OOTypeSymbol _String;
  public static SymTypeOfObject _StringSymType;
  
  public static void set_String() {
    _String = type("String");
    _StringSymType = new SymTypeOfObject(new OOTypeSymbolLoader("String", createScopeWithString()));
  }

  public static ITypeSymbolsScope createScopeWithString() {
    TypeSymbolsScope typeSymbolsScope = new TypeSymbolsScope();
    typeSymbolsScope.add(_String);
    return typeSymbolsScope;
  }

  public static void link_String() {
    MethodSymbol m; FieldSymbol f;
    TypeSymbolsScope scope = TypeSymbolsMill.typeSymbolsScopeBuilder().build();
    
    // hashCode()
    add(_String, method("hashCode", _intSymType));
    scope.add(method("hashCode",_intSymType));

    // equals(Object)
    m = method("equals", _booleanSymType);
    m.setFullName("java.lang.Object.equals");
    add(m, field("o", _ObjectSymType));
    add(_String, m);
    scope.add(m);
  
    // indexOf(String str, int fromIndex)
    m = method("indexOf", _intSymType);
    add(m, field("str", _StringSymType));
    add(m, field("fromIndex", _intSymType));
    add(_String, m);
    scope.add(m);

    // toString()
    m = method("toString",_StringSymType);
    add(_String, m);
    scope.add(m);

    // charAt(int index)
    m = method("charAt",_charSymType);
    add(m,field("index",_intSymType));
    add(_String, m);
    scope.add(m);
    
    // TODO RE: this function is very incomplete (wegen der fehlenden Signatur); ersetzen oder komplettieren
    _String.setSpannedScope(scope);
    completeFullnames(_String);
  }
  
  /*********************************************************************/
  
  /**
   * This is a predefined Symbol describing the
   * externally accessible Fields, Methods, etc. of a Object
   *
   * We deliberately choose Java 10 here and take methods from:
   * https://docs.oracle.com/javase/10/docs/api/java/lang/Object.html
   */
  public static OOTypeSymbol _Object;
  public static SymTypeOfObject _ObjectSymType;
  
  public static void set_Object() {
    _Object = type("ObjectType");
  }
  
  public static void link_Object() {
    MethodSymbol m; FieldSymbol f;
    
    // equals(Object)
    m = method("equals", _booleanSymType);
    m.setFullName("equals");
    add(m, field("o", _ObjectSymType));
    add(_Object, m);
    
    // TODO RE: this function is very incomplete; ersetzen oder komplettieren
    
    completeFullnames(_Object);
    _ObjectSymType = new SymTypeOfObject(new OOTypeSymbolLoader("Object", createScopeWithObject()));
  }

  public static ITypeSymbolsScope createScopeWithObject() {
    TypeSymbolsScope typeSymbolsScope = new TypeSymbolsScope();
    typeSymbolsScope.add(_Object);
    return typeSymbolsScope;
  }



  /*********************************************************************/
  
  /**
   * This is the predefined Symbol for all Primitives, such as "int"
   * which has empty Fields and Methods
   */
  public static OOTypeSymbol _int;
  public static SymTypeConstant _intSymType;
  public static OOTypeSymbol _char;
  public static SymTypeConstant _charSymType;
  public static OOTypeSymbol _boolean;
  public static SymTypeConstant _booleanSymType;
  public static OOTypeSymbol _double;
  public static SymTypeConstant _doubleSymType;
  public static OOTypeSymbol _float;
  public static SymTypeConstant _floatSymType;
  public static OOTypeSymbol _long;
  public static SymTypeConstant _longSymType;
  public static OOTypeSymbol _byte;
  public static SymTypeConstant _byteSymType;
  public static OOTypeSymbol _short;
  public static SymTypeConstant _shortSymType;
  
  
  public static Map<String,SymTypeConstant> typeConstants;

  public static void set_thePrimitives() {
    typeConstants = new HashMap<>();
    TypeSymbolsScope typeSymbolsScope = new TypeSymbolsScope();
    _int = type("int");
    typeSymbolsScope.add(_int);
    _intSymType = new SymTypeConstant(new OOTypeSymbolLoader("int", typeSymbolsScope));
    typeConstants.put("int", _intSymType);

    _boolean = type("boolean");
    typeSymbolsScope.add(_boolean);
    _booleanSymType = new SymTypeConstant(new OOTypeSymbolLoader("boolean", typeSymbolsScope));
    typeConstants.put("boolean", _booleanSymType);

    _char = type("char");
    typeSymbolsScope.add(_char);
    _charSymType = new SymTypeConstant(new OOTypeSymbolLoader("char", typeSymbolsScope));
    typeConstants.put("char", _charSymType);

    _double = type("double");
    typeSymbolsScope.add(_double);
    _doubleSymType = new SymTypeConstant(new OOTypeSymbolLoader("double", typeSymbolsScope));
    typeConstants.put("double", _doubleSymType);
    _float = type("float");
    typeSymbolsScope.add(_float);
    _floatSymType = new SymTypeConstant(new OOTypeSymbolLoader("float", typeSymbolsScope));
    typeConstants.put("float", _floatSymType);

    _long = type("long");
    typeSymbolsScope.add(_long);
    _longSymType = new SymTypeConstant(new OOTypeSymbolLoader("long", typeSymbolsScope));
    typeConstants.put("long", _longSymType);

    _byte = type("byte");
    typeSymbolsScope.add(_byte);
    _byteSymType = new SymTypeConstant(new OOTypeSymbolLoader("byte", typeSymbolsScope));
    typeConstants.put("byte", _byteSymType);

    _short = type("short");
    typeSymbolsScope.add(_short);
    _shortSymType = new SymTypeConstant(new OOTypeSymbolLoader("short", typeSymbolsScope));
    typeConstants.put("short", _shortSymType);
  }
  
  /*********************************************************************/

  /**
   * This is a predefined Dummy Symbol mimicking the
   * pseudoType "void" with no Fields, no Methods, etc.
   * It is used for internal derivations, but of course not for real type results
   *
   */
  public static OOTypeSymbol _void;
  public static SymTypeVoid _voidSymType;
  public static final String _voidTypeString = "voidType";
  
  public static void set_Void() {
    _void = type(_voidTypeString);           // the name shouldn't be used
    _void.setEnclosingScope(new TypeSymbolsScope());
    _voidSymType = new SymTypeVoid();
  }
  
  
  /*********************************************************************/

  
  /**
   * This is a predefined Dummy Symbol mimicking the
   * pseudoType "null" with no Fields, no Methods, etc.
   */
  public static OOTypeSymbol _null;
  public static SymTypeOfNull _nullSymType;
  public static final String _nullTypeString = "nullType";
  
  public static void set_Null() {
    _null = type(_nullTypeString);    // and the name shouldn't be used anyway, but it is at DeSer
    _null.setEnclosingScope(new TypeSymbolsScope());
    _nullSymType = new SymTypeOfNull();
  }
  
  /*********************************************************************/
  
  // TODO: diese Klasse etwas testen

  // TODO: diese Objekte realisieren
  
}
