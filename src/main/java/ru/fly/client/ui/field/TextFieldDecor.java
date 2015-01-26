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

package ru.fly.client.ui.field;

import com.google.gwt.core.client.GWT;

/**
 * User: fil
 * Date: 10.09.13
 * Time: 17:51
 */
public class TextFieldDecor {

    public static interface Resources extends FieldDecor.Resources {

        @Source({"ru/fly/client/ui/common.css", "field.css", "textfield.css"})
        public CssTF css();

    }

    public static interface CssTF extends FieldDecor.Styles {

        @ClassName("text-field")
        String textField();

    }

    private final Resources res = GWT.create(Resources.class);

    public TextFieldDecor(){
        res.css().ensureInjected();
    }

    public CssTF css(){
        return res.css();
    }

}
