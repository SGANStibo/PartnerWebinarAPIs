package com.webinar.extensionapidemo.editorfield;

import com.stibo.core.domain.Node;
import com.stibo.framework.Plugin;
import com.stibo.portal.api.component.context.ComponentContext;
import com.stibo.portal.api.component.noparameters.NoParameters;
import com.stibo.portal.api.widget.Widget;
import com.stibo.portal.componenttype.field.EditorField;
import com.stibo.portal.componenttype.field.controller.FieldController;
import com.stibo.portal.componenttype.field.controller.store.StoreEvent;
import com.stibo.portal.componenttype.field.controller.store.StoreListener;
import com.stibo.portal.event.change.event.ChangeEvent;
import com.stibo.portal.event.change.listener.ChangeListener;
import com.stibo.portal.widget.element.style.Stylist;
import com.stibo.portal.widget.formfield.FormField;
import com.stibo.portal.widget.panel.flow.FlowPanel;
import com.stibo.portal.widget.popup.notification.Notification;
import com.stibo.portal.widget.text.label.Label;
import com.stibo.portal.widget.text.textbox.TextBox;

@Plugin(id = "RetailProductNameEditorField")
public class RetailProductNameEditorField implements EditorField<NoParameters, Node> {
    @Override
    public Widget createUi(final ComponentContext<NoParameters, Node, FieldController> context) {
        final Node node = context.getSelection();

        FlowPanel panel = new FlowPanel();

        String retailProductNameCurrentValue;
        if (node != null) {
            retailProductNameCurrentValue = node.getValue("RetailProductName").getSimpleValue();
        } else {
            retailProductNameCurrentValue = "";
        }

        final TextBox textBox = new TextBox();
        textBox.setText(retailProductNameCurrentValue);
        panel.add(textBox);
        Stylist.setInlineStyle(textBox, "display", "block");

        final Label errorLabel = new Label();
        panel.add(errorLabel);
        Stylist.setInlineStyle(errorLabel, "display", "none");
        Stylist.setInlineStyle(errorLabel, "color", "red");

        textBox.addChangeListener(new ChangeListener() {
            @Override
            public void onChanged(ChangeEvent event) {
                String retailProductNameNewValue = textBox.getText();
                if(retailProductNameCurrentValue != null && retailProductNameNewValue != null) {
                    context.getController().markDirty(!retailProductNameNewValue.equals(retailProductNameCurrentValue));
                }

                if(node.getValue("IsRetailProduct").getSimpleValue().equals("Yes") || retailProductNameNewValue == null || retailProductNameNewValue.equals("")) {
                    context.getController().markValid(true);
                    Stylist.setInlineStyle(errorLabel, "display", "none");
                    context.getController().markDirty(true);

                } else {
                    errorLabel.setText("Uanble to set RetailProductName - IsRetailProduct for the node is not 'Yes'");
                    Stylist.setInlineStyle(errorLabel, "display", "inline-block");

                    context.getController().markValid(false);
                }
            }
        });

        context.getController().addStoreListener(new StoreListener() {
            @Override
            public void store(StoreEvent event) {
                try {
                    node.getValue("RetailProductName").setSimpleValue(textBox.getText());
                } catch (com.stibo.core.domain.ValidatorException e) {
                    Notification notification = new Notification("Unable to set value ", e.getLocalizedMessage());
                    notification.show();
                }
            }
        });

        return new FormField("Retail Name", panel);
    }

}
