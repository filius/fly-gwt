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

package ru.fly.client.log;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * User: fil
 * Date: 01.09.13
 * Time: 21:38
 */
public class LogDecor {

    public static interface Resources extends ClientBundle{

        @Source({"log.css"})
        public CssLog css();

        @Source("error.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
        ImageResource error();

    }

    public static interface CssLog extends CssResource{
        @ClassName("error-wnd")
        String errorWnd();
    }

    public final Resources res = GWT.create(Resources.class);

    public LogDecor(){
        res.css().ensureInjected();
    }

    public CssLog css(){
        return res.css();
    }

}
