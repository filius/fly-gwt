package ru.fly.client.ui.multipleupload;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import ru.fly.client.event.ClickEvent;
import ru.fly.client.ui.button.Button;
import ru.fly.client.ui.multipleupload.decor.UploadDecor;
import ru.fly.client.ui.multipleupload.event.AbortEvent;
import ru.fly.client.ui.multipleupload.event.ErrorEvent;
import ru.fly.client.ui.multipleupload.event.LoadEvent;
import ru.fly.client.ui.multipleupload.event.UploadCompleteEvent;
import ru.fly.client.ui.panel.FlowLayout;
import ru.fly.client.ui.panel.messagebox.MessageBox;

import java.util.List;

/**
 * @author fil.
 */
public class MultipleUploadPanel extends FlowLayout implements UploadCompleteEvent.HasUploadCompleteHandler {

    private final UploadDecor uploadDecor = GWT.create(UploadDecor.class);
    private UploadInputElement uploadInputElement;
    private Button selectBtn;
    private String uploadUrl;
//    private boolean appendTimestamp;

    public MultipleUploadPanel() {
        uploadInputElement = new UploadInputElement();
        add(uploadInputElement);
        selectBtn = new Button(uploadDecor.res.upload16(), "Выбрать файл", new ClickEvent.ClickHandler() {
            @Override
            public void onClick() {
                uploadInputElement.click();
            }
        });
    }

    public MultipleUploadPanel(String uploadUrl) {
        this();
        setUploadUrl(uploadUrl);
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

//    public void setAppendTimestamp(boolean appendTimestamp) {
//        this.appendTimestamp = appendTimestamp;
//    }

    public Button getSelectBtn() {
        return selectBtn;
    }

    @Override
    protected void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        uploadInputElement.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                String url = uploadUrl;
//                if (appendTimestamp) {
//                    if (url.contains("?")) {
//                        url += "&t=" + new Date().getTime();
//                    } else {
//                        url += "?t=" + new Date().getTime();
//                    }
//                }
                List<UploadProgressItem> items = uploadInputElement.sendTo(url);
                uploadInputElement.clear();
                for (UploadProgressItem item : items) {
                    final UploadProgressItem uploadItem = item;
                    MultipleUploadPanel.this.add(uploadItem);
                    uploadItem.setLoadHandler(new LoadEvent.LoadHandler() {
                        @Override
                        public void onLoad(Event nativeEvent) {
                            MultipleUploadPanel.this.remove(uploadItem);
                            MultipleUploadPanel.this.fireEvent(new UploadCompleteEvent(uploadItem.getResponseText()));
                        }
                    });
                    uploadItem.setErrorHandler(new ErrorEvent.ErrorHandler() {
                        @Override
                        public void onError(Event nativeEvent) {
                            MessageBox.alert("Ошибка загрузки", "Возникла ошибка во время загрузки: " +
                                    uploadItem.getFileName());
                        }
                    });
                    uploadItem.setAbortHandler(new AbortEvent.AbortHandler() {
                        @Override
                        public void onAbort(Event nativeEvent) {
                            MessageBox.info("Сообщение", "Загрузка прервана для: " + uploadItem.getFileName());
                        }
                    });
                    uploadItem.send();
                }
            }
        });
    }

    @Override
    public HandlerRegistration addUploadCompleteHandler(UploadCompleteEvent.UploadCompleteHandler h) {
        return addHandler(h, UploadCompleteEvent.getType());
    }
}
