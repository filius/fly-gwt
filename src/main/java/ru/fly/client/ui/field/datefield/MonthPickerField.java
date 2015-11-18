package ru.fly.client.ui.field.datefield;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import ru.fly.client.event.ValueChangeEvent;
import ru.fly.client.ui.FElement;
import ru.fly.client.ui.field.Field;
import ru.fly.client.ui.field.TriggerController;
import ru.fly.client.ui.field.datefield.decor.DateFieldDecor;

import java.util.Date;

/**
 * User: fil
 * Date: 18.10.15
 */
public class MonthPickerField extends Field<Date> {

    private DateFieldDecor decor;
    private TriggerController triggerController;
    private MonthPicker monthPicker;
    private DateTimeFormat format;
    private FElement viewElement;
    private FElement triggerElement;

    public MonthPickerField() {
        this(DateTimeFormat.getFormat("MM.yyyy"));
    }

    public MonthPickerField(DateTimeFormat format) {
        this(GWT.<DateFieldDecor>create(DateFieldDecor.class), format);
    }

    public MonthPickerField(DateFieldDecor decor, DateTimeFormat format) {
        this.decor = decor;
        this.format = format;
        addStyleName(decor.css().dateField());
        setWidth(100);

        viewElement = DOM.createDiv().cast();
        viewElement.addClassName(decor.css().dateFieldView());
        triggerElement = buildTriggerElement();
        triggerController = new TriggerController(this, triggerElement) {
            @Override
            protected FElement getExpandedElement() {
                return getMonthPicker().getElement().cast();
            }

            @Override
            public void onExpand() {
                MonthPickerField.this.onExpand();
            }

            @Override
            public void onCollapse() {
                MonthPickerField.this.onCollapse();
            }

            @Override
            public boolean isEnabled() {
                return MonthPickerField.this.isEnabled();
            }
        };
    }

    @Override
    public void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        getElement().appendChild(viewElement);
        getElement().appendChild(triggerElement);
        DOM.setEventListener(viewElement, new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                triggerController.expandCollapse();
            }
        });
        DOM.sinkEvents(viewElement, Event.ONCLICK);
    }

    protected FElement buildTriggerElement() {
        FElement ret = DOM.createDiv().cast();
        ret.addClassName(decor.css().dateFieldTrigger());
        FElement trIcon = DOM.createDiv().cast();
        ret.appendChild(trIcon);
        trIcon.setClassName(decor.css().dateFieldTriggerIcon());
        return ret;
    }

    protected void onExpand() {
        int left = getAbsoluteLeft() + getWidth() - 130;
        int wndViewHeight = Window.getClientHeight() + Window.getScrollTop();
        getMonthPicker().getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        getMonthPicker().getElement().getStyle().setZIndex(10000);
        RootPanel.get().add(getMonthPicker());
        if (getElement().getAbsoluteTop() + getHeight() + 170 > wndViewHeight) {
            getMonthPicker().getElement().setPosition(left, getAbsoluteTop() - 170);
        } else {
            getMonthPicker().getElement().setPosition(left, getAbsoluteTop() + getHeight() + 2);
        }
        getMonthPicker().setValue(getValue(), false);
    }

    private MonthPicker getMonthPicker() {
        if (monthPicker == null) {
            monthPicker = new MonthPicker() {

            };
            monthPicker.addValueChangeHandler(new ValueChangeEvent.ValueChangeHandler<Date>() {
                @Override
                public void onValueChange(Date object) {
                    MonthPickerField.this.setValue(object);
                    triggerController.collapse();
                }
            });
        }
        return monthPicker;
    }

    protected void onCollapse() {
        RootPanel.get().remove(getMonthPicker());
    }

    @Override
    public boolean setValue(Date value, boolean fire) {
        if (viewElement != null) {
            if (value == null)
                viewElement.setInnerHTML("");
            else
                viewElement.setInnerHTML(format.format(value));
        }
        return super.setValue(value, fire);
    }
}
