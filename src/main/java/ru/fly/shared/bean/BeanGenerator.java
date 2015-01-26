/*
 * Copyright 2015 Valeriy Filatov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package ru.fly.shared.bean;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

public class BeanGenerator extends Generator {

    private String beanModelName = BeanModel.class.getSimpleName();
    
    private TypeOracle oracle;
    private List<JClassType> beans = new ArrayList<JClassType>();
    private JClassType beanType;
    
    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) 
            throws UnableToCompleteException {
        oracle = context.getTypeOracle();        
        beanType = oracle.findType(BaseModel.class.getName());
        
        JClassType[] types = oracle.getTypes();
        for (JClassType type : types) {
            if (type.isAssignableTo(beanType)) {
                beans.add(type);
            }
        }
        final String genPackageName = BeanGenerator.class.getPackage().getName();
        final String genClassName = "BeanFactoryImpl";
        
        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(genPackageName, genClassName);
        composer.setSuperclass(BeanFactory.class.getCanonicalName());
        
        PrintWriter pw = context.tryCreate(logger, genPackageName, genClassName);
        if (pw != null) {
            SourceWriter sw = composer.createSourceWriter(context, pw);
            sw.println("public "+beanModelName+" create(Object obj) {");
            sw.println("String className = obj.getClass().getName();");
            for(JClassType bean : beans){
                sw.println("if(className.equals(\""+bean.getQualifiedSourceName()+"\")){");
                sw.println("return ("+beanModelName+") new "+createBean(bean,logger,context)+"(obj);");
                sw.println("}");
            }
            sw.println("return null;");
            sw.println("}");
            sw.commit(logger);
        }
        
        return composer.getCreatedClassName();
    }
    
    protected String createBean(JClassType bean, TreeLogger logger, GeneratorContext context){
        final String genPackageName = bean.getPackage().getName();
        final String genClassName = beanModelName+"_" + bean.getQualifiedSourceName().replace(".", "_");
        
        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(genPackageName, genClassName);
        composer.setSuperclass(BeanModel.class.getCanonicalName());
        
        PrintWriter pw = context.tryCreate(logger, genPackageName, genClassName);
        if (pw != null) {
            SourceWriter sw = composer.createSourceWriter(context, pw);
            sw.println("private Object obj;");
            sw.println("private java.util.List<String> fields = new java.util.ArrayList<String>();");
            
            sw.println("public " + genClassName + "(Object obj){");
            sw.println("  this.obj = obj;");
            List<String> fields = findFields(bean);
            for(String fld : fields){
                sw.println("  fields.add(\""+fld+"\");");
            }
            sw.println("}");

            sw.println("public java.util.List<String> getFields(){ return fields; }");

            List<JMethod> getters = findGetters(bean);
            sw.println("public Object get(String field){");
            for(JMethod method : getters){
                String f = method.getName();
                if(f.startsWith("get")){
                    f=f.replaceFirst("get", "");
                }else if(f.startsWith("is")){
                    f=f.replaceFirst("is", "");
                }
                f=f.substring(0,1).toLowerCase()+f.substring(1);
                sw.println("  if(field.equals(\""+f+"\"))");
                sw.println("    return (("+bean.getQualifiedSourceName()+")obj)."+method.getName()+"();");

                JClassType retType = method.getReturnType().isClass();
                if(retType != null && retType.isAssignableTo(beanType)){
                    buildGettersForSubBean(sw,bean,retType,f,method.getName()+"()", 0);
                }
            }
            sw.println("return null;");
            sw.println("}");

            List<JMethod> setters = findSetters(bean);
            sw.println("public void set(String field, Object value){");
            for(JMethod method : setters){
                String f = method.getName().replaceFirst("set", "");;
                f=f.substring(0,1).toLowerCase()+f.substring(1);
                sw.println("  if(field.equals(\""+f+"\"))");
                String castType = method.getParameters()[0].getType().getQualifiedSourceName();
                if(!castType.contains("."))
                    castType = castType.substring(0,1).toUpperCase() + castType.substring(1);
                sw.println("    (("+bean.getQualifiedSourceName()+")obj)."+method.getName()+"(("+castType+")value);");
            }
            sw.println("}");
            
            sw.commit(logger);
        }
        
        return composer.getCreatedClassName();
    }

    public void buildGettersForSubBean(SourceWriter sw, JClassType parentBean, JClassType bean, String filedName, String methodName, int level){
        List<JMethod> getters = findGetters(bean);
        for(JMethod method : getters){
            String f = method.getName();
            if(f.startsWith("get")){
                f=f.replaceFirst("get", "");
            }else if(f.startsWith("is")){
                f=f.replaceFirst("is", "");
            }
            f=f.substring(0,1).toLowerCase()+f.substring(1);
            sw.println("if(field.equals(\""+filedName+"."+f+"\"))");
            sw.println("return (("+parentBean.getQualifiedSourceName()+")obj)."+methodName+"."+method.getName()+"();");

            JClassType retType = method.getReturnType().isClass();
            if(retType != null && retType.isAssignableTo(beanType) && level < 4){
                buildGettersForSubBean(sw,parentBean,retType,filedName+"."+f,methodName+"."+method.getName()+"()", level+1);
            }
        }
    }

    protected List<String> findFields(JClassType cls) {
        Set<String> getters = new HashSet<String>();
        Set<String> setters = new HashSet<String>();
        addField(cls, getters, setters);
        List<String> ret = new ArrayList<String>();
        for(String fld : getters){
            if(setters.contains(fld))
                ret.add(fld);
        }
        return ret;
    }

    protected void addField(JClassType cls, Set<String> getters, Set<String> setters) {
        if (cls == null) {
            return;
        }
        // ignore methods of Object
        if (cls.isInterface() != null || cls.getSuperclass() != null) {
            addField(cls.getSuperclass(), getters, setters);
            for (JMethod m : cls.getMethods()) {
                if (m.isPublic() || m.isProtected()) {
                    String name = m.getName();
                    if ((name.matches("get.*") || name.matches("is.*")) && m.getParameters().length == 0) {
                        if(name.startsWith("get"))
                            name = name.replaceFirst("get","");
                        else if(name.startsWith("is"))
                            name = name.replaceFirst("is","");
                        getters.add(name.substring(0,1).toLowerCase()+name.substring(1));
                    }else if (name.matches("set.*") && m.getParameters().length == 1) {
                        name = name.replaceFirst("set","");
                        setters.add(name.substring(0,1).toLowerCase()+name.substring(1));
                    }
                }
            }
        }
    }
    
    protected List<JMethod> findGetters(JClassType cls) {
        List<JMethod> methods = new ArrayList<JMethod>();
        addGetters(cls, methods);
        return methods;
    }

    protected void addGetters(JClassType cls, List<JMethod> methods) {
        if (cls == null) {
            return;
        }
        // ignore methods of Object
        if (cls.isInterface() != null || cls.getSuperclass() != null) {
            addGetters(cls.getSuperclass(), methods);
            for (JMethod m : cls.getMethods()) {
                if (m.isPublic() || m.isProtected()) {
                    String name = m.getName();
                    if ((name.matches("get.*") || name.matches("is.*")) && m.getParameters().length == 0) {
                        methods.add(m);
                    }
                }
            }
        }
    }

    protected List<JMethod> findSetters(JClassType cls) {
        List<JMethod> methods = new ArrayList<JMethod>();
        addSetters(cls, methods);
        return methods;
    }

    protected void addSetters(JClassType cls, List<JMethod> methods) {
        if (cls == null) {
            return;
        }
        if (cls.isInterface() != null || cls.getSuperclass() != null) {
            addSetters(cls.getSuperclass(), methods);
            for (JMethod m : cls.getMethods()) {
                if (m.isPublic() || m.isProtected()) {
                    String name = m.getName();
                    if (name.matches("set.*") && m.getParameters().length == 1) {
                        methods.add(m);
                    }
                }
            }
        }

    }


}
