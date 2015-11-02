<#--
***************************************************************************************
Copyright (c) 2015, MontiCore
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software
without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.
***************************************************************************************
-->
<#assign genHelper = glex.getGlobalValue("astHelper")>
  
<#-- Copyright -->
${tc.defineHookPoint("JavaCopyright")}

${tc.signature("ast", "grammarName", "packageURI", "astClasses")}

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EDataType;

public interface ${ast.getName()} extends EPackage {
    // The package name.
    String eNAME = "${grammarName}";
    // The package namespace URI.
    String eNS_URI = "${packageURI}";
    // The package namespace name.
    String eNS_PREFIX = "${grammarName}";
    // The singleton instance of the package.
    ${ast.getName()} eINSTANCE = ${grammarName}PackageImpl.init();
    
  <#--  TODO GV: interfaces, enums -->
  <#list astClasses as astClass>
    int ${astClass?upper_case} = ${astClass?index};
  </#list>
  
   <#-- generate all attributes -->  
  <#list ast.getCDAttributes() as attribute>
    <#if !genHelper.isInherited(attribute)>
  ${tc.include("ast.Attribute", attribute)}
    </#if>
  </#list>
 <#--   ${op.includeTemplates(ePackageIDCalculationMain, ast)}
    ${op.includeTemplates(ePackageMetaObjectGetMethodsMain, ast.getFiles())} -->   
     
    // Returns the factory that creates the instances of the model.
    ${grammarName}Factory get${grammarName}Factory();
    
    <#--  TODO GV: interfaces, enums -->
    <#list astClasses as astClass>
    EClass get${astClass[3..]}();
       <#--  ${op.includeTemplates(ePackageLiteralMain, ast.getFiles())} --> 
    </#list>
     
     /**
     * <!-- begin-user-doc -->
     * Defines literals for the meta objects that represent
     * <ul>
     *   <li>each class,</li>
     *   <li>each feature of each class,</li>
     *   <li>each enum,</li>
     *   <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     */
     interface Literals {
     <#--  TODO GV: interfaces, enums -->
     <#list astClasses as astClass>
       EClass ${astClass[3..]?upper_case} = eINSTANCE.get${astClass[3..]}();
       <#--  ${op.includeTemplates(ePackageLiteralMain, ast.getFiles())} --> 
     </#list>
     }
}