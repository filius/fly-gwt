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

package ru.fly.client.ui.panel.messagebox.decor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import ru.fly.client.ui.CommonDecor;

/**
 * User: fil
 * Date: 09.01.14
 * Time: 19:59
 */
public class MessageBoxDecor {

    public static interface Resources extends ClientBundle {

        @Source({"ru/fly/client/ui/common.css", "messagebox.css"})
        public Styles css();

        @Source("info.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.Horizontal)
        ImageResource info();

        @Source("warning.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
        ImageResource warning();

        @Source("progress.gif")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
        ImageResource progress();

    }

    @CssResource.Shared
    public static interface Styles extends CommonDecor.Styles {

        String mb();

        String header();

        String inner();

        String buttons();

        String modal();

    }

    public final Resources res = GWT.create(Resources.class);

    public MessageBoxDecor(){
        res.css().ensureInjected();
    }

    public Styles css(){
        return res.css();
    }

}
