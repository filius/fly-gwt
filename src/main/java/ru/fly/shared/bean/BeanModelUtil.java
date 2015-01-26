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

import com.google.gwt.core.shared.GWT;
import ru.fly.client.F;


/** . */
public abstract class BeanModelUtil {
    
    private static BeanFactory factory = GWT.create(BeanFactory.class);
    
    public static BeanModel createBean(BaseModel object){
        return factory.create(object);
    }

    public static<T extends BaseModel> T clone(T from, T to){
        if(!F.isAssignable(to.getClass(), from.getClass())){
            throw new IllegalArgumentException("Source object class not assignable destination object class from="
                    +from.getClass().getName()+" to="+to.getClass().getName());
        }
        BeanModel srcModel = createBean(from);
        BeanModel destModel = createBean(to);
        for(String fld : srcModel.getFields()){
            destModel.set(fld, srcModel.get(fld));
        }
        return to;
    }
}