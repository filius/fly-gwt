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

package ru.fly.client.ui.panel.fieldset;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import ru.fly.client.ui.CommonDecor;

/**
 * Created by fil on 06.01.14.
 */
public class FieldSetDecor {

    public static interface Resources extends ClientBundle {

        @Source({"ru/fly/client/ui/common.css", "fieldset.css"})
        public Styles css();

    }

    @CssResource.Shared
    public static interface Styles extends CommonDecor.Styles {
        String fieldset();
    }

    public final Resources res = GWT.create(Resources.class);

    public FieldSetDecor(){
        res.css().ensureInjected();
    }

    public Styles css(){
        return res.css();
    }

}
