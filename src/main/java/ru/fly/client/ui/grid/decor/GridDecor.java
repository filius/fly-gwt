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

package ru.fly.client.ui.grid.decor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import ru.fly.client.ui.CommonDecor;

/**
 * User: fil
 * Date: 01.09.13
 * Time: 21:38
 */
public class GridDecor {

    public static interface Resources extends ClientBundle{

        @Source({"ru/fly/client/ui/common.css", "grid.css"})
        public Styles css();

        @Source("down-arrow.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
        ImageResource downArrow();

        @Source("up-arrow.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
        ImageResource upArrow();

        @Source("nav_first.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
        ImageResource navFirst();

        @Source("nav_previous.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
        ImageResource navPrev();

        @Source("nav_next.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
        ImageResource navNext();

        @Source("nav_last.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
        ImageResource navLast();

    }

    public static interface Styles extends CommonDecor.Styles {

        int pGridRowHeight();

        int pGridRowBorderBottom();

        @ClassName("strip")
        String strip();

        @ClassName("grid")
        String grid();

        @ClassName("grid-hdr")
        String gridHdr();

        @ClassName("grid-hdr-col")
        String gridHdrCol();

        @ClassName("grid-view")
        String gridView();

        @ClassName("grid-view-lazy")
        String gridViewLazy();

        @ClassName("grid-view-col")
        String gridViewCol();

        @ClassName("grid-view-row")
        String gridViewRow();

        @ClassName("grid-lazy-load-mask")
        String gridLazyLoadMask();

        String order();

        String asc();

        String desc();

        String split();

        String dragLine();
    }

    public final Resources res = GWT.create(Resources.class);

    public GridDecor(){
        res.css().ensureInjected();
    }

    public Styles css(){
        return res.css();
    }

}
