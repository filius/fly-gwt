package ru.fly.client.ui.field.datefield;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import ru.fly.client.event.ChangeEvent;
import ru.fly.client.event.ValueChangeEvent;
import ru.fly.client.ui.FElement;
import ru.fly.client.ui.field.TriggerField;
import ru.fly.client.ui.field.datefield.decor.DateFieldDecor;

import java.util.Date;

/**
 * User: fil
 * Date: 18.10.15
 */
public class MonthPickerField extends TriggerField<Date> {

    private DateFieldDecor decor;
    private MonthPicker monthPicker;
    private DateTimeFormat format;

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
    }

    @Override
    public void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        view.addClassName(decor.css().dateFieldView());
        DOM.setEventListener(view, new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                expander.expandCollapse();
            }
        });
        DOM.sinkEvents(view, Event.ONCLICK);
    }

    @Override
    protected FElement buildTriggerElement() {
        FElement ret = DOM.createDiv().cast();
        ret.addClassName(decor.css().dateFieldTrigger());
        FElement trIcon = DOM.createDiv().cast();
        ret.appendChild(trIcon);
        trIcon.setClassName(decor.css().dateFieldTriggerIcon());
        return ret;
    }

    @Override
    protected FElement getExpandedElement() {
        return getMonthPicker().getElement().cast();
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
                    expander.collapse();
                }
            });
        }
        return monthPicker;
    }

    protected void onCollapse() {
        RootPanel.get().remove(getMonthPicker());
    }

    @Override
    public void setValue(Date value) {
        Date old = getValue();
        super.setValue(value);
        if (view != null) {
            if (value == null)
                view.setInnerHTML("");
            else
                view.setInnerHTML(format.format(value));
        }
        if ((old != null && !old.equals(value)) || (value != null && !value.equals(old))) {
            fireEvent(new ChangeEvent<Date>(value));
        }
    }
}
