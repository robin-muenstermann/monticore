${tc.signature("attributeName", "referencedSymbol", "simpleSymbolName", "scopeInterface")}
    java.util.List<Optional<${referencedSymbol}>> temp = new java.util.ArrayList<>();
    if (this.${attributeName}.isEmpty() && this.enclosingScope.isPresent()) {
      ${attributeName}.clear();
      for (String element : this.${attributeName?remove_ending("Symbol")}) {
        //add the string to Symbol match to the map
        ${attributeName}.put(element, ((${scopeInterface}) this.enclosingScope.get()).resolve${simpleSymbolName}(element));
        //create the returned list
        temp.add(${attributeName}.get(element));
      }
    }else if(enclosingScope.isPresent()){
      for (String element : this.${attributeName?remove_ending("Symbol")}) {
        //create the returned list, because the names list has not changed
        temp.add(${attributeName}.get(element));
      }
    }
    return temp;