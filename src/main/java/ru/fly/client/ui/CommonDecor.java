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

package ru.fly.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * User: fil
 * Date: 10.09.13
 * Time: 17:51
 */
public class CommonDecor {

    public interface Resources extends ClientBundle {

        @Source("common.css")
        Styles css();

    }

    @CssResource.Shared
    public interface Styles extends CssResource {

        String pColorBackground();

        String font();

        String over();

        String selected();

        String disabled();

        String tooltip();

        String disableTextSelection();

        String focused();
    }

    private final Resources res = GWT.create(Resources.class);

    public CommonDecor(){
        res.css().ensureInjected();
    }

    public Styles css(){
        return res.css();
    }

}
