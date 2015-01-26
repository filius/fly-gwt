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

import com.google.gwt.dom.client.InputElement;
import ru.fly.client.ui.FElement;

/**
 * User: fil
 * Date: 05.08.13
 * Time: 21:06
 */
public class PasswordField extends TextField {

    protected FElement getInputElement(){
        InputElement inp = super.getInputElement().cast();
        inp.setAttribute("type", "password");
        return inp.cast();
    }
}
